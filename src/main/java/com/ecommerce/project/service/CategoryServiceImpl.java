package com.ecommerce.project.service;
import java.util.List;
import java.util.Optional;
import com.ecommerce.project.Exception.APIException;
import com.ecommerce.project.Exception.ResourceNotFoundException;
import com.ecommerce.project.Payload.CategoryDTO;
import com.ecommerce.project.Payload.CategoryResponse;
import com.ecommerce.project.Repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecommerce.project.model.Category;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories() {
        List<Category> category = categoryRepository.findAll();
        if (category.isEmpty()) {
            throw new APIException("No category Created till now!!!");
        }
        // converting object of Category type to CategoryDTO using ModelMapper class
        List<CategoryDTO> categoryDTOs = category.stream().map(cat -> modelMapper.map(cat, CategoryDTO.class)).toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOs);
        return categoryResponse;
        
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if (savedCategory != null ) {
           throw new APIException("Category with this name : " + category.getCategoryName() + " already exists.");
        }
        Category saved = categoryRepository.save(category);
        return modelMapper.map(saved, CategoryDTO.class);

    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(String.valueOf(categoryId)).orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", categoryId));
        categoryRepository.delete(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Optional<Category> savedCategoriesOptional = categoryRepository.findById(String.valueOf(categoryId));

        Category savedCategory = savedCategoriesOptional.orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", categoryId));
        category.setCategoryId(categoryId);
        savedCategory =  categoryRepository.save(category);
    
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

}
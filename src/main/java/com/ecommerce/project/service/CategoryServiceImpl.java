package com.ecommerce.project.service;
import java.util.List;
import java.util.Optional;
import com.ecommerce.project.Exception.APIException;
import com.ecommerce.project.Exception.ResourceNotFoundException;
import com.ecommerce.project.Payload.CategoryDTO;
import com.ecommerce.project.Payload.CategoryResponse;
import com.ecommerce.project.Repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.ecommerce.project.model.Category;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    /**
     * Fetches categories with pagination and sorting metadata.
     */
    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        logger.debug("Fetching categories pageNumber={}, pageSize={}, sortBy={}, sortOrder={}", pageNumber, pageSize, sortBy, sortOrder);
        // sorting 
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending(): Sort.by(sortBy).descending();
        // pagination
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> category = categoryPage.getContent();
        if (category.isEmpty()) {
            logger.warn("No categories found pageNumber={}, pageSize={}", pageNumber, pageSize);
            throw new APIException("No category Created till now!!!");
        }
        // converting object of Category type to CategoryDTO using ModelMapper class
        List<CategoryDTO> categoryDTOs = category.stream().map(cat -> modelMapper.map(cat, CategoryDTO.class)).toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOs);
        categoryResponse.setPageNumber(categoryPage.getNumber()); //getting page number
        categoryResponse.setPageSize(categoryPage.getSize()); //getting page size
        categoryResponse.setTotalElements(categoryPage.getTotalElements()); //getting total elements
        categoryResponse.setTotalPages(categoryPage.getTotalPages()); //getting total pages
        categoryResponse.setLastPage(categoryPage.isLast());//gettting boolean value

        logger.debug("Fetched {} categories out of totalElements={}", categoryDTOs.size(), categoryPage.getTotalElements());
        return categoryResponse;
        
    }

    /**
     * Creates a category after validating duplicate name constraints.
     */
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        logger.info("Creating category");
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if (savedCategory != null ) {
           logger.warn("Category creation rejected because name already exists categoryName={}", category.getCategoryName());
           throw new APIException("Category with this name : " + category.getCategoryName() + " already exists.");
        }
        Category saved = categoryRepository.save(category);
        logger.info("Category created categoryId={}", saved.getCategoryId());
        return modelMapper.map(saved, CategoryDTO.class);

    }

    /**
     * Deletes a category by id.
     */
    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        logger.info("Deleting category categoryId={}", categoryId);
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", categoryId));
        categoryRepository.delete(category);
        logger.info("Category deleted categoryId={}", categoryId);
        return modelMapper.map(category, CategoryDTO.class);
    }

    /**
     * Updates category details for the given id.
     */
    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        logger.info("Updating category categoryId={}", categoryId);
        Category category = modelMapper.map(categoryDTO, Category.class);
        Optional<Category> savedCategoriesOptional = categoryRepository.findById(categoryId);

        Category savedCategory = savedCategoriesOptional.orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", categoryId));
        category.setCategoryId(categoryId);
        savedCategory =  categoryRepository.save(category);
    
        logger.info("Category updated categoryId={}", categoryId);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

}

package com.ecommerce.project.service;

import java.util.List;
import java.util.Optional;

import com.ecommerce.project.Exception.APIException;
import com.ecommerce.project.Exception.ResourceNotFoundException;
import com.ecommerce.project.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecommerce.project.model.Category;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        List<Category> category = categoryRepository.findAll();
        if (category.isEmpty()) {
            throw new APIException("No category Created till now!!!");
        }
        return category;
        
    }

    @Override
    public void createCategory(Category category) {
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if (savedCategory != null ) {
           throw new APIException("Category with this name : " + category.getCategoryName() + " already exists.");
        }
        categoryRepository.save(category);
        
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(String.valueOf(categoryId)).orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", categoryId));
        categoryRepository.delete(category);
        return "Category Id deleted successfully" + categoryId + " deleted successfully";
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        Optional<Category> savedCategoriesOptional = categoryRepository.findById(String.valueOf(categoryId));

        Category savedCategory = savedCategoriesOptional.orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", categoryId));
        category.setCategoryId(categoryId);
        savedCategory =  categoryRepository.save(category);
        return savedCategory;
    }

}
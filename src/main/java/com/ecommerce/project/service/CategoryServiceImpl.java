package com.ecommerce.project.service;

import java.util.List;
import java.util.Optional;
import com.ecommerce.project.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.ecommerce.project.model.Category;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static Long categoryID = 1l;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(String.valueOf(categoryId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        categoryRepository.delete(category);
        return "Category Id deleted successfully" + categoryId + " deleted successfully";


    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        Optional<Category> savedCategoriesOptional = categoryRepository.findById(String.valueOf(categoryId));

        Category savedCategory = savedCategoriesOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        category.setCategoryId(categoryId);
        savedCategory =  categoryRepository.save(category);
        return savedCategory;
    }

}
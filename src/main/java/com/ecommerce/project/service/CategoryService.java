package com.ecommerce.project.service;
import com.ecommerce.project.Payload.CategoryDTO;
import com.ecommerce.project.Payload.CategoryResponse;

public interface CategoryService {

    /**
     * Returns a paginated category list.
     */
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    /**
     * Creates a new category.
     */
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    /**
     * Deletes category by id.
     */
    CategoryDTO deleteCategory(Long categoryId);

    /**
     * Updates category details by id.
     */
    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
    
} 

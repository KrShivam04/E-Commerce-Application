package com.ecommerce.project.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.project.Config.AppConstants;
import com.ecommerce.project.Payload.CategoryDTO;
import com.ecommerce.project.Payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Returns paginated category list with sorting.
     *
     * @param pageNumber page index starting from 0
     * @param pageSize number of records per page
     * @param sortBy field used for sorting
     * @param sortOrder sort direction (asc/desc)
     * @return paginated category response
     */
    @RequestMapping(value = "public/categories", method = RequestMethod.GET)
    public ResponseEntity<CategoryResponse> getAllCategories(
        @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber, 
        @RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize, 
        @RequestParam(name="sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
        @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ) {
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    /**
     * Creates a new category.
     *
     * @param categoryDTO category payload
     * @return created category
     */
    @PostMapping("public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO categoryDTO2 = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(categoryDTO2, HttpStatus.CREATED);
        
    }

    /**
     * Deletes a category by id.
     *
     * @param categoryId category id
     * @return deleted category details
     */
    @DeleteMapping("admin/category/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){
        CategoryDTO delCategoryDTO = categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(delCategoryDTO, HttpStatus.OK);
  
    }

    /**
     * Updates category details for the given id.
     *
     * @param categoryDTO updated category payload
     * @param categoryId category id to update
     * @return updated category
     */
    @PutMapping("public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO categoryDTO, @PathVariable Long categoryId) {
        CategoryDTO updateCategoryDTO = categoryService.updateCategory(categoryDTO, categoryId);
        return new ResponseEntity<>(updateCategoryDTO, HttpStatus.OK);
        
    }

}

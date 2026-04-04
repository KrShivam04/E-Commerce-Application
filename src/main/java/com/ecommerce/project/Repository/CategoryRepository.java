package com.ecommerce.project.Repository;
import com.ecommerce.project.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds category by exact category name.
     */
    Category findByCategoryName(String categoryName);

    

}

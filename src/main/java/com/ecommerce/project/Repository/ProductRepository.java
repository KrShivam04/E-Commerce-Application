package com.ecommerce.project.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    /**
     * Returns products in category sorted by price ascending.
     */
    Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable pageDetails);

    /**
     * Returns products matching keyword pattern (case insensitive).
     */
    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetails);

}

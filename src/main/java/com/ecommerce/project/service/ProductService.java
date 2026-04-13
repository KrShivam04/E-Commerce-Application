package com.ecommerce.project.service;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import com.ecommerce.project.Payload.ProductDTO;
import com.ecommerce.project.Payload.ProductResponse;

public interface ProductService {
    /**
     * Adds a product to a category.
     */
    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);

    /**
     * Returns paginated products.
     */
    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword, String category);

    /**
     * Returns paginated products for a category.
     */
    ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    /**
     * Returns paginated products filtered by keyword.
     */
    ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    /**
     * Updates a product by id.
     */
    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    /**
     * Deletes a product by id.
     */
    ProductDTO deleteProduct(Long productId);

    /**
     * Updates a product image by id.
     */
    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}

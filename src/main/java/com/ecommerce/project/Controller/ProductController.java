package com.ecommerce.project.Controller;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ecommerce.project.Config.AppConstants;
import com.ecommerce.project.Payload.ProductDTO;
import com.ecommerce.project.Payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    ProductService productService;

    /**
     * Creates a product under the given category.
     *
     * @param productDTO product payload
     * @param categoryId category id for the product
     * @return created product
     */
    @PostMapping("admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long categoryId) {
        logger.info("Adding product to category categoryId={}", categoryId);
        ProductDTO savedproductDTO = productService.addProduct(categoryId, productDTO);
        logger.info("Product added successfully to category categoryId={}", categoryId);
        return new ResponseEntity<>(savedproductDTO, HttpStatus.CREATED);
    }

    /**
     * Returns paginated and sorted product list.
     *
     * @param pageNumber page index
     * @param pageSize page size
     * @param sortBy sort field
     * @param sortOrder sort direction
     * @return paginated product response
     */
    @GetMapping("public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
        @RequestParam(name="keyword", required = false) String keyword,
        @RequestParam(name="category", required = false) String category,
        @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber, 
        @RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize, 
        @RequestParam(name="soryBy", defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortBy, 
        @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_DIR) String sortOrder
    ) {
        logger.debug("Fetching products pageNumber={}, pageSize={}, sortBy={}, sortOrder={}, keyword={}, category={}", pageNumber, pageSize, sortBy, sortOrder, keyword, category);
        ProductResponse productResponse = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder, keyword, category);
        logger.debug("Fetched products pageNumber={}, pageSize={}", pageNumber, pageSize);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    /**
     * Returns products by category with pagination and sorting.
     *
     * @param categoryId category id filter
     * @param pageNumber page index
     * @param pageSize page size
     * @param sortBy sort field
     * @param sortOrder sort direction
     * @return paginated product response
     */
    @GetMapping("public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(
        @PathVariable Long categoryId,
        @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber, 
        @RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize, 
        @RequestParam(name="soryBy", defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortBy, 
        @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_DIR) String sortOrder
    ) {
        logger.debug("Fetching products by categoryId={}, pageNumber={}, pageSize={}", categoryId, pageNumber, pageSize);
        ProductResponse productResponse = productService.searchByCategory(categoryId, pageNumber, pageSize, sortBy, sortOrder);
        logger.debug("Fetched products by categoryId={}", categoryId);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    /**
     * Searches products by keyword with pagination and sorting.
     *
     * @param keyword search keyword
     * @param pageNumber page index
     * @param pageSize page size
     * @param sortBy sort field
     * @param sortOrder sort direction
     * @return product search response
     */
    @GetMapping("public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeywords(
        @PathVariable String keyword,
        @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber, 
        @RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize, 
        @RequestParam(name="soryBy", defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortBy, 
        @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_DIR) String sortOrder
    ) {
        logger.debug("Searching products by keyword={}, pageNumber={}, pageSize={}", keyword, pageNumber, pageSize);
        ProductResponse productResponse = productService.searchProductByKeyword('%' + keyword + '%',  pageNumber, pageSize, sortBy, sortOrder);
        logger.debug("Searched products by keyword={}", keyword);
        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }

    /**
     * Updates product details for a given product id.
     *
     * @param productDTO updated payload
     * @param productId product id
     * @return updated product
     */
    @PutMapping("admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long productId) {
        logger.info("Updating product productId={}", productId);
        ProductDTO updatedProductDTO = productService.updateProduct(productId, productDTO);
        logger.info("Product updated successfully productId={}", productId);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }

    /**
     * Deletes a product by id.
     *
     * @param productId product id
     * @return deleted product details
     */
    @DeleteMapping("admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        logger.info("Deleting product productId={}", productId);
        ProductDTO deletedProduct =  productService.deleteProduct(productId);
        logger.info("Product deleted successfully productId={}", productId);
        return new ResponseEntity<>(deletedProduct, HttpStatus.OK);
    }

    /**
     * Updates image for a given product.
     *
     * @param productId product id
     * @param image new image file
     * @return updated product details
     * @throws IOException when image upload fails
     */
    @PutMapping("products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId, @RequestParam("Image")MultipartFile image) throws IOException{
        logger.info("Updating product image productId={}, originalFilename={}", productId, image.getOriginalFilename());
        ProductDTO updatedProduct = productService.updateProductImage(productId, image);
        logger.info("Product image updated successfully productId={}", productId);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

}
  

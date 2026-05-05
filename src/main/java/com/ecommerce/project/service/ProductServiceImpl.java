package com.ecommerce.project.service;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ecommerce.project.Exception.APIException;
import com.ecommerce.project.Exception.ResourceNotFoundException;
import com.ecommerce.project.Payload.CartDTO;
import com.ecommerce.project.Payload.ProductDTO;
import com.ecommerce.project.Payload.ProductResponse;
import com.ecommerce.project.Repository.CartRepository;
import com.ecommerce.project.Repository.CategoryRepository;
import com.ecommerce.project.Repository.ProductRepository;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import org.springframework.data.domain.Pageable;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Value("${image.base.url}")
    String imageBaseUrl;

    /**
     * Adds a product to a category after checking duplicate product names.
     */
    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        logger.info("Adding product to category categoryId={}", categoryId);
        // check if the category is present or not
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", categoryId));

        Boolean isProductNotPresent = true;
        List<Product> products = category.getProducts();
        for (int i = 0; i<products.size() ; i++) {
            if(products.get(i).getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }

        if (isProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);

            product.setImage("default.png");
            product.setCategory(category);
            double specialPrice =product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            logger.info("Product added productId={} categoryId={}", savedProduct.getProductId(), categoryId);
            return modelMapper.map(savedProduct, ProductDTO.class);
        } else {
            logger.warn("Product creation rejected because product already exists in category categoryId={}, productName={}", categoryId, productDTO.getProductName());
            throw new APIException("Product already exists with :: "+ productDTO.getProductName());
        }
    }

    /**
     * Fetches all products with pagination and sorting.
     */
    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword, String category) {
        logger.debug("Fetching products pageNumber={}, pageSize={}, sortBy={}, sortOrder={}, keyword={}, category={}", pageNumber, pageSize, sortBy, sortOrder, keyword, category);
        // sorting
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending(): Sort.by(sortBy).descending();
        // pagination
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Specification<Product> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%"));
        }

        if (category != null && !category.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("category").get("categoryName"), category));
        }

        Page<Product> pageProducts = productRepository.findAll(spec, pageDetails);

        // product size is 0 or not
        List<Product> products = pageProducts.getContent();
        List<ProductDTO> productDTOs = products.stream().map(product -> {
            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
            productDTO.setImage(constructImageURL(product.getImage()));
            return productDTO;
        }).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        logger.debug("Fetched {} products out of totalElements={}", productDTOs.size(), pageProducts.getTotalElements());
        return productResponse;
    }

    /**
     * Method to construct image url
     */

    private String constructImageURL(String imageName) {

        return imageBaseUrl.endsWith("/") ? imageBaseUrl + imageName : imageBaseUrl + "/" + imageName;
    }

    /**
     * Fetches products for a specific category.
     */
    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        logger.debug("Searching products by categoryId={}, pageNumber={}, pageSize={}", categoryId, pageNumber, pageSize);
        // product size is 0 or not

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", categoryId));

        // sorting
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending(): Sort.by(sortBy).descending();
        // pagination
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);

        // product size is 0 or not
        List<Product> products = pageProducts.getContent();


        if (products.isEmpty()) {
            logger.warn("No products found for categoryId={}", categoryId);
            throw new APIException("Products does not exists with category :: " + categoryId);
        }

        List<ProductDTO> productDTOs = products.stream().map(product-> modelMapper.map(product, ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        logger.debug("Fetched {} products for categoryId={}", productDTOs.size(), categoryId);
        return productResponse;
    }

    /**
     * Fetches products matching a keyword.
     */
    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        logger.debug("Searching products by keyword={}, pageNumber={}, pageSize={}", keyword, pageNumber, pageSize);
        // sorting
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending(): Sort.by(sortBy).descending();
        // pagination
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByProductNameLikeIgnoreCase( keyword ,pageDetails);

        List<Product> products = pageProducts.getContent();

        if (products.isEmpty()) {
            logger.warn("No products found for keyword={}", keyword);
            throw new APIException("Products does not exists with keyword :: " + keyword );
        }

        List<ProductDTO> productDTOs = products.stream().map(
                product-> modelMapper.map(product, ProductDTO.class)
        ).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        logger.debug("Fetched {} products for keyword={}", productDTOs.size(), keyword);
        return productResponse;
    }

    /**
     * Updates product details and refreshes linked cart entries.
     */
    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        logger.info("Updating product productId={}", productId);
        // getting the existing product from the db
        Product productFromDb = productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product", "Product ID", productId));

        Product product = modelMapper.map(productDTO, Product.class);

        // updating the product info with request body
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setSpecialPrice(product.getSpecialPrice());

        Product savedProduct = productRepository.save(productFromDb);

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        logger.debug("Refreshing {} carts after product update productId={}", carts.size(), productId);

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> products = cart.getCartItem().stream().map(p-> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());
            cartDTO.setProducts(products);
            return cartDTO;
        }).collect(Collectors.toList());

        cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

        logger.info("Product updated productId={}", productId);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    /**
     * Deletes a product and removes it from all carts where present.
     */
    @Override
    public ProductDTO deleteProduct(Long productId) {
        logger.info("Deleting product productId={}", productId);
        Product product = productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product", "ProductId", productId));

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        logger.debug("Removing productId={} from {} carts before delete", productId, carts.size());
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

        productRepository.delete(product);
        logger.info("Product deleted productId={}", productId);
        return modelMapper.map(product, ProductDTO.class);
    }

    /**
     * Uploads and updates product image.
     */
    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        logger.info("Updating product image productId={}, originalFilename={}", productId, image.getOriginalFilename());
        // Get the product from the database
        Product productFromDb = productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product", "ProductId", productId));

        // upload the image to server in the folder
        // get the file name of uploaded image

        String fileName = fileService.uploadImage(path, image);

        // updating the new file name to the product
        productFromDb.setImage(fileName);
        // saving the updated product
        Product updatedProduct = productRepository.save(productFromDb);
        // return the ProductDTO class object
        logger.info("Product image updated productId={}, fileName={}", productId, fileName);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

}

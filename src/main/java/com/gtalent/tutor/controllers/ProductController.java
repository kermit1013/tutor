package com.gtalent.tutor.controllers;

import com.gtalent.tutor.models.Product;
import com.gtalent.tutor.models.User;
import com.gtalent.tutor.repositories.ProductRepository;
import com.gtalent.tutor.requests.CreateProductRequest;
import com.gtalent.tutor.requests.CreateUserRequest;
import com.gtalent.tutor.requests.UpdateProductRequest;
import com.gtalent.tutor.requests.UpdateUserRequest;
import com.gtalent.tutor.responses.CreateUserResponse;
import com.gtalent.tutor.responses.GetUserResponse;
import com.gtalent.tutor.responses.ProductResponse;
import com.gtalent.tutor.responses.UpdateUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@CrossOrigin("*")
public class ProductController {
    //Step 1. 建立 Product model
    //Step 2. 建立 Product Repository extends JpaRepository
    //Step 3. 建立 Product CRUD APIs

    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products.stream().map(ProductResponse::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductsById(@PathVariable int id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            ProductResponse response = new ProductResponse(product.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProductsById(@PathVariable int id, @RequestBody UpdateProductRequest request) {
        // 1. 找到user
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()) {
            Product updatedProduct = product.get();
            System.out.println("Before Update:" + updatedProduct);
            updatedProduct.setName(request.getName());
            updatedProduct.setPrice(request.getPrice());
            updatedProduct.setQuantity(request.getQuantity());
            System.out.println("Before Save:" + updatedProduct);
            updatedProduct = productRepository.save(updatedProduct);
            ProductResponse response = new ProductResponse(updatedProduct);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setStatus(request.isStatus());
        product.setSupplierId(request.getSupplierId());
        System.out.println("Before Save:" + product);
        Product savedProduct = productRepository.save(product);
        ProductResponse response = new ProductResponse(savedProduct);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductsById(@PathVariable int id) {
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()){
            productRepository.delete(product.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

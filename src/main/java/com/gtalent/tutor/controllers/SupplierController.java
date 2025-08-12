package com.gtalent.tutor.controllers;


import com.gtalent.tutor.models.Product;
import com.gtalent.tutor.models.Supplier;
import com.gtalent.tutor.repositories.SupplierRepository;
import com.gtalent.tutor.requests.CreateSupplierRequest;
import com.gtalent.tutor.requests.UpdateSupplierRequest;
import com.gtalent.tutor.responses.ProductResponse;
import com.gtalent.tutor.responses.SupplierResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {
    private final SupplierRepository supplierRepository;
    @Autowired

    public SupplierController(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }


    @GetMapping
    public ResponseEntity<List<SupplierResponse>> getAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();
//        return ResponseEntity.ok(suppliers.stream().map(SupplierResponse::new).toList());
        List<SupplierResponse> responses = suppliers
                .stream()
                .map(supplier -> {
            SupplierResponse response = new SupplierResponse(supplier);
            response.setProducts(supplier.getProducts().stream().map(ProductResponse::new).toList());
            return response;
        }).toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponse> getSuppliersById(@PathVariable int id) {
        Optional<Supplier> supplier = supplierRepository.findById(id);
        if (supplier.isPresent()) {
            SupplierResponse response = new SupplierResponse(supplier.get());
            List<Product> productList = supplier.get().getProducts();
            List<ProductResponse> productResponseList = productList.stream()
                    .map(product -> new ProductResponse(product))
                    .toList();
            response.setProducts(productResponseList);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponse> updateSuppliersById(@PathVariable int id, @RequestBody UpdateSupplierRequest request) {
        Optional<Supplier> supplier = supplierRepository.findById(id);
        if(supplier.isPresent()) {
            Supplier updatedSupplier = supplier.get();
            System.out.println("Before Update:" + updatedSupplier);
            updatedSupplier.setName(request.getName());
            updatedSupplier.setPhone(request.getPhone());
            System.out.println("Before Save:" + updatedSupplier);
            updatedSupplier = supplierRepository.save(updatedSupplier);
            SupplierResponse response = new SupplierResponse(updatedSupplier);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<SupplierResponse> createSupplier(@RequestBody CreateSupplierRequest request) {
        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        supplier.setEmail(request.getEmail());
        System.out.println("Before Save:" + supplier);
        Supplier savedSupplier = supplierRepository.save(supplier);
        SupplierResponse response = new SupplierResponse(savedSupplier);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSuppliersById(@PathVariable int id) {
        Optional<Supplier> supplier = supplierRepository.findById(id);
        if(supplier.isPresent()){
            supplierRepository.delete(supplier.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

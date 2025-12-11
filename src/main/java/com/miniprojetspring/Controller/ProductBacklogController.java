package com.miniprojetspring.controller;

import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Service.ProductBacklogService;
import com.miniprojetspring.payload.ProductBacklogPayload;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product-backlogs")
public class ProductBacklogController {

    private final ProductBacklogService productBacklogService;

    @Autowired
    public ProductBacklogController(ProductBacklogService productBacklogService) {
        this.productBacklogService = productBacklogService;
    }

    @PostMapping()
    public ResponseEntity<ProductBacklog> createProductBacklog(@Valid @RequestBody ProductBacklogPayload payload) {
        ProductBacklog productBacklog = productBacklogService.createProductBacklog(payload);
        return ResponseEntity.ok(productBacklog);
    }

    @GetMapping()
    public ResponseEntity<ProductBacklog> getProductBacklogById() {
        ProductBacklog productBacklog = productBacklogService.getProductBacklog();
        return ResponseEntity.ok(productBacklog);
    }

    @PutMapping()
    public ResponseEntity<ProductBacklog> updateProductBacklog(@Valid @RequestBody ProductBacklogPayload payload) {
        ProductBacklog productBacklog = productBacklogService.updateProductBacklog(payload);
        return ResponseEntity.ok(productBacklog);
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteProductBacklog() {
        productBacklogService.deleteProductBacklog();
        return ResponseEntity.noContent().build();
    }
}
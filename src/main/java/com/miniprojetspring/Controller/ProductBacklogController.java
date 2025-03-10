package com.miniprojetspring.Controller;

import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Service.ProductBacklogService;
import com.miniprojetspring.payload.ProductBacklogPayload;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductBacklogController {

    private final ProductBacklogService productBacklogService;

    @Autowired
    public ProductBacklogController(ProductBacklogService productBacklogService) {
        this.productBacklogService = productBacklogService;
    }

    @PostMapping("/projects/{projectId}/product-backlogs")
    public ResponseEntity<ProductBacklog> createProductBacklog(@PathVariable String projectId, @Valid @RequestBody ProductBacklogPayload payload) {
        ProductBacklog productBacklog = productBacklogService.createProductBacklog(projectId, payload);
        return ResponseEntity.ok(productBacklog);
    }

    @GetMapping("/product-backlogs/{id}")
    public ResponseEntity<ProductBacklog> getProductBacklogById(@PathVariable String id) {
        ProductBacklog productBacklog = productBacklogService.getProductBacklogById(id);
        return ResponseEntity.ok(productBacklog);
    }

    @PutMapping("/product-backlogs/{id}")
    public ResponseEntity<ProductBacklog> updateProductBacklog(@PathVariable String id, @Valid @RequestBody ProductBacklogPayload payload) {
        ProductBacklog productBacklog = productBacklogService.updateProductBacklog(id, payload);
        return ResponseEntity.ok(productBacklog);
    }

    @DeleteMapping("/product-backlogs/{id}")
    public ResponseEntity<Void> deleteProductBacklog(@PathVariable String id) {
        productBacklogService.deleteProductBacklog(id);
        return ResponseEntity.noContent().build();
    }
}
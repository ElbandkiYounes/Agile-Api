package com.miniprojetspring.Service;

import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.payload.CreateProductBacklogPayload;
import com.miniprojetspring.payload.UpdateProductBacklogPayload;

import java.util.UUID;

public interface ProductBacklogService {
    ProductBacklog createProductBacklog(CreateProductBacklogPayload payload);
    ProductBacklog getProductBacklogById(UUID id);
    void deleteProductBacklog(UUID id);
    ProductBacklog updateProductBacklog(UUID id, UpdateProductBacklogPayload payload);
}
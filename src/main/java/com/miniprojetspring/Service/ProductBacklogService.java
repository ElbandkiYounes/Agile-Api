package com.miniprojetspring.Service;

import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.payload.ProductBacklogPayload;

public interface ProductBacklogService {
    ProductBacklog createProductBacklog(String id, ProductBacklogPayload payload);
    ProductBacklog getProductBacklogById(String id);
    void deleteProductBacklog(String id);
    ProductBacklog updateProductBacklog(String id, ProductBacklogPayload payload);
}
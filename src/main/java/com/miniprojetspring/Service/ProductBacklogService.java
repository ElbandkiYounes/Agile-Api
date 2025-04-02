package com.miniprojetspring.Service;

import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.payload.ProductBacklogPayload;

public interface ProductBacklogService {
    ProductBacklog createProductBacklog(ProductBacklogPayload payload);
    ProductBacklog getProductBacklog();
    void deleteProductBacklog();
    ProductBacklog updateProductBacklog(ProductBacklogPayload payload);
}
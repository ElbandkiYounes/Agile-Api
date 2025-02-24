package com.miniprojetspring.payload;

import com.miniprojetspring.Model.Epic;
import com.miniprojetspring.Model.ProductBacklog;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateProductBacklogPayload {
    private String name;
    private List<Epic> epics;

    public ProductBacklog ToEntity(ProductBacklog productBacklog) {
        productBacklog.setName(this.getName());
        productBacklog.setEpics(this.getEpics());
        return productBacklog;
    }
}

package com.miniprojetspring.payload;

import com.miniprojetspring.model.ProductBacklog;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class ProductBacklogPayload {

    @NotBlank(message = "name is required (Cannot be blank)")
    @NotNull(message = "name is required (Cannot be null)")
    private String name;

    public ProductBacklog toEntity() {
        return ProductBacklog.builder()
                .name(name)
                .build();
    }

    public ProductBacklog ToEntity(ProductBacklog productBacklog) {
        productBacklog.setName(this.getName());
        return productBacklog;
    }
}

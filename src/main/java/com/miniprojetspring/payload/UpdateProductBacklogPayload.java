package com.miniprojetspring.payload;

import com.miniprojetspring.Model.ProductBacklog;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductBacklogPayload {
    @NotBlank(message = "name is required (Cannot be blank)")
    @NotNull(message = "name is required (Cannot be null)")
    private String name;

    public ProductBacklog ToEntity(ProductBacklog productBacklog) {
        productBacklog.setName(this.getName());
        return productBacklog;
    }
}

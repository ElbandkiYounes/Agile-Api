package com.miniprojetspring.payload;

import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.Project;
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

    @NotBlank(message = "projectId is required (Cannot be blank)")
    @NotNull(message = "projectId is required (Cannot be null)")
    private String projectId;

    public ProductBacklog toEntity(Project project) {
        return ProductBacklog.builder()
                .name(name)
                .project(project)
                .build();
    }

    public ProductBacklog ToEntity(ProductBacklog productBacklog) {
        productBacklog.setName(this.getName());
        return productBacklog;
    }
}

package com.miniprojetspring.Repository;

import com.miniprojetspring.Model.ProductBacklog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductBacklogRepository extends JpaRepository<ProductBacklog, UUID> {
}

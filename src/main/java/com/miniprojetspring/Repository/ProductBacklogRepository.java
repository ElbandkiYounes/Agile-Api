package com.miniprojetspring.repository;

import com.miniprojetspring.model.ProductBacklog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductBacklogRepository extends JpaRepository<ProductBacklog, UUID> {
}

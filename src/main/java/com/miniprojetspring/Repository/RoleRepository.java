package com.miniprojetspring.repository;

import com.miniprojetspring.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByNameAndProject_Id(String name, UUID projectId);
    List<Role> findAllByProject_Id(UUID projectId);

}

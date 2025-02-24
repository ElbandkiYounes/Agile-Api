package com.miniprojetspring.Repository;

import com.miniprojetspring.Model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

}

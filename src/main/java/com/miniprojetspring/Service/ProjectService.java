package com.miniprojetspring.Service;

import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.payload.ProjectPayload;

import java.util.UUID;

public interface ProjectService {
    Project getProjectById(String id);
    Project createProject(ProjectPayload payload);
    Project updateProject(String id, ProjectPayload payload);
    void deleteProject(String id);
    Project linkProductBacklogToProject(UUID projectId, ProductBacklog productBacklog);
}

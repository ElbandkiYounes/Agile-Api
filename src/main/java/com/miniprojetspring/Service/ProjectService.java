package com.miniprojetspring.Service;

import com.miniprojetspring.Model.Project;
import com.miniprojetspring.payload.ProjectPayload;

public interface ProjectService {
    Project getProjectById(String id);
    Project createProject(ProjectPayload payload);
    Project updateProject(String id, ProjectPayload payload);
    void deleteProject(String id);
}

package com.miniprojetspring.Service;

import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Model.User;
import com.miniprojetspring.payload.InviteUserPayload;
import com.miniprojetspring.payload.ProjectPayload;

import java.util.UUID;

public interface ProjectService {
    Project getProject();
    Project createProject(ProjectPayload payload);
    Project updateProject(ProjectPayload payload);
    void deleteProject();
    Project linkProductBacklogToProject(ProductBacklog productBacklog);
    User inviteUser(InviteUserPayload userPayload);
}

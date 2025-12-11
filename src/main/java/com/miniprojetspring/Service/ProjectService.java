package com.miniprojetspring.Service;

import com.miniprojetspring.model.ProductBacklog;
import com.miniprojetspring.model.Project;
import com.miniprojetspring.model.User;
import com.miniprojetspring.payload.InviteUserPayload;
import com.miniprojetspring.payload.ProjectPayload;

public interface ProjectService {
    Project getProject();
    Project createProject(ProjectPayload payload);
    Project updateProject(ProjectPayload payload);
    void deleteProject();
    Project linkProductBacklogToProject(ProductBacklog productBacklog);
    User inviteUser(InviteUserPayload userPayload);
}

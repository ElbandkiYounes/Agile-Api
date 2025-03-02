package com.miniprojetspring.Service;

import com.miniprojetspring.Model.SprintBacklog;
import com.miniprojetspring.payload.SprintBacklogPayload;

public interface SprintBacklogService {
    SprintBacklog createSprintBacklog(String id, SprintBacklogPayload payload);
    SprintBacklog getSprintBacklogById(String id);
    void deleteSprintBacklog(String id);
    SprintBacklog updateSprintBacklog(String id, SprintBacklogPayload payload);
}
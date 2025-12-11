package com.miniprojetspring.service;

import com.miniprojetspring.model.SprintBacklog;
import com.miniprojetspring.payload.SprintBacklogPayload;

public interface SprintBacklogService {
    SprintBacklog createSprintBacklog(SprintBacklogPayload payload);
    SprintBacklog getSprintBacklogById(String id);
    void deleteSprintBacklog(String id);
    SprintBacklog updateSprintBacklog(String id, SprintBacklogPayload payload);
}
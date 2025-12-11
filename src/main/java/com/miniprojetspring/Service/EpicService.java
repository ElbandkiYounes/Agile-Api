package com.miniprojetspring.Service;

import com.miniprojetspring.model.Epic;
import com.miniprojetspring.payload.EpicPayload;

import java.util.List;

public interface EpicService {
    Epic createEpic(EpicPayload payload);
    Epic getEpicById(String id);
    List<Epic> getEpics();
    void deleteEpic(String id);
    Epic updateEpic(String id, EpicPayload payload);
    Epic linkEpicToSprintBacklog(String sprintBacklogId, String epicId);
    Epic unlinkEpicToSprintBacklog(String epicId);
}

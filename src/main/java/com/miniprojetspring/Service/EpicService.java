package com.miniprojetspring.Service;

import com.miniprojetspring.Model.Epic;
import com.miniprojetspring.payload.CreateEpicPayload;
import com.miniprojetspring.payload.UpdateEpicPayload;

import java.util.List;
import java.util.UUID;

public interface EpicService {
    Epic createEpic(CreateEpicPayload payload);
    Epic getEpicById(UUID id);
    List<Epic> getEpicsByProductBacklogId(UUID productBacklogId);
    void deleteEpic(UUID id);
    Epic updateEpic(UUID id, UpdateEpicPayload payload);
}

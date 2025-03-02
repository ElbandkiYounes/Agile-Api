package com.miniprojetspring.Service;

import com.miniprojetspring.Model.Epic;
import com.miniprojetspring.payload.EpicPayload;

import java.util.List;
import java.util.UUID;

public interface EpicService {
    Epic createEpic(String id,EpicPayload payload);
    Epic getEpicById(String id);
    List<Epic> getEpicsByProductBacklogId(String productBacklogId);
    void deleteEpic(String id);
    Epic updateEpic(String id, EpicPayload payload);
}

package com.miniprojetspring.Service;

import com.miniprojetspring.Model.UserStory;
import com.miniprojetspring.payload.UserStoryPayload;

import java.util.List;

public interface UserStoryService {
    List<UserStory> getUserStoriesByRoleId(String roleId);
    List<UserStory> getUserStoriesByEpicId(String id);
    List<UserStory> getUserStoriesByBacklogId(String id);
    UserStory createUserStory(String productBacklogId, UserStoryPayload userStoryPayload);
    UserStory unlinkUserStoryFromEpic(String userStoryId);
    UserStory linkUserStoryToEpic(String epicId, String userStoryId);
    UserStory getUserStoryById(String id);
    UserStory updateUserStory(UserStoryPayload userStoryPayload, String id);
    void deleteUserStory(String id);
    public void checkUserStoryStatus(String id);
}
package com.miniprojetspring.Service;

import com.miniprojetspring.model.UserStory;
import com.miniprojetspring.payload.UserStoryPayload;

import java.util.List;

public interface UserStoryService {
    List<UserStory> getUserStoriesByRoleId(String roleId);
    List<UserStory> getUserStoriesByEpicId(String id);
    List<UserStory> getUserStories();
    UserStory createUserStory(UserStoryPayload userStoryPayload);
    UserStory unlinkUserStoryFromEpic(String userStoryId);
    UserStory linkUserStoryToEpic(String epicId, String userStoryId);
    UserStory getUserStoryById(String id);
    UserStory updateUserStory(UserStoryPayload userStoryPayload, String id);
    void deleteUserStory(String id);
    void checkUserStoryStatus(String id);
}
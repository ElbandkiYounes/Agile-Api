package com.miniprojetspring.Service;

import com.miniprojetspring.Model.UserStory;
import com.miniprojetspring.payload.UserStoryPayload;

import java.util.List;
import java.util.UUID;

public interface UserStoryService {
    List<UserStory> getAllUserStories();
    UserStory createUserStory(UserStoryPayload userStoryPayload);
    UserStory linkUserStoryToEpic(UUID epicId, UUID userStoryId);
    UserStory getUserStoryById(UUID id);
    UserStory updateUserStory(UserStoryPayload userStoryPayload, UUID id);
    void deleteUserStory(UUID id);
}
package com.miniprojetspring.Service.Implementation;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Epic;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.UserStory;
import com.miniprojetspring.Repository.UserStoryRepository;
import com.miniprojetspring.Service.UserStoryService;
import com.miniprojetspring.payload.UserStoryPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserStoryServiceImpl implements UserStoryService {

    private final UserStoryRepository userStoryRepository;
    private final ProductBacklogServiceImpl productBacklogServiceImpl;
    private  final EpicServiceImpl epicService;

    @Autowired
    public UserStoryServiceImpl(UserStoryRepository userStoryRepository, ProductBacklogServiceImpl productBacklogServiceImpl, EpicServiceImpl epicService) {
        this.userStoryRepository = userStoryRepository;
        this.productBacklogServiceImpl = productBacklogServiceImpl;
        this.epicService = epicService;
    }

    public List<UserStory> getUserStoriesByEpicId(String EpicId) {
        Epic epic = epicService.getEpicById(EpicId);
        if(epic==null) {
            throw new NotFoundException("Epic not found.");
        }
        return userStoryRepository.findByEpicId(UUID.fromString(EpicId));
    }

    public List<UserStory> getUserStoriesByBacklogId(String productBacklogId) {
        ProductBacklog productBacklog = productBacklogServiceImpl.getProductBacklogById(productBacklogId);
        if(productBacklog==null) {
            throw new NotFoundException("Product backlog not found.");
        }
        return userStoryRepository.findUserStoriesByProductBacklogId(UUID.fromString(productBacklogId));
    }

    public UserStory createUserStory(UserStoryPayload userStoryPayload) {
        ProductBacklog productBacklog = productBacklogServiceImpl.getProductBacklogById(userStoryPayload.getProductBacklogId());
        if (productBacklog == null) {
            throw new NotFoundException("Product backlog not found");
        }

        UserStory userStory = UserStory.builder()
                .title(userStoryPayload.getTitle())
                .description(userStoryPayload.getDescription())
                .priority(userStoryPayload.getUserStoryPriority())
                .status(userStoryPayload.getUserStoryStatus())
                .productBacklog(productBacklog).build();


        userStory.setProductBacklog(productBacklog);

        return userStoryRepository.save(userStory);
    }

    public UserStory linkUserStoryToEpic(UUID epicId,UUID userStoryId) {
        UserStory userStory = getUserStoryById(userStoryId);
        Epic epic= epicService.getEpicById(epicId.toString());
        if(epic==null) {
            throw new NotFoundException("Epic not found.");
        }

        if(!userStory.getProductBacklog().getId().equals(epic.getProductBacklog().getId())) {
            throw new NotFoundException("UserStory and Epic not on the same backlog");
        }
        userStory.setEpic(epic);
        return userStoryRepository.save(userStory);

    }

    public UserStory getUserStoryById(UUID id) {
        return userStoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User Story not found for ID: " + id));
    }

    public UserStory updateUserStory(UserStoryPayload userStoryPayload,UUID id) {
        UserStory userStory = getUserStoryById(id);

        ProductBacklog productBacklog = productBacklogServiceImpl.getProductBacklogById(userStoryPayload.getProductBacklogId());
        if (productBacklog == null) {
            throw new NotFoundException("Product backlog not found");
        }

        userStory.setTitle(userStoryPayload.getTitle());
        userStory.setDescription(userStoryPayload.getDescription());
        userStory.setPriority(userStoryPayload.getUserStoryPriority());
        userStory.setStatus(userStoryPayload.getUserStoryStatus());
        userStory.setProductBacklog(productBacklog);

        return userStoryRepository.save(userStory);
    }

    public void deleteUserStory(UUID id) {
        UserStory userStory = getUserStoryById(id);
        userStoryRepository.delete(userStory);
    }
}

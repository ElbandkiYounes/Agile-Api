package com.miniprojetspring.Service;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.UserStory;
import com.miniprojetspring.Repository.ProductBacklogRepository;
import com.miniprojetspring.Repository.UserStoryRepository;
import com.miniprojetspring.payload.UserStoryPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserStoryService {

    private final UserStoryRepository userStoryRepository;
    private final ProductBacklogRepository productBacklogRepository;

    @Autowired
    public UserStoryService(UserStoryRepository userStoryRepository, ProductBacklogRepository productBacklogRepository) {
        this.userStoryRepository = userStoryRepository;
        this.productBacklogRepository = productBacklogRepository;
    }

    public List<UserStory> getAllUserStories() {
        return userStoryRepository.findAll();
    }

    public UserStory createUserStory(UserStoryPayload userStoryPayload) {
        Optional<ProductBacklog> productBacklogOptional = productBacklogRepository.findById(UUID.fromString(userStoryPayload.getProductBacklogId()));
        if (productBacklogOptional.isEmpty()) {
            throw new NotFoundException("Product Backlog not found.");
        }

        UserStory userStory = UserStory.builder()
                .title(userStoryPayload.getTitle())
                .description(userStoryPayload.getDescription())
                .priority(userStoryPayload.getUserStoryPriority())
                .status(userStoryPayload.getUserStoryStatus())
                .productBacklog(productBacklogOptional.get()).build();


        userStory.setProductBacklog(productBacklogOptional.get());

        return userStoryRepository.save(userStory);
    }

    public UserStory getUserStoryById(UUID id) {
        return userStoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User Story not found for ID: " + id));
    }

    public UserStory updateUserStory(UserStoryPayload userStoryPayload,UUID id) {
        UserStory userStory = getUserStoryById(id);

        Optional<ProductBacklog> productBacklogOptional = productBacklogRepository.findById(UUID.fromString(userStoryPayload.getProductBacklogId()));
        if (productBacklogOptional.isEmpty()) {
            throw new NotFoundException("Product Backlog not found.");
        }

        userStory.setTitle(userStoryPayload.getTitle());
        userStory.setDescription(userStoryPayload.getDescription());
        userStory.setPriority(userStoryPayload.getUserStoryPriority());
        userStory.setStatus(userStoryPayload.getUserStoryStatus());
        userStory.setProductBacklog(productBacklogOptional.get());

        return userStoryRepository.save(userStory);
    }

    public void deleteUserStory(UUID id) {
        UserStory userStory = getUserStoryById(id);
        userStoryRepository.delete(userStory);
    }
}

package com.miniprojetspring.Service.Implementation;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Epic;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.Role;
import com.miniprojetspring.Model.UserStory;
import com.miniprojetspring.Repository.UserStoryRepository;
import com.miniprojetspring.Service.EpicService;
import com.miniprojetspring.Service.ProductBacklogService;
import com.miniprojetspring.Service.RoleService;
import com.miniprojetspring.Service.UserStoryService;
import com.miniprojetspring.payload.UserStoryPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserStoryServiceImpl implements UserStoryService {

    private final UserStoryRepository userStoryRepository;
    private final ProductBacklogService productBacklogServiceImpl;
    private final EpicService epicService;
    private final RoleService roleService;

    @Autowired
    public UserStoryServiceImpl(
            UserStoryRepository userStoryRepository,
            ProductBacklogService productBacklogServiceImpl,
            EpicService epicService,
            RoleService roleService) {
        this.userStoryRepository = userStoryRepository;
        this.productBacklogServiceImpl = productBacklogServiceImpl;
        this.epicService = epicService;
        this.roleService = roleService;
    }

    public List<UserStory> getUserStoriesByRoleId(String roleId) {
        Role role = roleService.getRoleById(roleId);
        if(role==null) {
            throw new NotFoundException("Role not found.");
        }
        return userStoryRepository.findByRoleId(UUID.fromString(roleId));
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

    public UserStory createUserStory(String productBacklogId, UserStoryPayload userStoryPayload) {
        ProductBacklog productBacklog = productBacklogServiceImpl.getProductBacklogById(productBacklogId);
        if (productBacklog == null) {
            throw new NotFoundException("Product backlog not found");
        }

        Role role = roleService.getRoleById(userStoryPayload.getRoleId());
        if (role == null) {
            throw new NotFoundException("Role not found");
        }

        if (role.getProject().getId() != productBacklog.getProject().getId()) {
            throw new NotFoundException("Role and Product Backlog not on the same project");
        }
        return userStoryRepository.save(userStoryPayload.toEntity(productBacklog,role));
    }

    public UserStory linkUserStoryToEpic(String epicId,String userStoryId) {
        UserStory userStory = getUserStoryById(userStoryId);
        Epic epic= epicService.getEpicById(epicId);
        if(epic==null) {
            throw new NotFoundException("Epic not found.");
        }

        if(!userStory.getProductBacklog().getId().equals(epic.getProductBacklog().getId())) {
            throw new NotFoundException("UserStory and Epic not on the same backlog");
        }
        userStory.setEpic(epic);
        return userStoryRepository.save(userStory);

    }

    public UserStory unlinkUserStoryFromEpic(String userStoryId) {
        UserStory userStory = getUserStoryById(userStoryId);
        userStory.setEpic(null);
        return userStory;
    }

    public UserStory getUserStoryById(String id) {
        return userStoryRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("User Story not found for ID: " + id));
    }

    public UserStory updateUserStory(UserStoryPayload userStoryPayload,String userStoryId) {
        UserStory userStory = getUserStoryById(userStoryId);

        Role role = roleService.getRoleById(userStoryPayload.getRoleId());
        if (role == null) {
            throw new NotFoundException("Role not found");
        }

        if (role.getProject().getId() != userStory.getProductBacklog().getProject().getId()) {
            throw new NotFoundException("Role and Product Backlog not on the same project");
        }

        return userStoryRepository.save(userStoryPayload.toEntity(userStory,role));
    }

    public void deleteUserStory(String id) {
        UserStory userStory = getUserStoryById(id);
        userStoryRepository.delete(userStory);
    }
}

package com.miniprojetspring.service.implementation;

import com.miniprojetspring.exception.ConflictException;
import com.miniprojetspring.exception.NotFoundException;
import com.miniprojetspring.model.*;
import com.miniprojetspring.repository.TestCaseRepository;
import com.miniprojetspring.repository.UserStoryRepository;
import com.miniprojetspring.service.EpicService;
import com.miniprojetspring.service.ProductBacklogService;
import com.miniprojetspring.service.RoleService;
import com.miniprojetspring.service.UserStoryService;
import com.miniprojetspring.payload.UserStoryPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserStoryServiceImpl implements UserStoryService {

    private final UserStoryRepository userStoryRepository;
    private final ProductBacklogService productBacklogServiceImpl;
    private final EpicService epicService;
    private final RoleService roleService;
    private final TestCaseRepository testCaseRepository;
    private final ProjectSecurityService projectSecurityService;

    @Autowired
    public UserStoryServiceImpl(
            UserStoryRepository userStoryRepository,
            ProductBacklogService productBacklogServiceImpl,
            EpicService epicService,
            RoleService roleService, com.miniprojetspring.repository.TestCaseRepository testCaseRepository, ProjectSecurityService projectSecurityService) {
        this.userStoryRepository = userStoryRepository;
        this.productBacklogServiceImpl = productBacklogServiceImpl;
        this.epicService = epicService;
        this.roleService = roleService;
        this.testCaseRepository = testCaseRepository;
        this.projectSecurityService = projectSecurityService;
    }

    private void validateProjectAccess(String projectId) throws NotFoundException {
        if (!projectSecurityService.isProjectMember(projectId)
                && !projectSecurityService.isProjectOwner(projectId)) {
            throw new AccessDeniedException("Access denied to project");
        }
    }

    private void validateSameProject(UUID projectId1, UUID projectId2, String errorMessage) throws NotFoundException{
        if (!projectId1.equals(projectId2)) {
            throw new AccessDeniedException(errorMessage);
        }
    }

    public List<UserStory> getUserStoriesByRoleId(String roleId) {
        Role role = roleService.getRoleById(roleId);
        if(role==null) {
            throw new NotFoundException("Role not found.");
        }
        validateProjectAccess(role.getProject().getId().toString());
        return userStoryRepository.findByRole_Id(UUID.fromString(roleId));
    }

    public List<UserStory> getUserStoriesByEpicId(String epicId) {
        Epic epic = epicService.getEpicById(epicId);
        if(epic==null) {
            throw new NotFoundException("Epic not found.");
        }
        validateProjectAccess(epic.getProductBacklog().getProject().getId().toString());
        return userStoryRepository.findByEpic_Id(UUID.fromString(epicId));
    }

    public List<UserStory> getUserStories() {
        ProductBacklog productBacklog = productBacklogServiceImpl.getProductBacklog();
        if(productBacklog==null) {
            throw new NotFoundException("Product backlog not found.");
        }
        return userStoryRepository.findUserStoriesByProductBacklog_Id(UUID.fromString(productBacklog.getId().toString()));
    }

    public UserStory createUserStory(UserStoryPayload userStoryPayload) {
        ProductBacklog productBacklog = productBacklogServiceImpl.getProductBacklog();
        if (productBacklog == null) {
            throw new NotFoundException("Product backlog not found");
        }
        Role role = roleService.getRoleById(userStoryPayload.getRoleId());
        if (role == null) {
            throw new NotFoundException("Role not found");
        }

        validateSameProject(role.getProject().getId(), productBacklog.getProject().getId(),
                "Role and Product Backlog not on the same project");
        return userStoryRepository.save(userStoryPayload.toEntity(productBacklog,role));
    }

    public UserStory linkUserStoryToEpic(String epicId,String userStoryId) {
        UserStory userStory = getUserStoryById(userStoryId);
        Epic epic= epicService.getEpicById(epicId);

        if(userStory.getEpic() != null) {
            throw new ConflictException("UserStory already linked to an Epic");
        }

        if(epic==null) {
            throw new NotFoundException("Epic not found.");
        }

        validateSameProject(userStory.getProductBacklog().getId(), epic.getProductBacklog().getId(),
                "UserStory and Epic not on the same backlog");
        userStory.setEpic(epic);
        return userStoryRepository.save(userStory);

    }

    public UserStory unlinkUserStoryFromEpic(String userStoryId) {
        UserStory userStory = getUserStoryById(userStoryId);
        userStory.setEpic(null);
        return userStoryRepository.save(userStory);
    }

    public UserStory getUserStoryById(String id) {
        UserStory userStory =  userStoryRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("User Story not found for ID: " + id));
        validateProjectAccess(userStory.getProductBacklog().getProject().getId().toString());
        return userStory;
    }

    public UserStory updateUserStory(UserStoryPayload userStoryPayload,String userStoryId) {
        UserStory userStory = getUserStoryById(userStoryId);

        Role role = roleService.getRoleById(userStoryPayload.getRoleId());
        if (role == null) {
            throw new NotFoundException("Role not found");
        }

        validateSameProject(role.getProject().getId(), userStory.getProductBacklog().getProject().getId(),
                "Role and Product Backlog not on the same project");

        return userStoryRepository.save(userStoryPayload.toEntity(userStory,role));
    }

    public void deleteUserStory(String id) {
        UserStory userStory = getUserStoryById(id);
        userStoryRepository.delete(userStory);
    }

    @Override
    public void checkUserStoryStatus(String id) {
        UserStory userStory = getUserStoryById(id);
        List<TestCase> testCases = testCaseRepository.findTestCasesByUserStoryId(UUID.fromString(id));

        if (testCases.isEmpty()) {
            userStory.setStatus(UserStoryStatus.NOT_STARTED);
            userStoryRepository.save(userStory);
            return;
        }

        boolean allTestCasePassed = testCases.stream()
                .allMatch(testCase -> testCase.getResult() != null && testCase.getResult().equals(TestCaseResult.PASS));

        if (allTestCasePassed) {
            userStory.setStatus(UserStoryStatus.DONE);
        } else {
            userStory.setStatus(UserStoryStatus.IN_PROGRESS);
        }

        userStoryRepository.save(userStory);
    }
}

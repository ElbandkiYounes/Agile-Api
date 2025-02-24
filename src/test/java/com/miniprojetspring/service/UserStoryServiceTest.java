package com.miniprojetspring.service;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Epic;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.UserStory;
import com.miniprojetspring.Model.UserStoryPriority;
import com.miniprojetspring.Model.UserStoryStatus;
import com.miniprojetspring.Repository.UserStoryRepository;
import com.miniprojetspring.Service.ProductBacklogService;
import com.miniprojetspring.Service.UserStoryService;
import com.miniprojetspring.Service.EpicService;
import com.miniprojetspring.payload.UserStoryPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserStoryServiceTest {

    @Mock
    private UserStoryRepository userStoryRepository;

    @Mock
    private ProductBacklogService productBacklogService;

    @Mock
    private EpicService epicService;

    @InjectMocks
    private UserStoryService userStoryService;

    private UserStoryPayload payload;
    private ProductBacklog productBacklog;
    private UserStory userStory;
    private UUID productBacklogId;
    private UUID userStoryId;
    private UUID epicId;
    private Epic epic;

    @BeforeEach
    public void setUp() {
        productBacklogId = UUID.randomUUID();
        userStoryId = UUID.randomUUID();
        epicId = UUID.randomUUID();

        payload = new UserStoryPayload();
        payload.setTitle("Test User Story");
        payload.setDescription("This is a test description");
        payload.setProductBacklogId(productBacklogId.toString());
        payload.setUserStoryPriority(UserStoryPriority.HIGH);
        payload.setUserStoryStatus(UserStoryStatus.TO_DO);

        productBacklog = new ProductBacklog();
        productBacklog.setId(productBacklogId);

        userStory = UserStory.builder()
                .id(userStoryId)
                .title(payload.getTitle())
                .description(payload.getDescription())
                .priority(payload.getUserStoryPriority())
                .status(payload.getUserStoryStatus())
                .productBacklog(productBacklog)
                .build();

        epic = new Epic();
        epic.setId(epicId);
        epic.setProductBacklog(productBacklog);
    }

    @Test
    public void testCreateUserStory_Success() {
        when(productBacklogService.getProductBacklogById(any(UUID.class))).thenReturn(productBacklog);
        when(userStoryRepository.save(any(UserStory.class))).thenReturn(userStory);

        UserStory createdUserStory = userStoryService.createUserStory(payload);

        assertNotNull(createdUserStory);
        assertEquals(payload.getTitle(), createdUserStory.getTitle());
        assertEquals(payload.getDescription(), createdUserStory.getDescription());

        verify(productBacklogService, times(1)).getProductBacklogById(any(UUID.class));
        verify(userStoryRepository, times(1)).save(any(UserStory.class));
    }

    @Test
    public void testCreateUserStory_ProductBacklogNotFound() {
        when(productBacklogService.getProductBacklogById(any(UUID.class))).thenThrow(new NotFoundException("Product backlog not found"));

        assertThrows(NotFoundException.class, () -> userStoryService.createUserStory(payload));

        verify(productBacklogService, times(1)).getProductBacklogById(any(UUID.class));
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    @Test
    public void testGetUserStoryById_Success() {
        when(userStoryRepository.findById(any(UUID.class))).thenReturn(Optional.of(userStory));

        UserStory retrievedUserStory = userStoryService.getUserStoryById(userStoryId);

        assertNotNull(retrievedUserStory);
        assertEquals(userStoryId, retrievedUserStory.getId());

        verify(userStoryRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void testGetUserStoryById_NotFound() {
        when(userStoryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userStoryService.getUserStoryById(userStoryId));

        verify(userStoryRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void testUpdateUserStory_Success() {
        when(userStoryRepository.findById(any(UUID.class))).thenReturn(Optional.of(userStory));
        when(productBacklogService.getProductBacklogById(any(UUID.class))).thenReturn(productBacklog);
        when(userStoryRepository.save(any(UserStory.class))).thenReturn(userStory);

        UserStory updatedUserStory = userStoryService.updateUserStory(payload, userStoryId);

        assertNotNull(updatedUserStory);
        assertEquals(payload.getTitle(), updatedUserStory.getTitle());
        assertEquals(payload.getDescription(), updatedUserStory.getDescription());

        verify(userStoryRepository, times(1)).findById(any(UUID.class));
        verify(productBacklogService, times(1)).getProductBacklogById(any(UUID.class));
        verify(userStoryRepository, times(1)).save(any(UserStory.class));
    }

    @Test
    public void testUpdateUserStory_NotFound() {
        when(userStoryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userStoryService.updateUserStory(payload, userStoryId));

        verify(userStoryRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void testDeleteUserStory_Success() {
        when(userStoryRepository.findById(any(UUID.class))).thenReturn(Optional.of(userStory));
        doNothing().when(userStoryRepository).delete(any(UserStory.class));

        assertDoesNotThrow(() -> userStoryService.deleteUserStory(userStoryId));

        verify(userStoryRepository, times(1)).findById(any(UUID.class));
        verify(userStoryRepository, times(1)).delete(any(UserStory.class));
    }

    @Test
    public void testDeleteUserStory_NotFound() {
        when(userStoryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userStoryService.deleteUserStory(userStoryId));

        verify(userStoryRepository, times(1)).findById(any(UUID.class));
        verify(userStoryRepository, never()).delete(any(UserStory.class));
    }

    @Test
    public void testLinkUserStoryToEpic_Success() {
        when(userStoryRepository.findById(any(UUID.class))).thenReturn(Optional.of(userStory));
        when(epicService.getEpicById(any(UUID.class))).thenReturn(epic);
        when(userStoryRepository.save(any(UserStory.class))).thenReturn(userStory);

        UserStory linkedUserStory = userStoryService.linkUserStoryToEpic(epicId, userStoryId);

        assertNotNull(linkedUserStory);
        assertEquals(epic, linkedUserStory.getEpic());

        verify(userStoryRepository, times(1)).findById(any(UUID.class));
        verify(epicService, times(1)).getEpicById(any(UUID.class));
        verify(userStoryRepository, times(1)).save(any(UserStory.class));
    }

    @Test
    public void testLinkUserStoryToEpic_EpicNotFound() {
        when(userStoryRepository.findById(any(UUID.class))).thenReturn(Optional.of(userStory));
        when(epicService.getEpicById(any(UUID.class))).thenThrow(new NotFoundException("Epic not found."));

        assertThrows(NotFoundException.class, () -> userStoryService.linkUserStoryToEpic(epicId, userStoryId));

        verify(userStoryRepository, times(1)).findById(any(UUID.class));
        verify(epicService, times(1)).getEpicById(any(UUID.class));
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }
}

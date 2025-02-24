package com.miniprojetspring.service;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.UserStory;
import com.miniprojetspring.Model.UserStoryPriority;
import com.miniprojetspring.Model.UserStoryStatus;
import com.miniprojetspring.Repository.ProductBacklogRepository;
import com.miniprojetspring.Repository.UserStoryRepository;
import com.miniprojetspring.Service.UserStoryService;
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
    private ProductBacklogRepository productBacklogRepository;

    @InjectMocks
    private UserStoryService userStoryService;

    private UserStoryPayload payload;
    private ProductBacklog productBacklog;
    private UUID productBacklogId;
    private UUID userStoryId;

    @BeforeEach
    public void setUp() {
        productBacklogId = UUID.randomUUID();
        userStoryId = UUID.randomUUID();

        payload = new UserStoryPayload();
        payload.setTitle("Test User Story");
        payload.setDescription("This is a test description");
        payload.setProductBacklogId(productBacklogId.toString());
        payload.setUserStoryPriority(UserStoryPriority.HIGH);
        payload.setUserStoryStatus(UserStoryStatus.TO_DO);

        productBacklog = new ProductBacklog();
        productBacklog.setId(productBacklogId);
    }

    @Test
    public void testCreateUserStory_Success() {
        when(productBacklogRepository.findById(any(UUID.class))).thenReturn(Optional.of(productBacklog));

        UserStory expectedUserStory = UserStory.builder()
                .title(payload.getTitle())
                .description(payload.getDescription())
                .priority(payload.getUserStoryPriority())
                .status(payload.getUserStoryStatus())
                .productBacklog(productBacklog)
                .build();

        when(userStoryRepository.save(any(UserStory.class))).thenReturn(expectedUserStory);

        UserStory actualUserStory = userStoryService.createUserStory(payload);

        assertNotNull(actualUserStory);
        assertEquals(expectedUserStory.getTitle(), actualUserStory.getTitle());
        assertEquals(expectedUserStory.getDescription(), actualUserStory.getDescription());
        assertEquals(expectedUserStory.getPriority(), actualUserStory.getPriority());
        assertEquals(expectedUserStory.getStatus(), actualUserStory.getStatus());
        assertEquals(expectedUserStory.getProductBacklog(), actualUserStory.getProductBacklog());

        verify(productBacklogRepository, times(1)).findById(any(UUID.class));
        verify(userStoryRepository, times(1)).save(any(UserStory.class));
    }

    @Test
    public void testCreateUserStory_ProductBacklogNotFound() {
        when(productBacklogRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userStoryService.createUserStory(payload));

        verify(productBacklogRepository, times(1)).findById(any(UUID.class));
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    @Test
    public void testGetUserStoryById_Success() {
        UserStory userStory = new UserStory();
        userStory.setId(userStoryId);
        userStory.setTitle(payload.getTitle());
        userStory.setDescription(payload.getDescription());
        userStory.setPriority(payload.getUserStoryPriority());
        userStory.setStatus(payload.getUserStoryStatus());
        userStory.setProductBacklog(productBacklog);

        when(userStoryRepository.findById(any(UUID.class))).thenReturn(Optional.of(userStory));

        UserStory retrievedUserStory = userStoryService.getUserStoryById(userStoryId);

        assertNotNull(retrievedUserStory);
        assertEquals(userStoryId, retrievedUserStory.getId());
        assertEquals(payload.getTitle(), retrievedUserStory.getTitle());

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
        UserStory existingUserStory = new UserStory();
        existingUserStory.setId(userStoryId);
        existingUserStory.setTitle("Old Title");
        existingUserStory.setDescription("Old Description");
        existingUserStory.setPriority(UserStoryPriority.MEDIUM);
        existingUserStory.setStatus(UserStoryStatus.IN_PROGRESS);
        existingUserStory.setProductBacklog(productBacklog);

        when(userStoryRepository.findById(any(UUID.class))).thenReturn(Optional.of(existingUserStory));
        when(productBacklogRepository.findById(any(UUID.class))).thenReturn(Optional.of(productBacklog));
        when(userStoryRepository.save(any(UserStory.class))).thenReturn(existingUserStory);

        UserStory updatedUserStory = userStoryService.updateUserStory(payload, userStoryId);

        assertNotNull(updatedUserStory);
        assertEquals(payload.getTitle(), updatedUserStory.getTitle());
        assertEquals(payload.getDescription(), updatedUserStory.getDescription());
        assertEquals(payload.getUserStoryPriority(), updatedUserStory.getPriority());
        assertEquals(payload.getUserStoryStatus(), updatedUserStory.getStatus());

        verify(userStoryRepository, times(1)).findById(any(UUID.class));
        verify(productBacklogRepository, times(1)).findById(any(UUID.class));
        verify(userStoryRepository, times(1)).save(any(UserStory.class));
    }

    @Test
    public void testUpdateUserStory_NotFound() {
        when(userStoryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userStoryService.updateUserStory(payload, userStoryId));

        verify(userStoryRepository, times(1)).findById(any(UUID.class));
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    @Test
    public void testUpdateUserStory_ProductBacklogNotFound() {
        UserStory existingUserStory = new UserStory();
        existingUserStory.setId(userStoryId);
        existingUserStory.setTitle("Old Title");
        existingUserStory.setDescription("Old Description");
        existingUserStory.setPriority(UserStoryPriority.MEDIUM);
        existingUserStory.setStatus(UserStoryStatus.IN_PROGRESS);
        existingUserStory.setProductBacklog(productBacklog);

        when(userStoryRepository.findById(any(UUID.class))).thenReturn(Optional.of(existingUserStory));
        when(productBacklogRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userStoryService.updateUserStory(payload, userStoryId));

        verify(userStoryRepository, times(1)).findById(any(UUID.class));
        verify(productBacklogRepository, times(1)).findById(any(UUID.class));
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    @Test
    public void testDeleteUserStory_Success() {
        UserStory userStory = new UserStory();
        userStory.setId(userStoryId);

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
}

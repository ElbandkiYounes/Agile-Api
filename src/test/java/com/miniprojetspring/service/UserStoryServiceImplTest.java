package com.miniprojetspring.service;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Epic;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.UserStory;
import com.miniprojetspring.Model.UserStoryPriority;
import com.miniprojetspring.Model.UserStoryStatus;
import com.miniprojetspring.Repository.UserStoryRepository;
import com.miniprojetspring.Service.Implementation.ProductBacklogServiceImpl;
import com.miniprojetspring.Service.Implementation.UserStoryServiceImpl;
import com.miniprojetspring.Service.Implementation.EpicServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserStoryServiceImplTest {

    @Mock
    private UserStoryRepository userStoryRepository;

    @Mock
    private ProductBacklogServiceImpl productBacklogServiceImpl;

    @Mock
    private EpicServiceImpl epicServiceImpl;

    @InjectMocks
    private UserStoryServiceImpl userStoryServiceImpl;

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
        when(productBacklogServiceImpl.getProductBacklogById(productBacklogId)).thenReturn(productBacklog);
        when(userStoryRepository.save(any(UserStory.class))).thenReturn(userStory);

        UserStory createdUserStory = userStoryServiceImpl.createUserStory(payload);

        assertNotNull(createdUserStory);
        assertEquals(payload.getTitle(), createdUserStory.getTitle());
        assertEquals(payload.getDescription(), createdUserStory.getDescription());

        verify(productBacklogServiceImpl, times(1)).getProductBacklogById(productBacklogId);
        verify(userStoryRepository, times(1)).save(any(UserStory.class));
    }

    @Test
    public void testCreateUserStory_ProductBacklogNotFound() {
        when(productBacklogServiceImpl.getProductBacklogById(productBacklogId)).thenThrow(new NotFoundException("Product backlog not found"));

        assertThrows(NotFoundException.class, () -> userStoryServiceImpl.createUserStory(payload));

        verify(productBacklogServiceImpl, times(1)).getProductBacklogById(productBacklogId);
        verify(userStoryRepository, never()).save(userStory);
    }

    @Test
    public void testGetUserStoryById_Success() {
        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.of(userStory));

        UserStory retrievedUserStory = userStoryServiceImpl.getUserStoryById(userStoryId);

        assertNotNull(retrievedUserStory);
        assertEquals(userStoryId, retrievedUserStory.getId());

        verify(userStoryRepository, times(1)).findById(userStoryId);
    }

    @Test
    public void testGetUserStoryById_NotFound() {
        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userStoryServiceImpl.getUserStoryById(userStoryId));

        verify(userStoryRepository, times(1)).findById(userStoryId);
    }

    @Test
    public void testUpdateUserStory_Success() {
        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.of(userStory));
        when(productBacklogServiceImpl.getProductBacklogById(productBacklogId)).thenReturn(productBacklog);
        when(userStoryRepository.save(userStory)).thenReturn(userStory);

        UserStory updatedUserStory = userStoryServiceImpl.updateUserStory(payload, userStoryId);

        assertNotNull(updatedUserStory);
        assertEquals(payload.getTitle(), updatedUserStory.getTitle());
        assertEquals(payload.getDescription(), updatedUserStory.getDescription());

        verify(userStoryRepository, times(1)).findById(userStoryId);
        verify(productBacklogServiceImpl, times(1)).getProductBacklogById(productBacklogId);
        verify(userStoryRepository, times(1)).save(userStory);
    }

    @Test
    public void testUpdateUserStory_NotFound() {
        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userStoryServiceImpl.updateUserStory(payload, userStoryId));

        verify(userStoryRepository, times(1)).findById(userStoryId);
    }

    @Test
    public void testDeleteUserStory_Success() {
        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.of(userStory));
        doNothing().when(userStoryRepository).delete(userStory);

        assertDoesNotThrow(() -> userStoryServiceImpl.deleteUserStory(userStoryId));

        verify(userStoryRepository, times(1)).findById(userStoryId);
        verify(userStoryRepository, times(1)).delete(userStory);
    }

    @Test
    public void testDeleteUserStory_NotFound() {
        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userStoryServiceImpl.deleteUserStory(userStoryId));

        verify(userStoryRepository, times(1)).findById(userStoryId);
        verify(userStoryRepository, never()).delete(userStory);
    }

    @Test
    public void testLinkUserStoryToEpic_Success() {
        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.of(userStory));
        when(epicServiceImpl.getEpicById(epicId)).thenReturn(epic);
        when(userStoryRepository.save(userStory)).thenReturn(userStory);

        UserStory linkedUserStory = userStoryServiceImpl.linkUserStoryToEpic(epicId, userStoryId);

        assertNotNull(linkedUserStory);
        assertEquals(epic, linkedUserStory.getEpic());

        verify(userStoryRepository, times(1)).findById(userStoryId);
        verify(epicServiceImpl, times(1)).getEpicById(epicId);
        verify(userStoryRepository, times(1)).save(userStory);
    }

    @Test
    public void testLinkUserStoryToEpic_EpicNotFound() {
        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.of(userStory));
        when(epicServiceImpl.getEpicById(epicId)).thenThrow(new NotFoundException("Epic not found."));

        assertThrows(NotFoundException.class, () -> userStoryServiceImpl.linkUserStoryToEpic(epicId, userStoryId));

        verify(userStoryRepository, times(1)).findById(userStoryId);
        verify(epicServiceImpl, times(1)).getEpicById(epicId);
        verify(userStoryRepository, never()).save(userStory);
    }
}
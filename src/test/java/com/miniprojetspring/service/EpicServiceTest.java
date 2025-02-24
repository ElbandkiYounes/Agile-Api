package com.miniprojetspring.service;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Epic;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Repository.EpicRepository;
import com.miniprojetspring.Service.EpicService;
import com.miniprojetspring.Service.ProductBacklogService;
import com.miniprojetspring.payload.CreateEpicPayload;
import com.miniprojetspring.payload.UpdateEpicPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EpicServiceTest {

    @Mock
    private EpicRepository epicRepository;

    @Mock
    private ProductBacklogService productBacklogService;

    @InjectMocks
    private EpicService epicService;

    private CreateEpicPayload createPayload;
    private UpdateEpicPayload updatePayload;
    private ProductBacklog productBacklog;
    private Epic epic;
    private UUID epicId;
    private UUID productBacklogId;

    @BeforeEach
    public void setUp() {
        productBacklogId = UUID.randomUUID();
        epicId = UUID.randomUUID();

        createPayload = new CreateEpicPayload();
        createPayload.setName("Test Epic");
        createPayload.setProductBacklogId(productBacklogId.toString());

        updatePayload = new UpdateEpicPayload();
        updatePayload.setName("Updated Epic");

        productBacklog = new ProductBacklog();
        productBacklog.setId(productBacklogId);

        epic = Epic.builder()
                .id(epicId)
                .name(createPayload.getName())
                .productBacklog(productBacklog)
                .build();
    }

    @Test
    public void testCreateEpic_Success() {
        when(productBacklogService.getProductBacklogById(productBacklogId)).thenReturn(productBacklog);
        when(epicRepository.save(any(Epic.class))).thenReturn(epic);

        Epic actualEpic = epicService.createEpic(createPayload);

        assertNotNull(actualEpic);
        assertEquals(epic.getName(), actualEpic.getName());
        assertEquals(epic.getProductBacklog(), actualEpic.getProductBacklog());

        verify(productBacklogService, times(1)).getProductBacklogById(productBacklogId);
        verify(epicRepository, times(1)).save(any(Epic.class));
    }

    @Test
    public void testCreateEpic_ProductBacklogNotFound() {
        when(productBacklogService.getProductBacklogById(productBacklogId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> epicService.createEpic(createPayload));

        verify(productBacklogService, times(1)).getProductBacklogById(productBacklogId);
        verify(epicRepository, never()).save(any(Epic.class));
    }

    @Test
    public void testGetEpicById_Success() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.of(epic));

        Epic actualEpic = epicService.getEpicById(epicId);

        assertNotNull(actualEpic);
        assertEquals(epic.getId(), actualEpic.getId());
        assertEquals(epic.getName(), actualEpic.getName());

        verify(epicRepository, times(1)).findById(epicId);
    }

    @Test
    public void testGetEpicById_NotFound() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> epicService.getEpicById(epicId));

        verify(epicRepository, times(1)).findById(epicId);
    }

    @Test
    public void testGetEpicsByProductBacklogId_Success() {
        when(productBacklogService.getProductBacklogById(productBacklogId)).thenReturn(productBacklog);
        when(epicRepository.findByProductBacklogId(productBacklogId)).thenReturn(List.of(epic));

        List<Epic> epics = epicService.getEpicsByProductBacklogId(productBacklogId);

        assertNotNull(epics);
        assertFalse(epics.isEmpty());
        assertEquals(epic.getId(), epics.get(0).getId());

        verify(productBacklogService, times(1)).getProductBacklogById(productBacklogId);
        verify(epicRepository, times(1)).findByProductBacklogId(productBacklogId);
    }

    @Test
    public void testGetEpicsByProductBacklogId_ProductBacklogNotFound() {
        when(productBacklogService.getProductBacklogById(productBacklogId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> epicService.getEpicsByProductBacklogId(productBacklogId));

        verify(productBacklogService, times(1)).getProductBacklogById(productBacklogId);
        verify(epicRepository, never()).findByProductBacklogId(productBacklogId);
    }

    @Test
    public void testDeleteEpic_Success() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.of(epic));

        epicService.deleteEpic(epicId);

        verify(epicRepository, times(1)).findById(epicId);
        verify(epicRepository, times(1)).deleteById(epicId);
    }

    @Test
    public void testDeleteEpic_NotFound() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> epicService.deleteEpic(epicId));

        verify(epicRepository, times(1)).findById(epicId);
        verify(epicRepository, never()).deleteById(epicId);
    }

    @Test
    public void testUpdateEpic_Success() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.of(epic));
        when(epicRepository.save(any(Epic.class))).thenReturn(epic);

        Epic actualEpic = epicService.updateEpic(epicId, updatePayload);

        assertNotNull(actualEpic);
        assertEquals(updatePayload.getName(), actualEpic.getName());

        verify(epicRepository, times(1)).findById(epicId);
        verify(epicRepository, times(1)).save(any(Epic.class));
    }

    @Test
    public void testUpdateEpic_NotFound() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> epicService.updateEpic(epicId, updatePayload));

        verify(epicRepository, times(1)).findById(epicId);
        verify(epicRepository, never()).save(any(Epic.class));
    }
}
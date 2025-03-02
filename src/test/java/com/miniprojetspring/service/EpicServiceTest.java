package com.miniprojetspring.service;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Epic;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Repository.EpicRepository;
import com.miniprojetspring.Service.Implementation.EpicServiceImpl;
import com.miniprojetspring.Service.Implementation.ProductBacklogServiceImpl;
import com.miniprojetspring.payload.EpicPayload;
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
    private ProductBacklogServiceImpl productBacklogServiceImpl;

    @InjectMocks
    private EpicServiceImpl epicService;

    private EpicPayload createPayload;
    private EpicPayload updatePayload;
    private ProductBacklog productBacklog;
    private Epic epic;
    private UUID epicId;
    private UUID productBacklogId;

    @BeforeEach
    public void setUp() {
        productBacklogId = UUID.randomUUID();
        epicId = UUID.randomUUID();

        createPayload = new EpicPayload();
        createPayload.setName("Test Epic");

        updatePayload = new EpicPayload();
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
        when(productBacklogServiceImpl.getProductBacklogById(String.valueOf(productBacklogId))).thenReturn(productBacklog);
        when(epicRepository.save(any(Epic.class))).thenReturn(epic);

        Epic actualEpic = epicService.createEpic(productBacklogId.toString(), createPayload);

        assertNotNull(actualEpic);
        assertEquals(epic.getName(), actualEpic.getName());
        assertEquals(epic.getProductBacklog(), actualEpic.getProductBacklog());

        verify(productBacklogServiceImpl, times(1)).getProductBacklogById(String.valueOf(productBacklogId));
        verify(epicRepository, times(1)).save(any(Epic.class));
    }

    @Test
    public void testCreateEpic_ProductBacklogNotFound() {
        UUID notFoundProductBacklogId = UUID.randomUUID();
        when(productBacklogServiceImpl.getProductBacklogById(String.valueOf(notFoundProductBacklogId))).thenReturn(null);

        assertThrows(NotFoundException.class, () -> epicService.createEpic(notFoundProductBacklogId.toString(), createPayload));

        verify(productBacklogServiceImpl, times(1)).getProductBacklogById(String.valueOf(notFoundProductBacklogId));
        verify(epicRepository, never()).save(any(Epic.class));
    }

    @Test
    public void testGetEpicById_Success() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.of(epic));

        Epic actualEpic = epicService.getEpicById(epicId.toString());

        assertNotNull(actualEpic);
        assertEquals(epic.getId(), actualEpic.getId());
        assertEquals(epic.getName(), actualEpic.getName());

        verify(epicRepository, times(1)).findById(epicId);
    }

    @Test
    public void testGetEpicById_NotFound() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> epicService.getEpicById(epicId.toString()));

        verify(epicRepository, times(1)).findById(epicId);
    }

    @Test
    public void testGetEpicsByProductBacklogId_Success() {
        when(productBacklogServiceImpl.getProductBacklogById(String.valueOf(productBacklogId))).thenReturn(productBacklog);
        when(epicRepository.findByProductBacklogId(productBacklogId)).thenReturn(List.of(epic));

        List<Epic> epics = epicService.getEpicsByProductBacklogId(productBacklogId.toString());

        assertNotNull(epics);
        assertFalse(epics.isEmpty());
        assertEquals(epic.getId(), epics.get(0).getId());

        verify(productBacklogServiceImpl, times(1)).getProductBacklogById(String.valueOf(productBacklogId));
        verify(epicRepository, times(1)).findByProductBacklogId(productBacklogId);
    }

    @Test
    public void testGetEpicsByProductBacklogId_ProductBacklogNotFound() {
        when(productBacklogServiceImpl.getProductBacklogById(String.valueOf(productBacklogId))).thenReturn(null);

        assertThrows(NotFoundException.class, () -> epicService.getEpicsByProductBacklogId(productBacklogId.toString()));

        verify(productBacklogServiceImpl, times(1)).getProductBacklogById(String.valueOf(productBacklogId));
        verify(epicRepository, never()).findByProductBacklogId(productBacklogId);
    }

    @Test
    public void testDeleteEpic_Success() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.of(epic));

        epicService.deleteEpic(epicId.toString());

        verify(epicRepository, times(1)).findById(epicId);
        verify(epicRepository, times(1)).deleteById(epicId);
    }

    @Test
    public void testDeleteEpic_NotFound() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> epicService.deleteEpic(epicId.toString()));

        verify(epicRepository, times(1)).findById(epicId);
        verify(epicRepository, never()).deleteById(epicId);
    }

    @Test
    public void testUpdateEpic_Success() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.of(epic));
        when(epicRepository.save(any(Epic.class))).thenReturn(epic);

        Epic actualEpic = epicService.updateEpic(epicId.toString(), updatePayload);

        assertNotNull(actualEpic);
        assertEquals(updatePayload.getName(), actualEpic.getName());

        verify(epicRepository, times(1)).findById(epicId);
        verify(epicRepository, times(1)).save(any(Epic.class));
    }

    @Test
    public void testUpdateEpic_NotFound() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> epicService.updateEpic(epicId.toString(), updatePayload));

        verify(epicRepository, times(1)).findById(epicId);
        verify(epicRepository, never()).save(any(Epic.class));
    }
}
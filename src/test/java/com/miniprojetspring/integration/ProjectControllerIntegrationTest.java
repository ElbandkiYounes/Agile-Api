package com.miniprojetspring.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniprojetspring.model.Previlige;
import com.miniprojetspring.model.Project;
import com.miniprojetspring.model.User;
import com.miniprojetspring.payload.InviteUserPayload;
import com.miniprojetspring.payload.ProjectPayload;
import com.miniprojetspring.repository.ProjectRepository;
import com.miniprojetspring.repository.UserRepository;
import com.miniprojetspring.service.implementation.ProjectSecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @MockitoBean
    private ProjectSecurityService projectSecurityService;

    private User currentUser;

    @BeforeEach
    void setUp() {
                projectRepository.deleteAll();
                userRepository.deleteAll();

        currentUser = User.builder()
                .fullName("Owner User")
                .email("owner@example.com")
                .password("OwnerPass123!")
                .previlige(Previlige.PRODUCT_OWNER)
                .build();

        currentUser = userRepository.save(currentUser);
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void createUpdateAndDeleteProject_flowSucceeds() throws Exception {
        ProjectPayload createPayload = ProjectPayload.builder()
                .name("Agile API")
                .description("Backlog manager")
                .build();

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(createPayload.getName()))
                .andExpect(jsonPath("$.description").value(createPayload.getDescription()));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(createPayload.getName()))
                .andExpect(jsonPath("$.description").value(createPayload.getDescription()));

        ProjectPayload updatePayload = ProjectPayload.builder()
                .name("Agile API v2")
                .description("Updated description")
                .build();

        mockMvc.perform(put("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatePayload.getName()))
                .andExpect(jsonPath("$.description").value(updatePayload.getDescription()));

        mockMvc.perform(delete("/api/projects"))
                .andExpect(status().isNoContent());

        assertThat(projectRepository.findAll()).isEmpty();
    }

    @Test
    void inviteUser_addsUserToProject() throws Exception {
        ProjectPayload projectPayload = ProjectPayload.builder()
                .name("Agile API")
                .description("Backlog manager")
                .build();

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectPayload)))
                .andExpect(status().isOk());

        List<Project> projects = projectRepository.findAll();
        assertThat(projects).hasSize(1);

        InviteUserPayload invitePayload = InviteUserPayload.builder()
                .fullName("Dev User")
                .email("dev@example.com")
                .password("DevPass123!")
                .previlige(Previlige.DEVELOPER)
                .build();

        mockMvc.perform(post("/api/projects/invite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invitePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(invitePayload.getEmail()))
                .andExpect(jsonPath("$.fullName").value(invitePayload.getFullName()))
                .andExpect(jsonPath("$.projectId").isNotEmpty());

        assertThat(userRepository.findByEmail(invitePayload.getEmail())).isPresent();
        assertThat(projectRepository.findById(projects.get(0).getId())
                .orElseThrow()
                .getUsers()).hasSize(1);
    }
}

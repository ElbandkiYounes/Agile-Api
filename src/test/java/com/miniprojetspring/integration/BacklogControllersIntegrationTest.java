package com.miniprojetspring.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniprojetspring.model.*;
import com.miniprojetspring.payload.ProductBacklogPayload;
import com.miniprojetspring.payload.RolePayload;
import com.miniprojetspring.payload.SprintBacklogPayload;
import com.miniprojetspring.payload.TestCasePayload;
import com.miniprojetspring.payload.UserStoryPayload;
import com.miniprojetspring.payload.EpicPayload;
import com.miniprojetspring.repository.*;
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
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class BacklogControllersIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProductBacklogRepository productBacklogRepository;
    @Autowired
    private SprintBacklogRepository sprintBacklogRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private EpicRepository epicRepository;
    @Autowired
    private UserStoryRepository userStoryRepository;
    @Autowired
    private TestCaseRepository testCaseRepository;

    @MockitoBean
    private ProjectSecurityService projectSecurityService;

    private User currentUser;
    private Project project;

    @BeforeEach
    void setUp() {
        testCaseRepository.deleteAll();
        userStoryRepository.deleteAll();
        epicRepository.deleteAll();
        sprintBacklogRepository.deleteAll();
        productBacklogRepository.deleteAll();
        roleRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        currentUser = userRepository.save(User.builder()
                .fullName("Owner User")
                .email("owner@example.com")
                .password("OwnerPass123!")
                .previlige(Previlige.PRODUCT_OWNER)
                .build());

        project = Project.builder()
                .name("Agile API")
                .description("Backlog manager")
                .owner(currentUser)
                .build();
        project.getUsers().add(currentUser);
        project = projectRepository.save(project);

        currentUser.setProject(project);
        userRepository.save(currentUser);

        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);
        when(projectSecurityService.isProjectOwner(anyString())).thenReturn(true);
        when(projectSecurityService.isProjectMember(anyString())).thenReturn(true);
    }

    @Test
    void productBacklog_crudFlow() throws Exception {
        ProductBacklogPayload createPayload = new ProductBacklogPayload();
        createPayload.setName("Product Backlog");

        mockMvc.perform(post("/api/product-backlogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(createPayload.getName()));

        mockMvc.perform(get("/api/product-backlogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(createPayload.getName()));

        ProductBacklogPayload updatePayload = new ProductBacklogPayload();
        updatePayload.setName("Updated Backlog");

        mockMvc.perform(put("/api/product-backlogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatePayload.getName()));

        mockMvc.perform(delete("/api/product-backlogs"))
                .andExpect(status().isNoContent());

        assertThat(productBacklogRepository.count()).isZero();
    }

    @Test
    void sprintBacklog_crudFlow() throws Exception {
        createProductBacklogIfMissing();

        SprintBacklogPayload createPayload = new SprintBacklogPayload();
        createPayload.setName("Sprint 1");
        createPayload.setDescription("Initial sprint");

        MvcResult createResult = mockMvc.perform(post("/api/sprint-backlogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(createPayload.getName()))
                .andReturn();

        String sprintId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(get("/api/sprint-backlogs/" + sprintId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sprintId));

        SprintBacklogPayload updatePayload = new SprintBacklogPayload();
        updatePayload.setName("Sprint 1 Updated");
        updatePayload.setDescription("Updated desc");

        mockMvc.perform(put("/api/sprint-backlogs/" + sprintId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatePayload.getName()));

        mockMvc.perform(delete("/api/sprint-backlogs/" + sprintId))
                .andExpect(status().isNoContent());

        assertThat(sprintBacklogRepository.count()).isZero();
    }

    @Test
    void role_crudFlow() throws Exception {
        RolePayload createPayload = new RolePayload();
        createPayload.setName("Developer");
        createPayload.setDescription("Builds features");

        MvcResult createResult = mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(createPayload.getName()))
                .andReturn();

        String roleId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(createPayload.getName()));

        mockMvc.perform(get("/api/roles/" + roleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roleId));

        RolePayload updatePayload = new RolePayload();
        updatePayload.setName("QA");
        updatePayload.setDescription("Tests features");

        mockMvc.perform(put("/api/roles/" + roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatePayload.getName()));

        mockMvc.perform(delete("/api/roles/" + roleId))
                .andExpect(status().isNoContent());

        assertThat(roleRepository.count()).isZero();
    }

    @Test
        void epic_crudFlow() throws Exception {
        createProductBacklogIfMissing();

        EpicPayload createPayload = new EpicPayload();
        createPayload.setName("Checkout");
        createPayload.setDescription("Checkout epic");
        createPayload.setEpicPriority(EpicPriority.HIGH);
        createPayload.setEpicStatus(EpicStatus.TO_DO);
        createPayload.setDueDate(Date.from(Instant.now()));

        MvcResult createResult = mockMvc.perform(post("/api/epics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(createPayload.getName()))
                .andReturn();

        String epicId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(get("/api/epics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(epicId));

        mockMvc.perform(get("/api/epics/" + epicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(epicId));

        EpicPayload updatePayload = new EpicPayload();
        updatePayload.setName("Checkout v2");
        updatePayload.setDescription("Updated");
        updatePayload.setEpicPriority(EpicPriority.MEDIUM);
        updatePayload.setEpicStatus(EpicStatus.IN_PROGRESS);
        updatePayload.setDueDate(Date.from(Instant.now().plusSeconds(3600)));

        mockMvc.perform(put("/api/epics/" + epicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatePayload.getName()))
                .andExpect(jsonPath("$.status").value(updatePayload.getEpicStatus().name()));

        mockMvc.perform(delete("/api/epics/" + epicId))
                .andExpect(status().isNoContent());

        assertThat(epicRepository.count()).isZero();
    }

    @Test
        void userStory_and_testCase_flow() throws Exception {
        createProductBacklogIfMissing();

        // Create role
        RolePayload rolePayload = new RolePayload();
        rolePayload.setName("User");
        rolePayload.setDescription("End user");
        MvcResult roleResult = mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rolePayload)))
                .andExpect(status().isOk())
                .andReturn();
        String roleId = objectMapper.readTree(roleResult.getResponse().getContentAsString()).get("id").asText();

        // Create epic to link later
        EpicPayload epicPayload = new EpicPayload();
        epicPayload.setName("Login Epic");
        epicPayload.setDescription("Login flow");
        epicPayload.setEpicPriority(EpicPriority.MEDIUM);
        epicPayload.setEpicStatus(EpicStatus.TO_DO);
        epicPayload.setDueDate(Date.from(Instant.now()));

        MvcResult epicResult = mockMvc.perform(post("/api/epics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(epicPayload)))
                .andExpect(status().isOk())
                .andReturn();
        String epicId = objectMapper.readTree(epicResult.getResponse().getContentAsString()).get("id").asText();

        // Create user story
        UserStoryPayload storyPayload = new UserStoryPayload();
        storyPayload.setTitle("As a user I log in");
        storyPayload.setDescription("Login story");
        storyPayload.setRoleId(roleId);
        storyPayload.setGoal("Access account");
        storyPayload.setDesire("View dashboard");
        storyPayload.setUserStoryPriority(UserStoryPriority.MUST_HAVE);
        storyPayload.setUserStoryStatus(UserStoryStatus.NOT_STARTED);

        MvcResult storyResult = mockMvc.perform(post("/api/user-stories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storyPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(storyPayload.getTitle()))
                .andReturn();
        JsonNode storyNode = objectMapper.readTree(storyResult.getResponse().getContentAsString());
        String storyId = storyNode.get("id").asText();

        mockMvc.perform(get("/api/user-stories/" + storyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(storyId));

        // Link user story to epic and validate filters
        mockMvc.perform(post("/api/user-stories/" + storyId + "/link/epic/" + epicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.epicId").value(epicId));

        mockMvc.perform(get("/api/user-stories/roles/" + roleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(storyId));

        mockMvc.perform(get("/api/user-stories/epics/" + epicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(storyId));

        storyPayload.setTitle("As a user I log in securely");
        storyPayload.setUserStoryStatus(UserStoryStatus.IN_PROGRESS);

        mockMvc.perform(put("/api/user-stories/" + storyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storyPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(storyPayload.getTitle()));

        // Create test case for the story
        TestCasePayload testCasePayload = TestCasePayload.builder()
                .title("Valid credentials")
                .description("User logs in with valid creds")
                .result(TestCaseResult.PASS)
                .build();

        MvcResult testCaseResult = mockMvc.perform(post("/api/test-cases/user-stories/" + storyId + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCasePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(testCasePayload.getTitle()))
                .andReturn();
        String testCaseId = objectMapper.readTree(testCaseResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(get("/api/test-cases/" + testCaseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCaseId));

        mockMvc.perform(get("/api/test-cases/user-stories/" + storyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testCaseId));

        TestCasePayload updateTestCase = TestCasePayload.builder()
                .title("Invalid credentials")
                .description("User fails login")
                .result(TestCaseResult.FAIL)
                .build();

        mockMvc.perform(put("/api/test-cases/" + testCaseId + "/user-stories/" + storyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTestCase)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updateTestCase.getTitle()))
                .andExpect(jsonPath("$.result").value(updateTestCase.getResult().name()));

        mockMvc.perform(delete("/api/test-cases/" + testCaseId))
                .andExpect(status().isNoContent());

        assertThat(testCaseRepository.count()).isZero();
    }

    @Test
    void epic_link_and_unlink_sprintBacklog_flow() throws Exception {
        createProductBacklogIfMissing();

        // Create sprint backlog
        SprintBacklogPayload sprintPayload = new SprintBacklogPayload();
        sprintPayload.setName("Sprint Link");
        sprintPayload.setDescription("Sprint for linking");

        MvcResult sprintResult = mockMvc.perform(post("/api/sprint-backlogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprintPayload)))
                .andExpect(status().isOk())
                .andReturn();
        String sprintId = objectMapper.readTree(sprintResult.getResponse().getContentAsString()).get("id").asText();

        // Create role and user story
        RolePayload rolePayload = new RolePayload();
        rolePayload.setName("RoleLink");
        rolePayload.setDescription("Role for linking");
        String roleId = objectMapper.readTree(mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rolePayload)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).get("id").asText();

        UserStoryPayload storyPayload = new UserStoryPayload();
        storyPayload.setTitle("Story to link");
        storyPayload.setDescription("Desc");
        storyPayload.setRoleId(roleId);
        storyPayload.setGoal("Goal");
        storyPayload.setDesire("Desire");
        storyPayload.setUserStoryPriority(UserStoryPriority.MUST_HAVE);
        storyPayload.setUserStoryStatus(UserStoryStatus.NOT_STARTED);

        String storyId = objectMapper.readTree(mockMvc.perform(post("/api/user-stories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storyPayload)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).get("id").asText();

        // Create epic and link story to epic
        EpicPayload epicPayload = new EpicPayload();
        epicPayload.setName("Linkable Epic");
        epicPayload.setDescription("Epic to link to sprint");
        epicPayload.setEpicPriority(EpicPriority.LOW);
        epicPayload.setEpicStatus(EpicStatus.TO_DO);
        epicPayload.setDueDate(Date.from(Instant.now()));

        String epicId = objectMapper.readTree(mockMvc.perform(post("/api/epics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(epicPayload)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(post("/api/user-stories/" + storyId + "/link/epic/" + epicId))
                .andExpect(status().isOk());

        // Link epic to sprint backlog
        mockMvc.perform(post("/api/epics/" + epicId + "/link/sprint-backlog/" + sprintId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sprintBacklogId").value(sprintId));

        // Unlink epic from sprint backlog
        mockMvc.perform(post("/api/epics/" + epicId + "/unlink"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sprintBacklogId").value(nullValue()));
    }

    private void createProductBacklogIfMissing() throws Exception {
        if (productBacklogRepository.count() == 0) {
            ProductBacklogPayload payload = new ProductBacklogPayload();
            payload.setName("PB");
            mockMvc.perform(post("/api/product-backlogs")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpect(status().isOk());
        }
    }
}

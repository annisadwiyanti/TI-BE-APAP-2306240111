package apap.ti._5.tour_package_2306240111_be.restcontroller;

import apap.ti._5.tour_package_2306240111_be.model.Activity;
import apap.ti._5.tour_package_2306240111_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306240111_be.model.Package;
import apap.ti._5.tour_package_2306240111_be.model.Plan;
import apap.ti._5.tour_package_2306240111_be.repository.ActivityRepository;
import apap.ti._5.tour_package_2306240111_be.repository.OrderedQuantityRepository;
import apap.ti._5.tour_package_2306240111_be.repository.PackageRepository;
import apap.ti._5.tour_package_2306240111_be.repository.PlanRepository;
import apap.ti._5.tour_package_2306240111_be.restdto.request.plan.CreatePlanRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.request.plan.UpdatePlanRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restservice.PlanRestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class PlanRestControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private PlanRestService planRestService;

    @MockBean
    private PackageRepository packageRepository;

    @MockBean
    private PlanRepository planRepository;

    @MockBean
    private OrderedQuantityRepository orderedQuantityRepository;

    @MockBean
    private ActivityRepository activityRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper;

    private Plan testPlan;
    private Package testPackage;
    private Activity testActivity;
    private OrderedQuantity testOrderedQuantity;
    private UUID planId;
    private String packageId;
    private String activityId;

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Configure ObjectMapper to handle LocalDateTime
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        planId = UUID.randomUUID();
        packageId = "PKG-2025-001";
        activityId = "ACT-001";

        testPackage = Package.builder()
                .id(packageId)
                .packageName("Bali Holiday")
                .userId("USER-001")
                .quota(50)
                .price(5000000L)
                .status("Pending")
                .startDate(LocalDateTime.of(2025, 12, 15, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 20, 18, 0))
                .build();

        testPlan = Plan.builder()
                .id(planId)
                .packageId(packageId)
                .planName("Day 1 - Flight")
                .price(1500000L)
                .activityType("Flight")
                .status("ACTIVE")
                .startDate(LocalDateTime.of(2025, 12, 15, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 15, 12, 0))
                .startLocation("Jakarta")
                .endLocation("Bali")
                .build();

        testActivity = Activity.builder()
                .id(activityId)
                .activityName("Jakarta to Bali Flight")
                .activityItem("Garuda Indonesia")
                .capacity(100)
                .price(1500000L)
                .activityType("Flight")
                .startDate(LocalDateTime.of(2025, 12, 15, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 15, 12, 0))
                .startLocation("Jakarta")
                .endLocation("Bali")
                .build();

        testOrderedQuantity = OrderedQuantity.builder()
                .id(UUID.randomUUID())
                .planId(planId)
                .activityId(activityId)
                .orderedQuota(10)
                .quota(100)
                .price(1500000L)
                .startDate(LocalDateTime.of(2025, 12, 15, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 15, 12, 0))
                .build();
    }

    @Test
    void testGetAllActivitiesSuccess() throws Exception {
        List<Activity> activityList = new ArrayList<>();
        activityList.add(testActivity);

        when(activityRepository.findAll()).thenReturn(activityList);

        mockMvc.perform(get("/api/activities")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success retrieve all activities"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(activityId))
                .andExpect(jsonPath("$.data[0].activityName").value("Jakarta to Bali Flight"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(activityRepository, times(1)).findAll();
    }

    @Test
    void testGetAllActivitiesEmpty() throws Exception {
        when(activityRepository.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/activities")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success retrieve all activities"))
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(activityRepository, times(1)).findAll();
    }

    @Test
    void testGetAllActivitiesException() throws Exception {
        when(activityRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/activities")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Error: Database error"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(activityRepository, times(1)).findAll();
    }

    @Test
    void testGetAvailableActivitiesForPlanSuccess() throws Exception {
        List<Activity> allActivities = new ArrayList<>();
        allActivities.add(testActivity);

        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(activityRepository.findAll()).thenReturn(allActivities);

        mockMvc.perform(get("/api/plans/{planId}/available-activities", planId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success retrieve available activities for plan"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(activityId))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(planRepository, times(1)).findById(planId);
        verify(activityRepository, times(1)).findAll();
    }

    @Test
    void testGetAvailableActivitiesForPlanEmpty() throws Exception {
        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(activityRepository.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/plans/{planId}/available-activities", planId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success retrieve available activities for plan"))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(planRepository, times(1)).findById(planId);
    }

    @Test
    void testGetAvailableActivitiesForPlanNotFound() throws Exception {
        when(planRepository.findById(planId))
            .thenReturn(Optional.empty());  

        mockMvc.perform(get("/api/plans/{planId}/available-activities", planId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message", containsString("Plan not found")))  
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(planRepository, times(1)).findById(planId);
    }

    @Test
    void testGetAvailableActivitiesException() throws Exception {
        when(planRepository.findById(planId)).thenThrow(new RuntimeException("Invalid UUID"));

        mockMvc.perform(get("/api/plans/{planId}/available-activities", planId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(planRepository, times(1)).findById(planId);
    }

    @Test
    void testGetCreatePlanFormSuccess() throws Exception {
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));

        mockMvc.perform(get("/api/packages/{packageId}/plans/create", packageId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Create plan form structure"))
                .andExpect(jsonPath("$.data.packageId").value(packageId))
                .andExpect(jsonPath("$.data.packageName").value("Bali Holiday"))
                .andExpect(jsonPath("$.data.fields.planName").value("text input"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(packageRepository, times(1)).findById(packageId);
    }

    @Test
    void testGetCreatePlanFormPackageNotFound() throws Exception {
        when(packageRepository.findById(packageId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/packages/{packageId}/plans/create", packageId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error: Package not found"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(packageRepository, times(1)).findById(packageId);
    }

    @Test
    void testGetCreatePlanFormInvalidPackageStatus() throws Exception {
        Package invalidPackage = testPackage.toBuilder()
            .status("Active")  // Bukan "Pending"
            .build();
        
        when(packageRepository.findById(packageId))
            .thenReturn(Optional.of(invalidPackage));

        mockMvc.perform(get("/api/packages/{packageId}/plans/create", packageId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Cannot create plan"))) 
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(packageRepository, times(1)).findById(packageId);
    }

    @Test
    void testCreatePlanSuccess() throws Exception {
        CreatePlanRequestDTO request = CreatePlanRequestDTO.builder()
                .planName("Day 1 - Flight")
                .activityType("Flight")
                .startDate(LocalDateTime.of(2025, 12, 15, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 15, 12, 0))
                .startLocation("Jakarta")
                .endLocation("Bali")
                .build();

        when(planRestService.createPlan(eq(packageId), any(CreatePlanRequestDTO.class)))
                .thenReturn(testPlan);

        mockMvc.perform(post("/api/packages/{packageId}/plans/create", packageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Plan created successfully"))
                .andExpect(jsonPath("$.data.id").value(planId.toString()))
                .andExpect(jsonPath("$.data.planName").value("Day 1 - Flight"))
                .andExpect(jsonPath("$.data.packageId").value(packageId))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(planRestService, times(1)).createPlan(eq(packageId), any(CreatePlanRequestDTO.class));
    }

    @Test
    void testCreatePlanRuntimeException() throws Exception {
        CreatePlanRequestDTO request = CreatePlanRequestDTO.builder()
                .planName("Day 1")
                .activityType("Flight")
                .startDate(LocalDateTime.of(2025, 12, 15, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 15, 12, 0))
                .startLocation("Jakarta")
                .endLocation("Bali")
                .build();

        when(planRestService.createPlan(eq(packageId), any(CreatePlanRequestDTO.class)))
                .thenThrow(new RuntimeException("Package not found"));

        mockMvc.perform(post("/api/packages/{packageId}/plans/create", packageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error: Package not found"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(planRestService, times(1)).createPlan(eq(packageId), any(CreatePlanRequestDTO.class));
    }

    @Test
    void testCreatePlanException() throws Exception {
        CreatePlanRequestDTO request = CreatePlanRequestDTO.builder()
                .planName("Day 1")
                .activityType("Flight")
                .startDate(LocalDateTime.of(2025, 12, 15, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 15, 12, 0))
                .startLocation("Jakarta")
                .endLocation("Bali")
                .build();

        // When createPlan throws RuntimeException, it's caught and returns BAD_REQUEST (400)
        when(planRestService.createPlan(eq(packageId), any(CreatePlanRequestDTO.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/packages/{packageId}/plans/create", packageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Database error")));

        verify(planRestService, times(1)).createPlan(eq(packageId), any(CreatePlanRequestDTO.class));
    }

    @Test
    void testGetPlanDetailSuccess() throws Exception {
        List<OrderedQuantity> orderedQuantities = new ArrayList<>();
        orderedQuantities.add(testOrderedQuantity);

        when(planRestService.getPlanById(planId)).thenReturn(testPlan);
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(orderedQuantities);
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(testActivity));

        mockMvc.perform(get("/api/plans/{planId}", planId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success retrieve plan detail"))
                .andExpect(jsonPath("$.data.id").value(planId.toString()))
                .andExpect(jsonPath("$.data.planName").value("Day 1 - Flight"))
                .andExpect(jsonPath("$.data.packageName").value("Bali Holiday"))
                .andExpect(jsonPath("$.data.orderedActivities", hasSize(1)))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(planRestService, times(1)).getPlanById(planId);
        verify(packageRepository, times(1)).findById(packageId);
    }

    @Test
    void testGetPlanDetailNotFound() throws Exception {
        when(planRestService.getPlanById(planId))
                .thenThrow(new RuntimeException("Plan not found"));

        mockMvc.perform(get("/api/plans/{planId}", planId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Error: Plan not found"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(planRestService, times(1)).getPlanById(planId);
    }

    @Test
    void testGetPlanDetailException() throws Exception {
        // When getPlanById throws RuntimeException, it's caught and returns NOT_FOUND (404)
        when(planRestService.getPlanById(planId))
                .thenThrow(new RuntimeException("Server error"));

        mockMvc.perform(get("/api/plans/{planId}", planId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("Server error")));

        verify(planRestService, times(1)).getPlanById(planId);
    }

    @Test
    void testGetEditPlanFormSuccess() throws Exception {
        List<OrderedQuantity> orderedQuantities = new ArrayList<>();
        orderedQuantities.add(testOrderedQuantity);

        when(planRestService.getPlanById(planId)).thenReturn(testPlan);
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(orderedQuantities);
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(testActivity));

        mockMvc.perform(get("/api/plans/{id}/edit", planId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Edit plan form data retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(planId.toString()))
                .andExpect(jsonPath("$.data.orderedActivities", hasSize(1)))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(planRestService, times(1)).getPlanById(planId);
    }

    @Test
    void testGetEditPlanFormInvalidId() throws Exception {
        when(planRestService.getPlanById(any(UUID.class)))
                .thenThrow(new RuntimeException("Plan not found"));

        mockMvc.perform(get("/api/plans/{id}/edit", planId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error: Plan not found"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(planRestService, times(1)).getPlanById(planId);
    }

    @Test
    void testUpdatePlanSuccess() throws Exception {
        UpdatePlanRequestDTO request = UpdatePlanRequestDTO.builder()
                .planName("Updated Day 1")
                .startDate(LocalDateTime.of(2025, 12, 15, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 15, 12, 0))
                .startLocation("Jakarta")
                .endLocation("Bali")
                .build();

        Plan updatedPlan = testPlan.toBuilder()
                .planName("Updated Day 1")
                .build();

        when(planRestService.updatePlan(eq(planId), any(UpdatePlanRequestDTO.class)))
                .thenReturn(updatedPlan);

        mockMvc.perform(put("/api/plans/{id}/edit", planId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Plan updated successfully"))
                .andExpect(jsonPath("$.data.id").value(planId.toString()))
                .andExpect(jsonPath("$.data.planName").value("Updated Day 1"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(planRestService, times(1)).updatePlan(eq(planId), any(UpdatePlanRequestDTO.class));
    }

    @Test
    void testUpdatePlanBadRequest() throws Exception {
        UpdatePlanRequestDTO request = UpdatePlanRequestDTO.builder()
                .planName("Updated Day")
                .startDate(LocalDateTime.of(2025, 12, 15, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 15, 12, 0))
                .startLocation("Jakarta")
                .endLocation("Bali")
                .build();

        when(planRestService.updatePlan(eq(planId), any(UpdatePlanRequestDTO.class)))
                .thenThrow(new RuntimeException("Plan is locked"));

        mockMvc.perform(put("/api/plans/{id}/edit", planId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error: Plan is locked"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(planRestService, times(1)).updatePlan(eq(planId), any(UpdatePlanRequestDTO.class));
    }

    @Test
    void testUpdatePlanException() throws Exception {
        UpdatePlanRequestDTO request = UpdatePlanRequestDTO.builder()
                .planName("Updated Day")
                .startDate(LocalDateTime.of(2025, 12, 15, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 15, 12, 0))
                .startLocation("Jakarta")
                .endLocation("Bali")
                .build();

        // When updatePlan throws RuntimeException, it's caught and returns BAD_REQUEST (400)
        when(planRestService.updatePlan(eq(planId), any(UpdatePlanRequestDTO.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(put("/api/plans/{id}/edit", planId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Database error")));

        verify(planRestService, times(1)).updatePlan(eq(planId), any(UpdatePlanRequestDTO.class));
    }

    @Test
    void testDeletePlanSuccess() throws Exception {
        doNothing().when(planRestService).deletePlan(planId);

        mockMvc.perform(delete("/api/plans/{id}", planId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Plan deleted successfully"))
                .andExpect(jsonPath("$.data").value("Plan with id " + planId.toString() + " has been deleted"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(planRestService, times(1)).deletePlan(planId);
    }

    @Test
    void testDeletePlanBadRequest() throws Exception {
        doThrow(new RuntimeException("Plan has activities"))
                .when(planRestService).deletePlan(planId);

        mockMvc.perform(delete("/api/plans/{id}", planId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error: Plan has activities"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(planRestService, times(1)).deletePlan(planId);
    }

    @Test
    void testDeletePlanException() throws Exception {
        // When deletePlan throws RuntimeException, it's caught and returns BAD_REQUEST (400)
        doThrow(new RuntimeException("Database error"))
                .when(planRestService).deletePlan(planId);

        mockMvc.perform(delete("/api/plans/{id}", planId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Database error")));

        verify(planRestService, times(1)).deletePlan(planId);
    }

    @Test
    void testGetPlanDetailNoOrderedActivities() throws Exception {
        when(planRestService.getPlanById(planId)).thenReturn(testPlan);
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/plans/{planId}", planId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.orderedActivities", hasSize(0)));

        verify(planRestService, times(1)).getPlanById(planId);
    }

    @Test
    void testGetAllActivitiesResponseStructure() throws Exception {
        List<Activity> activityList = new ArrayList<>();
        activityList.add(testActivity);

        when(activityRepository.findAll()).thenReturn(activityList);

        mockMvc.perform(get("/api/activities")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").isArray());

        verify(activityRepository, times(1)).findAll();
    }

    @Test
    void testCreatePlanResponseStructure() throws Exception {
        CreatePlanRequestDTO request = CreatePlanRequestDTO.builder()
                .planName("Day 1")
                .activityType("Flight")
                .startDate(LocalDateTime.of(2025, 12, 15, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 15, 12, 0))
                .startLocation("Jakarta")
                .endLocation("Bali")
                .build();

        when(planRestService.createPlan(eq(packageId), any(CreatePlanRequestDTO.class)))
                .thenReturn(testPlan);

        mockMvc.perform(post("/api/packages/{packageId}/plans/create", packageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").exists());

        verify(planRestService, times(1)).createPlan(eq(packageId), any(CreatePlanRequestDTO.class));
    }

    @Test
    void testGetEditPlanFormEmptyOrderedActivities() throws Exception {
        when(planRestService.getPlanById(planId)).thenReturn(testPlan);
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/plans/{id}/edit", planId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderedActivities", hasSize(0)));

        verify(planRestService, times(1)).getPlanById(planId);
    }

    @Test
    void testGetAvailableActivitiesMultipleActivities() throws Exception {
        List<Activity> allActivities = new ArrayList<>();
        allActivities.add(testActivity);
        Activity activity2 = testActivity.toBuilder()
                .id("ACT-002")
                .activityName("Bali Accommodation")
                .activityType("Accommodation")
                .build();
        allActivities.add(activity2);

        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(activityRepository.findAll()).thenReturn(allActivities);

        mockMvc.perform(get("/api/plans/{planId}/available-activities", planId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].activityType").value("Flight"));

        verify(planRepository, times(1)).findById(planId);
    }

    @Test
    void testGetPlanDetailNoActivityFound() throws Exception {
        List<OrderedQuantity> orderedQuantities = new ArrayList<>();
        orderedQuantities.add(testOrderedQuantity);

        when(planRestService.getPlanById(planId)).thenReturn(testPlan);
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(orderedQuantities);
        when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/plans/{planId}", planId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.orderedActivities[0].activityName").value(""))
                .andExpect(jsonPath("$.data.orderedActivities[0].activityItem").value(""));

        verify(planRestService, times(1)).getPlanById(planId);
    }
}
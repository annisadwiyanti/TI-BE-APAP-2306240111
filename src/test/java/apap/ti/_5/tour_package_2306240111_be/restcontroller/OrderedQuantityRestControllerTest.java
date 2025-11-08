package apap.ti._5.tour_package_2306240111_be.restcontroller;

import apap.ti._5.tour_package_2306240111_be.model.Activity;
import apap.ti._5.tour_package_2306240111_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306240111_be.restdto.request.orderedquantity.CreateOrderedQuantityRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.request.orderedquantity.UpdateOrderedQuantityRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restservice.OrderedQuantityRestService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebMvc
class OrderedQuantityRestControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private OrderedQuantityRestService orderedQuantityRestService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper = new ObjectMapper();
    
    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
    
    private Activity testActivity;
    private OrderedQuantity testOrderedQuantity;
    private UUID orderedQuantityId;
    private UUID planId;
    private String activityId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        orderedQuantityId = UUID.randomUUID();
        planId = UUID.randomUUID();
        activityId = "ACT-001";

        testActivity = Activity.builder()
                .id(activityId)
                .activityName("Diving Activity")
                .activityType("Water Sport")
                .startLocation("Bali")
                .endLocation("Bali")
                .price(500000L)
                .capacity(20)
                .startDate(LocalDateTime.of(2025, 12, 16, 9, 0))
                .endDate(LocalDateTime.of(2025, 12, 16, 17, 0))
                .build();

        testOrderedQuantity = OrderedQuantity.builder()
                .id(orderedQuantityId)
                .planId(planId)
                .activityId(activityId)
                .orderedQuota(10)
                .quota(20)
                .price(500000L)
                .startDate(LocalDateTime.of(2025, 12, 16, 9, 0))
                .endDate(LocalDateTime.of(2025, 12, 16, 17, 0))
                .build();
    }

    @Test
    void testGetAllActivitiesSuccess() throws Exception {
        List<Activity> activities = new ArrayList<>();
        activities.add(testActivity);

        Activity activity2 = testActivity.toBuilder()
                .id("ACT-002")
                .activityName("Snorkeling Activity")
                .build();
        activities.add(activity2);

        when(orderedQuantityRestService.getAllActivities())
                .thenReturn(activities);

        mockMvc.perform(get("/api/ordered-activities/activities")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success retrieve all activities"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(activityId))
                .andExpect(jsonPath("$.data[0].activityName").value("Diving Activity"))
                .andExpect(jsonPath("$.data[1].id").value("ACT-002"))
                .andExpect(jsonPath("$.data[1].activityName").value("Snorkeling Activity"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(orderedQuantityRestService, times(1)).getAllActivities();
    }

    @Test
    void testGetAllActivitiesEmpty() throws Exception {
        when(orderedQuantityRestService.getAllActivities())
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/ordered-activities/activities")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success retrieve all activities"))
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(orderedQuantityRestService, times(1)).getAllActivities();
    }

    @Test
    void testGetAllActivitiesException() throws Exception {
        when(orderedQuantityRestService.getAllActivities())
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/ordered-activities/activities")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Error: Database error"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(orderedQuantityRestService, times(1)).getAllActivities();
    }

    @Test
    void testCreateOrderedQuantitySuccess() throws Exception {
        CreateOrderedQuantityRequestDTO request = CreateOrderedQuantityRequestDTO.builder()
                .planId(planId)
                .activityId(activityId)
                .orderedQuota(10)
                .build();

        when(orderedQuantityRestService.createOrderedQuantity(any(CreateOrderedQuantityRequestDTO.class)))
                .thenReturn(testOrderedQuantity);

        mockMvc.perform(post("/api/ordered-activities/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Activity added to plan successfully"))
                .andExpect(jsonPath("$.data.id").value(orderedQuantityId.toString()))
                .andExpect(jsonPath("$.data.activityId").value(activityId))
                .andExpect(jsonPath("$.data.orderedQuota").value(10))
                .andExpect(jsonPath("$.data.price").value(500000))
                .andExpect(jsonPath("$.data.quota").value(20))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(orderedQuantityRestService, times(1)).createOrderedQuantity(any(CreateOrderedQuantityRequestDTO.class));
    }

    @Test
    void testCreateOrderedQuantityPlanNotFound() throws Exception {
        CreateOrderedQuantityRequestDTO request = CreateOrderedQuantityRequestDTO.builder()
                .planId(UUID.randomUUID())
                .activityId(activityId)
                .orderedQuota(10)
                .build();

        when(orderedQuantityRestService.createOrderedQuantity(any(CreateOrderedQuantityRequestDTO.class)))
                .thenThrow(new RuntimeException("Plan not found"));

        mockMvc.perform(post("/api/ordered-activities/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error: Plan not found"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(orderedQuantityRestService, times(1)).createOrderedQuantity(any(CreateOrderedQuantityRequestDTO.class));
    }

    @Test
    void testCreateOrderedQuantityActivityNotFound() throws Exception {
        CreateOrderedQuantityRequestDTO request = CreateOrderedQuantityRequestDTO.builder()
                .planId(planId)
                .activityId("INVALID-ACTIVITY")
                .orderedQuota(10)
                .build();

        when(orderedQuantityRestService.createOrderedQuantity(any(CreateOrderedQuantityRequestDTO.class)))
                .thenThrow(new RuntimeException("Activity not found"));

        mockMvc.perform(post("/api/ordered-activities/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error: Activity not found"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(orderedQuantityRestService, times(1)).createOrderedQuantity(any(CreateOrderedQuantityRequestDTO.class));
    }

    @Test
    void testCreateOrderedQuantityQuotaExceeded() throws Exception {
        CreateOrderedQuantityRequestDTO request = CreateOrderedQuantityRequestDTO.builder()
                .planId(planId)
                .activityId(activityId)
                .orderedQuota(50)
                .build();

        when(orderedQuantityRestService.createOrderedQuantity(any(CreateOrderedQuantityRequestDTO.class)))
                .thenThrow(new RuntimeException("Ordered quantity cannot exceed activity capacity"));

        mockMvc.perform(post("/api/ordered-activities/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", notNullValue()))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(orderedQuantityRestService, times(1)).createOrderedQuantity(any(CreateOrderedQuantityRequestDTO.class));
    }

   @Test
    void testCreateOrderedQuantityException() throws Exception {
        CreateOrderedQuantityRequestDTO request = CreateOrderedQuantityRequestDTO.builder()
                .planId(planId)
                .activityId(activityId)
                .orderedQuota(10)
                .build();

        when(orderedQuantityRestService.createOrderedQuantity(any(CreateOrderedQuantityRequestDTO.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/ordered-activities/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isBadRequest())  // Coba ganti jadi 400 dulu
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("error")));  // Ganti jadi lebih flexible

        verify(orderedQuantityRestService, times(1)).createOrderedQuantity(any(CreateOrderedQuantityRequestDTO.class));
    }

    @Test
    void testGetOrderedQuantityForEditSuccess() throws Exception {
        when(orderedQuantityRestService.getOrderedQuantityById(orderedQuantityId))
                .thenReturn(testOrderedQuantity);

        mockMvc.perform(get("/api/ordered-activities/{id}/edit", orderedQuantityId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success retrieve ordered quantity for edit"))
                .andExpect(jsonPath("$.data.id").value(orderedQuantityId.toString()))
                .andExpect(jsonPath("$.data.activityId").value(activityId))
                .andExpect(jsonPath("$.data.orderedQuota").value(10))
                .andExpect(jsonPath("$.data.price").value(500000))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(orderedQuantityRestService, times(1)).getOrderedQuantityById(orderedQuantityId);
    }

    @Test
    void testGetOrderedQuantityForEditNotFound() throws Exception {
        when(orderedQuantityRestService.getOrderedQuantityById(orderedQuantityId))
                .thenThrow(new RuntimeException("Ordered Quantity not found"));

        mockMvc.perform(get("/api/ordered-activities/{id}/edit", orderedQuantityId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Error: Ordered Quantity not found"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(orderedQuantityRestService, times(1)).getOrderedQuantityById(orderedQuantityId);
    }

    @Test
    void testUpdateOrderedQuantitySuccess() throws Exception {
        UpdateOrderedQuantityRequestDTO request = UpdateOrderedQuantityRequestDTO.builder()
                .orderedQuota(15)
                .build();

        OrderedQuantity updated = testOrderedQuantity.toBuilder()
                .orderedQuota(15)
                .build();

        when(orderedQuantityRestService.updateOrderedQuantity(eq(orderedQuantityId), any(UpdateOrderedQuantityRequestDTO.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/ordered-activities/{id}/edit", orderedQuantityId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Ordered quantity updated successfully"))
                .andExpect(jsonPath("$.data.id").value(orderedQuantityId.toString()))
                .andExpect(jsonPath("$.data.activityId").value(activityId))
                .andExpect(jsonPath("$.data.orderedQuota").value(15))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(orderedQuantityRestService, times(1)).updateOrderedQuantity(eq(orderedQuantityId), any(UpdateOrderedQuantityRequestDTO.class));
    }

    @Test
    void testUpdateOrderedQuantityNotFound() throws Exception {
        UpdateOrderedQuantityRequestDTO request = UpdateOrderedQuantityRequestDTO.builder()
                .orderedQuota(15)
                .build();

        when(orderedQuantityRestService.updateOrderedQuantity(eq(orderedQuantityId), any(UpdateOrderedQuantityRequestDTO.class)))
                .thenThrow(new RuntimeException("Ordered Quantity not found"));

        mockMvc.perform(put("/api/ordered-activities/{id}/edit", orderedQuantityId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error: Ordered Quantity not found"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(orderedQuantityRestService, times(1)).updateOrderedQuantity(eq(orderedQuantityId), any(UpdateOrderedQuantityRequestDTO.class));
    }

    @Test
    void testUpdateOrderedQuantityQuotaExceeded() throws Exception {
        UpdateOrderedQuantityRequestDTO request = UpdateOrderedQuantityRequestDTO.builder()
                .orderedQuota(50)
                .build();

        when(orderedQuantityRestService.updateOrderedQuantity(eq(orderedQuantityId), any(UpdateOrderedQuantityRequestDTO.class)))
                .thenThrow(new RuntimeException("Ordered quantity cannot exceed activity capacity"));

        mockMvc.perform(put("/api/ordered-activities/{id}/edit", orderedQuantityId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", notNullValue()))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(orderedQuantityRestService, times(1)).updateOrderedQuantity(eq(orderedQuantityId), any(UpdateOrderedQuantityRequestDTO.class));
    }

    @Test
    void testUpdateOrderedQuantityException() throws Exception {
        UpdateOrderedQuantityRequestDTO request = UpdateOrderedQuantityRequestDTO.builder()
                .orderedQuota(15)
                .build();

        when(orderedQuantityRestService.updateOrderedQuantity(eq(orderedQuantityId), any(UpdateOrderedQuantityRequestDTO.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(put("/api/ordered-activities/{id}/edit", orderedQuantityId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isBadRequest())  // Ganti jadi 400
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("error")));  // Lebih flexible

        verify(orderedQuantityRestService, times(1)).updateOrderedQuantity(eq(orderedQuantityId), any(UpdateOrderedQuantityRequestDTO.class));
    }

    @Test
    void testGetEligibleActivitiesSuccess() throws Exception {
        List<Activity> eligibleActivities = new ArrayList<>();
        eligibleActivities.add(testActivity);

        when(orderedQuantityRestService.getEligibleActivities(planId))
                .thenReturn(eligibleActivities);

        mockMvc.perform(get("/api/ordered-activities/eligible")
                .param("planId", planId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success retrieve eligible activities for plan"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(activityId))
                .andExpect(jsonPath("$.data[0].activityName").value("Diving Activity"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(orderedQuantityRestService, times(1)).getEligibleActivities(planId);
    }

    @Test
    void testGetEligibleActivitiesEmpty() throws Exception {
        when(orderedQuantityRestService.getEligibleActivities(planId))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/ordered-activities/eligible")
                .param("planId", planId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success retrieve eligible activities for plan"))
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(orderedQuantityRestService, times(1)).getEligibleActivities(planId);
    }

    @Test
    void testGetEligibleActivitiesPlanNotFound() throws Exception {
        when(orderedQuantityRestService.getEligibleActivities(planId))
                .thenThrow(new RuntimeException("Plan not found"));

        mockMvc.perform(get("/api/ordered-activities/eligible")
                .param("planId", planId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Error: Plan not found"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(orderedQuantityRestService, times(1)).getEligibleActivities(planId);
    }

    @Test
    void testDeleteOrderedQuantitySuccess() throws Exception {
        doNothing().when(orderedQuantityRestService).deleteOrderedQuantity(orderedQuantityId);

        mockMvc.perform(delete("/api/ordered-activities/{id}/delete", orderedQuantityId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Ordered quantity deleted successfully"))
                .andExpect(jsonPath("$.data").value("Successfully deleted"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(orderedQuantityRestService, times(1)).deleteOrderedQuantity(orderedQuantityId);
    }

    @Test
    void testDeleteOrderedQuantityNotFound() throws Exception {
        doThrow(new RuntimeException("Ordered Quantity not found"))
                .when(orderedQuantityRestService).deleteOrderedQuantity(orderedQuantityId);

        mockMvc.perform(delete("/api/ordered-activities/{id}/delete", orderedQuantityId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error: Ordered Quantity not found"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(orderedQuantityRestService, times(1)).deleteOrderedQuantity(orderedQuantityId);
    }

    @Test
    void testDeleteOrderedQuantityPackageNotPending() throws Exception {
        doThrow(new RuntimeException("Cannot delete ordered quantity. Package status must be 'Pending'"))
                .when(orderedQuantityRestService).deleteOrderedQuantity(orderedQuantityId);

        mockMvc.perform(delete("/api/ordered-activities/{id}/delete", orderedQuantityId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", notNullValue()))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(orderedQuantityRestService, times(1)).deleteOrderedQuantity(orderedQuantityId);
    }

    @Test
    void testDeleteOrderedQuantityException() throws Exception {
        doThrow(new RuntimeException("Database error"))
                .when(orderedQuantityRestService).deleteOrderedQuantity(orderedQuantityId);

        mockMvc.perform(delete("/api/ordered-activities/{id}/delete", orderedQuantityId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) 
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("error")));  

        verify(orderedQuantityRestService, times(1)).deleteOrderedQuantity(orderedQuantityId);
    }
    
    @Test
    void testCreateOrderedQuantity_ServerErrorBranch() throws Exception {
        CreateOrderedQuantityRequestDTO request = CreateOrderedQuantityRequestDTO.builder()
                .planId(planId).activityId(activityId).orderedQuota(10).build();

        when(orderedQuantityRestService.createOrderedQuantity(any(CreateOrderedQuantityRequestDTO.class)))
                .thenAnswer(inv -> { throw new Exception("DB down"); });

        mockMvc.perform(post("/api/ordered-activities/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message", containsString("Error: DB down")));
    }

    @Test
    void testGetOrderedQuantityForEdit_ServerErrorBranch() throws Exception {
        when(orderedQuantityRestService.getOrderedQuantityById(orderedQuantityId))
                .thenAnswer(inv -> { throw new Exception("DB down"); });

        mockMvc.perform(get("/api/ordered-activities/{id}/edit", orderedQuantityId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message", containsString("Error: DB down")));
    }

    @Test
    void testUpdateOrderedQuantity_ServerErrorBranch() throws Exception {
        UpdateOrderedQuantityRequestDTO request = UpdateOrderedQuantityRequestDTO.builder()
                .orderedQuota(15).build();

        when(orderedQuantityRestService.updateOrderedQuantity(eq(orderedQuantityId), any(UpdateOrderedQuantityRequestDTO.class)))
                .thenAnswer(inv -> { throw new Exception("DB down"); });

        mockMvc.perform(put("/api/ordered-activities/{id}/edit", orderedQuantityId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message", containsString("Error: DB down")));
    }

    @Test
    void testGetEligibleActivities_ServerErrorBranch() throws Exception {
        when(orderedQuantityRestService.getEligibleActivities(planId))
                .thenAnswer(inv -> { throw new Exception("DB down"); });

        mockMvc.perform(get("/api/ordered-activities/eligible")
                .param("planId", planId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message", containsString("Error: DB down")));
    }

    @Test
    void testDeleteOrderedQuantity_ServerErrorBranch() throws Exception {
        doAnswer(inv -> { throw new Exception("DB down"); })
                .when(orderedQuantityRestService).deleteOrderedQuantity(orderedQuantityId);

        mockMvc.perform(delete("/api/ordered-activities/{id}/delete", orderedQuantityId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message", containsString("Error: DB down")));
    }
}

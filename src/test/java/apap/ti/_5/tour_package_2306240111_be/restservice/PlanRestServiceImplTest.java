package apap.ti._5.tour_package_2306240111_be.restservice;

import apap.ti._5.tour_package_2306240111_be.model.Package;
import apap.ti._5.tour_package_2306240111_be.model.Plan;
import apap.ti._5.tour_package_2306240111_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306240111_be.repository.OrderedQuantityRepository;
import apap.ti._5.tour_package_2306240111_be.repository.PackageRepository;
import apap.ti._5.tour_package_2306240111_be.repository.PlanRepository;
import apap.ti._5.tour_package_2306240111_be.restdto.request.plan.CreatePlanRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.request.plan.UpdatePlanRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanRestServiceImplTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private PackageRepository packageRepository;

    @Mock
    private OrderedQuantityRepository orderedQuantityRepository;

    @InjectMocks
    private PlanRestServiceImpl planRestService;

    private Package testPackage;
    private Plan testPlan;
    private String packageId;
    private UUID planId;

    @BeforeEach
    void setUp() {
        packageId = "PACK-USER001-001";
        planId = UUID.randomUUID();

        testPackage = Package.builder()
                .id(packageId)
                .userId("USER001")
                .packageName("Bali Tour Package")
                .quota(10)
                .price(0L)
                .status("Pending")
                .startDate(LocalDateTime.of(2025, 12, 1, 0, 0))
                .endDate(LocalDateTime.of(2025, 12, 31, 23, 59))
                .isDeleted(false)
                .build();

        testPlan = Plan.builder()
                .id(planId)
                .packageId(packageId)
                .planName("Day 1 Flight")
                .price(1000000L)
                .activityType("Flight")
                .status("Unfulfilled")
                .startDate(LocalDateTime.of(2025, 12, 5, 8, 0))
                .endDate(LocalDateTime.of(2025, 12, 5, 10, 0))
                .startLocation("Jakarta")
                .endLocation("Bali")
                .isDeleted(false)
                .build();
    }

    @Test
    void testCreatePlan_Success() {
        CreatePlanRequestDTO requestDTO = new CreatePlanRequestDTO();
        requestDTO.setPlanName("Flight to Bali");
        requestDTO.setActivityType("Flight");
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 5, 8, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 5, 10, 0));
        requestDTO.setStartLocation("Jakarta");
        requestDTO.setEndLocation("Bali");

        Plan savedPlan = Plan.builder()
                .id(UUID.randomUUID())
                .packageId(packageId)
                .planName("Flight to Bali")
                .activityType("Flight")
                .price(0L)
                .status("Unfulfilled")
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .startLocation("Jakarta")
                .endLocation("Bali")
                .build();

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(planRepository.save(any(Plan.class))).thenReturn(savedPlan);

        Plan result = planRestService.createPlan(packageId, requestDTO);

        assertNotNull(result);
        assertEquals("Flight to Bali", result.getPlanName());
        assertEquals("Flight", result.getActivityType());
        assertEquals(0L, result.getPrice());
        assertEquals("Unfulfilled", result.getStatus());
        verify(packageRepository, times(1)).findById(packageId);
        verify(planRepository, times(1)).save(any(Plan.class));
    }

    @Test
    void testCreatePlan_PackageNotFound() {
        CreatePlanRequestDTO requestDTO = new CreatePlanRequestDTO();
        requestDTO.setPlanName("Flight to Bali");
        requestDTO.setActivityType("Flight");
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 5, 8, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 5, 10, 0));
        requestDTO.setStartLocation("Jakarta");
        requestDTO.setEndLocation("Bali");

        when(packageRepository.findById(packageId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.createPlan(packageId, requestDTO));

        assertTrue(ex.getMessage().contains("Package not found"));
        verify(packageRepository, times(1)).findById(packageId);
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testCreatePlan_PackageNotPending() {
        testPackage.setStatus("Processed");

        CreatePlanRequestDTO requestDTO = new CreatePlanRequestDTO();
        requestDTO.setPlanName("Flight to Bali");
        requestDTO.setActivityType("Flight");
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 5, 8, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 5, 10, 0));
        requestDTO.setStartLocation("Jakarta");
        requestDTO.setEndLocation("Bali");

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.createPlan(packageId, requestDTO));

        assertTrue(ex.getMessage().contains("Package status must be 'Pending'"));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testCreatePlan_EndDateBeforeStartDate() {
        CreatePlanRequestDTO requestDTO = new CreatePlanRequestDTO();
        requestDTO.setPlanName("Invalid Plan");
        requestDTO.setActivityType("Flight");
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 10, 8, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 5, 10, 0));
        requestDTO.setStartLocation("Jakarta");
        requestDTO.setEndLocation("Bali");

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.createPlan(packageId, requestDTO));

        assertTrue(ex.getMessage().contains("end date must be after start date"));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testCreatePlan_EndDateEqualsStartDate() {
        CreatePlanRequestDTO requestDTO = new CreatePlanRequestDTO();
        requestDTO.setPlanName("Invalid Plan");
        requestDTO.setActivityType("Flight");
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 5, 8, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 5, 8, 0));
        requestDTO.setStartLocation("Jakarta");
        requestDTO.setEndLocation("Bali");

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.createPlan(packageId, requestDTO));

        assertTrue(ex.getMessage().contains("end date must be after start date"));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testCreatePlan_StartDateBeforePackageStartDate() {
        CreatePlanRequestDTO requestDTO = new CreatePlanRequestDTO();
        requestDTO.setPlanName("Early Plan");
        requestDTO.setActivityType("Flight");
        requestDTO.setStartDate(LocalDateTime.of(2025, 11, 30, 8, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 1, 10, 0));
        requestDTO.setStartLocation("Jakarta");
        requestDTO.setEndLocation("Bali");

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.createPlan(packageId, requestDTO));

        assertTrue(ex.getMessage().contains("start date cannot be before package start date"));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testCreatePlan_EndDateAfterPackageEndDate() {
        CreatePlanRequestDTO requestDTO = new CreatePlanRequestDTO();
        requestDTO.setPlanName("Late Plan");
        requestDTO.setActivityType("Flight");
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 30, 8, 0));
        requestDTO.setEndDate(LocalDateTime.of(2026, 1, 1, 10, 0));
        requestDTO.setStartLocation("Jakarta");
        requestDTO.setEndLocation("Bali");

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.createPlan(packageId, requestDTO));

        assertTrue(ex.getMessage().contains("cannot be after package end date"));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testCreatePlan_AccommodationWithDifferentLocations() {
        CreatePlanRequestDTO requestDTO = new CreatePlanRequestDTO();
        requestDTO.setPlanName("Hotel Stay");
        requestDTO.setActivityType("Accommodation");
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 5, 14, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 6, 12, 0));
        requestDTO.setStartLocation("Bali");
        requestDTO.setEndLocation("Jakarta");

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.createPlan(packageId, requestDTO));

        assertTrue(ex.getMessage().contains("start location and end location must be the same"));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testCreatePlan_AccommodationWithNullStartLocation() {
        CreatePlanRequestDTO requestDTO = new CreatePlanRequestDTO();
        requestDTO.setPlanName("Hotel Stay");
        requestDTO.setActivityType("Accommodation");
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 5, 14, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 6, 12, 0));
        requestDTO.setStartLocation(null);
        requestDTO.setEndLocation("Bali");

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.createPlan(packageId, requestDTO));

        assertTrue(ex.getMessage().contains("start location and end location must be the same"));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testCreatePlan_AccommodationWithNullEndLocation() {
        CreatePlanRequestDTO requestDTO = new CreatePlanRequestDTO();
        requestDTO.setPlanName("Hotel Stay");
        requestDTO.setActivityType("Accommodation");
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 5, 14, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 6, 12, 0));
        requestDTO.setStartLocation("Bali");
        requestDTO.setEndLocation(null);

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.createPlan(packageId, requestDTO));

        assertTrue(ex.getMessage().contains("start location and end location must be the same"));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testCreatePlan_AccommodationSuccess() {
        CreatePlanRequestDTO requestDTO = new CreatePlanRequestDTO();
        requestDTO.setPlanName("Hotel Stay");
        requestDTO.setActivityType("Accommodation");
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 5, 14, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 6, 12, 0));
        requestDTO.setStartLocation("Bali");
        requestDTO.setEndLocation("Bali");

        Plan savedPlan = Plan.builder()
                .id(UUID.randomUUID())
                .packageId(packageId)
                .planName("Hotel Stay")
                .activityType("Accommodation")
                .price(0L)
                .status("Unfulfilled")
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .startLocation("Bali")
                .endLocation("Bali")
                .build();

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(planRepository.save(any(Plan.class))).thenReturn(savedPlan);

        Plan result = planRestService.createPlan(packageId, requestDTO);

        assertNotNull(result);
        assertEquals("Hotel Stay", result.getPlanName());
        assertEquals("Accommodation", result.getActivityType());
        verify(packageRepository, times(1)).findById(packageId);
        verify(planRepository, times(1)).save(any(Plan.class));
    }

    @Test
    void testGetPlanById_Success() {
        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));

        Plan result = planRestService.getPlanById(planId);

        assertNotNull(result);
        assertEquals(planId, result.getId());
        assertEquals("Day 1 Flight", result.getPlanName());
        verify(planRepository, times(1)).findById(planId);
    }

    @Test
    void testGetPlanById_NotFound() {
        when(planRepository.findById(planId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.getPlanById(planId));

        assertTrue(ex.getMessage().contains("Plan not found"));
        verify(planRepository, times(1)).findById(planId);
    }

    @Test
    void testUpdatePlan_Success() {
        UpdatePlanRequestDTO requestDTO = new UpdatePlanRequestDTO();
        requestDTO.setPlanName("Updated Flight");
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 6, 9, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 6, 11, 0));
        requestDTO.setStartLocation("Jakarta");
        requestDTO.setEndLocation("Bali");

        Plan updatedPlan = Plan.builder()
                .id(planId)
                .packageId(packageId)
                .planName("Updated Flight")
                .activityType("Flight")
                .price(testPlan.getPrice())
                .status(testPlan.getStatus())
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .startLocation("Jakarta")
                .endLocation("Bali")
                .build();

        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(new ArrayList<>());
        when(planRepository.save(any(Plan.class))).thenReturn(updatedPlan);

        Plan result = planRestService.updatePlan(planId, requestDTO);

        assertNotNull(result);
        assertEquals("Updated Flight", result.getPlanName());
        verify(planRepository, times(1)).findById(planId);
        verify(packageRepository, times(1)).findById(packageId);
        verify(orderedQuantityRepository, times(1)).findByPlanId(planId);
        verify(planRepository, times(1)).save(any(Plan.class));
    }

    @Test
    void testUpdatePlan_PlanNotFound() {
        UpdatePlanRequestDTO requestDTO = new UpdatePlanRequestDTO();
        requestDTO.setPlanName("Updated Flight");
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 6, 9, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 6, 11, 0));
        requestDTO.setStartLocation("Jakarta");
        requestDTO.setEndLocation("Bali");

        when(planRepository.findById(planId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.updatePlan(planId, requestDTO));

        assertTrue(ex.getMessage().contains("Plan not found"));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testUpdatePlan_PackageNotPending() {
        testPackage.setStatus("Processed");

        UpdatePlanRequestDTO requestDTO = new UpdatePlanRequestDTO();
        requestDTO.setPlanName("Updated Flight");
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 6, 9, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 6, 11, 0));
        requestDTO.setStartLocation("Jakarta");
        requestDTO.setEndLocation("Bali");

        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.updatePlan(planId, requestDTO));

        assertTrue(ex.getMessage().contains("Package status must be 'Pending'"));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testUpdatePlan_WithOrderedActivities() {
        UpdatePlanRequestDTO requestDTO = new UpdatePlanRequestDTO();
        requestDTO.setPlanName("Updated Flight");
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 6, 9, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 6, 11, 0));
        requestDTO.setStartLocation("Jakarta");
        requestDTO.setEndLocation("Bali");

        List<OrderedQuantity> oqs = List.of(OrderedQuantity.builder().id(UUID.randomUUID()).build());

        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(oqs);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.updatePlan(planId, requestDTO));

        assertTrue(ex.getMessage().contains("Plan has ordered activities"));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testUpdatePlan_InvalidDates() {
        UpdatePlanRequestDTO requestDTO = new UpdatePlanRequestDTO();
        requestDTO.setPlanName("Updated Flight");
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 10, 9, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 5, 11, 0));
        requestDTO.setStartLocation("Jakarta");
        requestDTO.setEndLocation("Bali");

        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(new ArrayList<>());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.updatePlan(planId, requestDTO));

        assertTrue(ex.getMessage().contains("end date must be after start date"));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testUpdatePlan_StartDateBeforePackageStartDate() {
        UpdatePlanRequestDTO requestDTO = new UpdatePlanRequestDTO();
        requestDTO.setPlanName("Updated Flight");
        requestDTO.setStartDate(LocalDateTime.of(2025, 11, 30, 9, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 5, 11, 0));
        requestDTO.setStartLocation("Jakarta");
        requestDTO.setEndLocation("Bali");

        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(new ArrayList<>());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.updatePlan(planId, requestDTO));

        assertTrue(ex.getMessage().contains("start date cannot be before package start date"));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testUpdatePlan_EndDateAfterPackageEndDate() {
        UpdatePlanRequestDTO requestDTO = new UpdatePlanRequestDTO();
        requestDTO.setPlanName("Updated Flight");
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 30, 9, 0));
        requestDTO.setEndDate(LocalDateTime.of(2026, 1, 1, 11, 0));
        requestDTO.setStartLocation("Jakarta");
        requestDTO.setEndLocation("Bali");

        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(new ArrayList<>());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.updatePlan(planId, requestDTO));

        assertTrue(ex.getMessage().contains("cannot be after package end date"));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testUpdatePlan_AccommodationWithDifferentLocations() {
        testPlan.setActivityType("Accommodation");

        UpdatePlanRequestDTO requestDTO = new UpdatePlanRequestDTO();
        requestDTO.setPlanName("Updated Hotel");
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 5, 14, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 6, 12, 0));
        requestDTO.setStartLocation("Bali");
        requestDTO.setEndLocation("Jakarta");

        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(new ArrayList<>());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.updatePlan(planId, requestDTO));

        assertTrue(ex.getMessage().contains("start location and end location must be the same"));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testUpdatePlan_AccommodationWithNullLocations() {
        testPlan.setActivityType("Accommodation");

        UpdatePlanRequestDTO requestDTO = new UpdatePlanRequestDTO();
        requestDTO.setPlanName("Updated Hotel");
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 5, 14, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 6, 12, 0));
        requestDTO.setStartLocation(null);
        requestDTO.setEndLocation("Bali");

        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(new ArrayList<>());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.updatePlan(planId, requestDTO));

        assertTrue(ex.getMessage().contains("start location and end location must be the same"));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testDeletePlan_Success() {
        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(planRepository.save(any(Plan.class))).thenAnswer(invocation -> {
            Plan p = invocation.getArgument(0);
            assertTrue(p.getIsDeleted());
            return p;
        });

        planRestService.deletePlan(planId);

        verify(planRepository, times(1)).findById(planId);
        verify(packageRepository, times(1)).findById(packageId);
        verify(planRepository, times(1)).save(any(Plan.class));
    }

    @Test
    void testDeletePlan_PlanNotFound() {
        when(planRepository.findById(planId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.deletePlan(planId));

        assertTrue(ex.getMessage().contains("Plan not found"));
        verify(planRepository, times(1)).findById(planId);
        verify(packageRepository, never()).findById(anyString());
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testDeletePlan_PackageNotFound() {
        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(packageRepository.findById(packageId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.deletePlan(planId));

        assertTrue(ex.getMessage().contains("Package not found"));
        verify(packageRepository, times(1)).findById(packageId);
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testDeletePlan_PackageNotPending() {
        testPackage.setStatus("Processed");
        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> planRestService.deletePlan(planId));

        assertTrue(ex.getMessage().contains("Package status must be 'Pending'"));
        verify(planRepository, never()).save(any(Plan.class));
    }
}

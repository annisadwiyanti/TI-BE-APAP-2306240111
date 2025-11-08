package apap.ti._5.tour_package_2306240111_be.restservice;

import apap.ti._5.tour_package_2306240111_be.model.Activity;
import apap.ti._5.tour_package_2306240111_be.model.Plan;
import apap.ti._5.tour_package_2306240111_be.repository.ActivityRepository;
import apap.ti._5.tour_package_2306240111_be.repository.OrderedQuantityRepository;
import apap.ti._5.tour_package_2306240111_be.repository.PackageRepository;
import apap.ti._5.tour_package_2306240111_be.repository.PlanRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderedQuantityRestServiceImplTest {

    @Mock
    private OrderedQuantityRepository orderedQuantityRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private PackageRepository packageRepository;

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private OrderedQuantityRestServiceImpl orderedQuantityRestService;

    private Plan plan;
    private Activity activity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private UUID planId;

    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.of(2025, 11, 8, 10, 0);
        endDate = LocalDateTime.of(2025, 11, 9, 12, 0);
        planId = UUID.randomUUID();

        plan = Plan.builder()
                .id(planId)
                .packageId("PACK-USER123-001")
                .planName("Flight Plan")
                .price(1500000L)
                .activityType("Flight")
                .status("Unfulfilled")
                .startDate(startDate)
                .endDate(endDate)
                .startLocation("Jakarta")
                .endLocation("Bali")
                .isDeleted(false)
                .build();

        activity = Activity.builder()
                .id("ACT-FL-00001")
                .activityName("Jakarta to Bali Flight")
                .activityItem("Garuda Indonesia Economy")
                .capacity(100)
                .price(1500000L)
                .activityType("Flight")
                .startDate(startDate)
                .endDate(endDate)
                .startLocation("Jakarta")
                .endLocation("Bali")
                .build();
    }

    @Test
    void testGetAllActivitiesSuccess() {
        List<Activity> activities = new ArrayList<>();
        activities.add(activity);
        
        when(activityRepository.findAll()).thenReturn(activities);

        List<Activity> result = orderedQuantityRestService.getAllActivities();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(activityRepository, times(1)).findAll();
    }

    @Test
    void testGetAllActivitiesEmpty() {
        when(activityRepository.findAll()).thenReturn(new ArrayList<>());

        List<Activity> result = orderedQuantityRestService.getAllActivities();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetEligibleActivitiesSuccess() {
        List<Activity> allActivities = new ArrayList<>();
        allActivities.add(activity);

        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(activityRepository.findAll()).thenReturn(allActivities);

        List<Activity> result = orderedQuantityRestService.getEligibleActivities(planId);

        assertNotNull(result);
        verify(planRepository, times(1)).findById(planId);
        verify(activityRepository, times(1)).findAll();
    }

    @Test
    void testGetEligibleActivitiesPlanNotFound() {
        when(planRepository.findById(planId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            orderedQuantityRestService.getEligibleActivities(planId);
        });
    }

    @Test
    void testGetEligibleActivitiesNoMatch() {
        List<Activity> activities = new ArrayList<>();
        Activity differentTypeActivity = activity.toBuilder()
                .activityType("Accommodation")
                .build();
        activities.add(differentTypeActivity);

        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(activityRepository.findAll()).thenReturn(activities);

        List<Activity> result = orderedQuantityRestService.getEligibleActivities(planId);

        assertNotNull(result);
        verify(planRepository, times(1)).findById(planId);
    }

    @Test
    void testGetEligibleActivitiesMultiple() {
        List<Activity> activities = new ArrayList<>();
        activities.add(activity);
        Activity activity2 = activity.toBuilder()
                .id("ACT-FL-00002")
                .build();
        activities.add(activity2);

        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(activityRepository.findAll()).thenReturn(activities);

        List<Activity> result = orderedQuantityRestService.getEligibleActivities(planId);

        assertNotNull(result);
        verify(planRepository, times(1)).findById(planId);
    }

    @Test
    void testLocationNormalization() {
        // Test that the service can handle location normalization
        Plan testPlan = plan.toBuilder()
                .startLocation("DKI Jakarta")
                .endLocation("Bali")
                .build();

        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(activityRepository.findAll()).thenReturn(new ArrayList<>());

        List<Activity> result = orderedQuantityRestService.getEligibleActivities(planId);

        assertNotNull(result);
    }

    @Test
    void testGetEligibleActivitiesAccommodationType() {
        Plan accommodationPlan = plan.toBuilder()
                .activityType("Accommodation")
                .startLocation("Bali")
                .endLocation("Bali")
                .build();

        Activity accommodationActivity = activity.toBuilder()
                .activityType("Accommodation")
                .startLocation("Bali")
                .endLocation("Bali")
                .id("ACT-ACC-00001")
                .build();

        List<Activity> activities = new ArrayList<>();
        activities.add(accommodationActivity);

        when(planRepository.findById(planId)).thenReturn(Optional.of(accommodationPlan));
        when(activityRepository.findAll()).thenReturn(activities);

        List<Activity> result = orderedQuantityRestService.getEligibleActivities(planId);

        assertNotNull(result);
    }

    @Test
    void testGetEligibleActivitiesVehicleType() {
        Plan vehiclePlan = plan.toBuilder()
                .activityType("Vehicle Rental")
                .startLocation("Jakarta")
                .endLocation("Jakarta")
                .build();

        Activity vehicleActivity = activity.toBuilder()
                .activityType("Vehicle Rental")
                .startLocation("Jakarta")
                .endLocation("Jakarta")
                .id("ACT-VH-00001")
                .build();

        List<Activity> activities = new ArrayList<>();
        activities.add(vehicleActivity);

        when(planRepository.findById(planId)).thenReturn(Optional.of(vehiclePlan));
        when(activityRepository.findAll()).thenReturn(activities);

        List<Activity> result = orderedQuantityRestService.getEligibleActivities(planId);

        assertNotNull(result);
    }

    @Test
    void testGetEligibleActivitiesDifferentMonths() {
        LocalDateTime decemberDate = LocalDateTime.of(2025, 12, 8, 10, 0);
        Plan decemberPlan = plan.toBuilder()
                .startDate(decemberDate)
                .endDate(decemberDate.plusDays(1))
                .build();

        when(planRepository.findById(planId)).thenReturn(Optional.of(decemberPlan));
        when(activityRepository.findAll()).thenReturn(new ArrayList<>());

        List<Activity> result = orderedQuantityRestService.getEligibleActivities(planId);

        assertNotNull(result);
    }
}

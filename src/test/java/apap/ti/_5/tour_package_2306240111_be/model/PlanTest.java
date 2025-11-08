package apap.ti._5.tour_package_2306240111_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlanTest {

    private Plan plan;
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
    }

    @Test
    void testPlanBuilder() {
        assertNotNull(plan);
        assertEquals(planId, plan.getId());
        assertEquals("PACK-USER123-001", plan.getPackageId());
        assertEquals("Flight Plan", plan.getPlanName());
        assertEquals(1500000L, plan.getPrice());
        assertEquals("Flight", plan.getActivityType());
        assertEquals("Unfulfilled", plan.getStatus());
        assertEquals(startDate, plan.getStartDate());
        assertEquals(endDate, plan.getEndDate());
        assertEquals("Jakarta", plan.getStartLocation());
        assertEquals("Bali", plan.getEndLocation());
        assertFalse(plan.getIsDeleted());
    }

    @Test
    void testPlanSetters() {
        plan.setPlanName("Updated Plan");
        plan.setPrice(2000000L);
        plan.setStatus("Fulfilled");
        plan.setIsDeleted(true);

        assertEquals("Updated Plan", plan.getPlanName());
        assertEquals(2000000L, plan.getPrice());
        assertEquals("Fulfilled", plan.getStatus());
        assertTrue(plan.getIsDeleted());
    }

    @Test
    void testPlanNoArgsConstructor() {
        Plan emptyPlan = new Plan();
        assertNull(emptyPlan.getId());
        assertNull(emptyPlan.getPlanName());
    }

    @Test
    void testPlanAllArgsConstructor() {
        UUID testId = UUID.randomUUID();
        Plan fullPlan = new Plan(
                testId,
                "PACK-USER456-002",
                "Accommodation Plan",
                3000000L,
                "Accommodation",
                "Fulfilled",
                startDate,
                endDate,
                "Bali",
                "Bali",
                false,
                null
        );

        assertEquals(testId, fullPlan.getId());
        assertEquals("PACK-USER456-002", fullPlan.getPackageId());
        assertEquals("Accommodation Plan", fullPlan.getPlanName());
    }

    @Test
    void testPlanToBuilder() {
        Plan modifiedPlan = plan.toBuilder()
                .status("Fulfilled")
                .price(2500000L)
                .build();

        assertEquals("Fulfilled", modifiedPlan.getStatus());
        assertEquals(2500000L, modifiedPlan.getPrice());
        assertEquals("Flight Plan", modifiedPlan.getPlanName());
    }

    @Test
    void testPlanIsDeletedDefault() {
        Plan newPlan = Plan.builder()
                .id(UUID.randomUUID())
                .packageId("PACK-USER789-003")
                .planName("Test Plan")
                .price(1000000L)
                .activityType("Flight")
                .status("Unfulfilled")
                .startDate(startDate)
                .endDate(endDate)
                .build();

        assertFalse(newPlan.getIsDeleted());
    }

    @Test
    void testPlanEquality() {
        Plan plan2 = Plan.builder()
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

        assertEquals(plan, plan2);
    }

    @Test
    void testPlanHashCode() {
        Plan plan2 = Plan.builder()
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

        assertEquals(plan.hashCode(), plan2.hashCode());
    }

    @Test
    void testPlanToString() {
        String planString = plan.toString();
        assertNotNull(planString);
        assertTrue(planString.contains("Flight Plan"));
    }

    @Test
    void testPlanDifferentStatuses() {
        Plan unfulfilledPlan = plan.toBuilder().status("Unfulfilled").build();
        Plan fulfilledPlan = plan.toBuilder().status("Fulfilled").build();

        assertEquals("Unfulfilled", unfulfilledPlan.getStatus());
        assertEquals("Fulfilled", fulfilledPlan.getStatus());
    }

    @Test
    void testPlanDifferentActivityTypes() {
        Plan flightPlan = plan.toBuilder().activityType("Flight").build();
        Plan accommodationPlan = plan.toBuilder().activityType("Accommodation").build();
        Plan vehiclePlan = plan.toBuilder().activityType("Vehicle Rental").build();

        assertEquals("Flight", flightPlan.getActivityType());
        assertEquals("Accommodation", accommodationPlan.getActivityType());
        assertEquals("Vehicle Rental", vehiclePlan.getActivityType());
    }

    @Test
    void testPlanWithPackageEntity() {
        Package packageEntity = Package.builder()
                .id("PACK-USER123-001")
                .userId("USER123")
                .packageName("Test Package")
                .quota(10)
                .price(50000000L)
                .status("Pending")
                .startDate(startDate)
                .endDate(endDate)
                .isDeleted(false)
                .build();

        Plan planWithPackage = plan.toBuilder()
                .packageEntity(packageEntity)
                .build();

        assertNotNull(planWithPackage.getPackageEntity());
        assertEquals("Test Package", planWithPackage.getPackageEntity().getPackageName());
    }

    @Test
    void testPlanWithNullLocations() {
        Plan planWithNullLocations = plan.toBuilder()
                .startLocation(null)
                .endLocation(null)
                .build();

        assertNull(planWithNullLocations.getStartLocation());
        assertNull(planWithNullLocations.getEndLocation());
    }
}

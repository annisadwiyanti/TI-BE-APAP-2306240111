package apap.ti._5.tour_package_2306240111_be.repository;

import apap.ti._5.tour_package_2306240111_be.model.Plan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PlanRepositoryTest {

    @Autowired
    private PlanRepository planRepository;

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
    void testSavePlan() {
        Plan saved = planRepository.save(plan);

        assertNotNull(saved);
        assertEquals("Flight Plan", saved.getPlanName());
    }

    @Test
    void testFindPlanById() {
        planRepository.save(plan);
        Optional<Plan> found = planRepository.findById(planId);

        assertTrue(found.isPresent());
        assertEquals("Flight Plan", found.get().getPlanName());
    }

    @Test
    void testFindPlanByIdNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<Plan> found = planRepository.findById(nonExistentId);

        assertFalse(found.isPresent());
    }

    @Test
    void testUpdatePlan() {
        planRepository.save(plan);
        plan.setStatus("Fulfilled");
        plan.setPlanName("Updated Plan");
        Plan updated = planRepository.save(plan);

        assertEquals("Fulfilled", updated.getStatus());
        assertEquals("Updated Plan", updated.getPlanName());
    }

    @Test
    void testDeletePlan() {
        planRepository.save(plan);
        planRepository.delete(plan);

        Optional<Plan> found = planRepository.findById(planId);
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByPackageId() {
        Plan plan1 = plan.toBuilder().id(UUID.randomUUID()).build();
        Plan plan2 = plan.toBuilder()
                .id(UUID.randomUUID())
                .planName("Accommodation Plan")
                .activityType("Accommodation")
                .build();

        planRepository.save(plan1);
        planRepository.save(plan2);

        List<Plan> found = planRepository.findByPackageId("PACK-USER123-001");

        assertEquals(2, found.size());
    }

    @Test
    void testFindByPackageIdEmpty() {
        List<Plan> found = planRepository.findByPackageId("NON-EXISTENT-PACKAGE");

        assertTrue(found.isEmpty());
    }

    @Test
    void testFindByPackageIdFiltersDeleted() {
        Plan activePlan = plan.toBuilder().id(UUID.randomUUID()).isDeleted(false).build();
        Plan deletedPlan = plan.toBuilder()
                .id(UUID.randomUUID())
                .isDeleted(true)
                .planName("Deleted Plan")
                .build();

        planRepository.save(activePlan);
        planRepository.save(deletedPlan);

        List<Plan> found = planRepository.findByPackageId("PACK-USER123-001");

        // Only non-deleted plans should be returned
        assertEquals(1, found.size());
    }

    @Test
    void testMultiplePlansPerPackage() {
        for (int i = 0; i < 5; i++) {
            planRepository.save(plan.toBuilder()
                    .id(UUID.randomUUID())
                    .planName("Plan " + i)
                    .build());
        }

        List<Plan> found = planRepository.findByPackageId("PACK-USER123-001");
        assertEquals(5, found.size());
    }

    @Test
    void testPlanWithDifferentStatuses() {
        Plan unfulfilledPlan = plan.toBuilder()
                .id(UUID.randomUUID())
                .status("Unfulfilled")
                .build();
        Plan fulfilledPlan = plan.toBuilder()
                .id(UUID.randomUUID())
                .status("Fulfilled")
                .build();

        planRepository.save(unfulfilledPlan);
        planRepository.save(fulfilledPlan);

        List<Plan> found = planRepository.findByPackageId("PACK-USER123-001");
        assertEquals(2, found.size());
    }

    @Test
    void testPlanWithDifferentActivityTypes() {
        Plan flightPlan = plan.toBuilder()
                .id(UUID.randomUUID())
                .activityType("Flight")
                .build();
        Plan accommodationPlan = plan.toBuilder()
                .id(UUID.randomUUID())
                .activityType("Accommodation")
                .build();
        Plan vehiclePlan = plan.toBuilder()
                .id(UUID.randomUUID())
                .activityType("Vehicle Rental")
                .build();

        planRepository.save(flightPlan);
        planRepository.save(accommodationPlan);
        planRepository.save(vehiclePlan);

        List<Plan> found = planRepository.findByPackageId("PACK-USER123-001");
        assertEquals(3, found.size());
    }
}

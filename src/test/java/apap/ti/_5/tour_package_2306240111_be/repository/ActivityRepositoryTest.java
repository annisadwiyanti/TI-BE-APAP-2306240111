package apap.ti._5.tour_package_2306240111_be.repository;

import apap.ti._5.tour_package_2306240111_be.model.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ActivityRepositoryTest {

    @Autowired
    private ActivityRepository activityRepository;

    private Activity activity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.of(2025, 11, 8, 10, 0);
        endDate = LocalDateTime.of(2025, 11, 9, 12, 0);
        
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
    void testSaveActivity() {
        Activity saved = activityRepository.save(activity);

        assertNotNull(saved);
        assertEquals("ACT-FL-00001", saved.getId());
        assertEquals("Jakarta to Bali Flight", saved.getActivityName());
    }

    @Test
    void testFindActivityById() {
        activityRepository.save(activity);
        Optional<Activity> found = activityRepository.findById("ACT-FL-00001");

        assertTrue(found.isPresent());
        assertEquals("Jakarta to Bali Flight", found.get().getActivityName());
    }

    @Test
    void testFindActivityByIdNotFound() {
        Optional<Activity> found = activityRepository.findById("NON-EXISTENT");

        assertFalse(found.isPresent());
    }

    @Test
    void testUpdateActivity() {
        activityRepository.save(activity);
        activity.setActivityName("Updated Activity Name");
        Activity updated = activityRepository.save(activity);

        assertEquals("Updated Activity Name", updated.getActivityName());
    }

    @Test
    void testDeleteActivity() {
        activityRepository.save(activity);
        activityRepository.delete(activity);

        Optional<Activity> found = activityRepository.findById("ACT-FL-00001");
        assertFalse(found.isPresent());
    }

    @Test
    void testSaveMultipleActivities() {
        Activity activity1 = activity.toBuilder().id("ACT-FL-00001").build();
        Activity activity2 = activity.toBuilder().id("ACT-ACC-00001").activityType("Accommodation").build();

        activityRepository.save(activity1);
        activityRepository.save(activity2);

        assertEquals(2, activityRepository.count());
    }

    @Test
    void testActivityWithDifferentTypes() {
        Activity flight = activity.toBuilder().id("ACT-FL-00002").activityType("Flight").build();
        Activity accommodation = activity.toBuilder().id("ACT-ACC-00002").activityType("Accommodation").build();
        Activity vehicle = activity.toBuilder().id("ACT-VH-00001").activityType("Vehicle Rental").build();

        activityRepository.save(flight);
        activityRepository.save(accommodation);
        activityRepository.save(vehicle);

        assertEquals(3, activityRepository.count());
    }

    @Test
    void testActivityWithVariousPrices() {
        Activity cheapActivity = activity.toBuilder().id("ACT-001").price(100000L).build();
        Activity expensiveActivity = activity.toBuilder().id("ACT-002").price(5000000L).build();

        activityRepository.save(cheapActivity);
        activityRepository.save(expensiveActivity);

        assertEquals(2, activityRepository.count());
    }
}

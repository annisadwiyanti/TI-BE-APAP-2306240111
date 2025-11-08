package apap.ti._5.tour_package_2306240111_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ActivityTest {

    private Activity activity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.of(2025, 11, 8, 10, 0);
        endDate = LocalDateTime.of(2025, 11, 9, 12, 0);
        
        activity = Activity.builder()
                .id("ACT-001")
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
    void testActivityBuilder() {
        assertNotNull(activity);
        assertEquals("ACT-001", activity.getId());
        assertEquals("Jakarta to Bali Flight", activity.getActivityName());
        assertEquals("Garuda Indonesia Economy", activity.getActivityItem());
        assertEquals(100, activity.getCapacity());
        assertEquals(1500000L, activity.getPrice());
        assertEquals("Flight", activity.getActivityType());
        assertEquals(startDate, activity.getStartDate());
        assertEquals(endDate, activity.getEndDate());
        assertEquals("Jakarta", activity.getStartLocation());
        assertEquals("Bali", activity.getEndLocation());
    }

    @Test
    void testActivitySetters() {
        activity.setId("ACT-002");
        activity.setActivityName("Updated Activity");
        activity.setActivityItem("Updated Item");
        activity.setCapacity(50);
        activity.setPrice(2000000L);
        activity.setActivityType("Accommodation");

        assertEquals("ACT-002", activity.getId());
        assertEquals("Updated Activity", activity.getActivityName());
        assertEquals("Updated Item", activity.getActivityItem());
        assertEquals(50, activity.getCapacity());
        assertEquals(2000000L, activity.getPrice());
        assertEquals("Accommodation", activity.getActivityType());
    }

    @Test
    void testActivityNoArgsConstructor() {
        Activity emptyActivity = new Activity();
        assertNull(emptyActivity.getId());
        assertNull(emptyActivity.getActivityName());
    }

    @Test
    void testActivityAllArgsConstructor() {
        Activity fullActivity = new Activity(
                "ACT-003", 
                "Test Activity", 
                "Test Item", 
                25, 
                3000000L, 
                "Flight", 
                startDate, 
                endDate, 
                "Location A", 
                "Location B"
        );

        assertEquals("ACT-003", fullActivity.getId());
        assertEquals("Test Activity", fullActivity.getActivityName());
        assertEquals(25, fullActivity.getCapacity());
    }

    @Test
    void testActivityToBuilder() {
        Activity modifiedActivity = activity.toBuilder()
                .id("ACT-004")
                .capacity(150)
                .build();

        assertEquals("ACT-004", modifiedActivity.getId());
        assertEquals(150, modifiedActivity.getCapacity());
        assertEquals("Jakarta to Bali Flight", modifiedActivity.getActivityName());
    }

    @Test
    void testActivityEquality() {
        Activity activity2 = Activity.builder()
                .id("ACT-001")
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

        assertEquals(activity, activity2);
    }

    @Test
    void testActivityHashCode() {
        Activity activity2 = Activity.builder()
                .id("ACT-001")
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

        assertEquals(activity.hashCode(), activity2.hashCode());
    }

    @Test
    void testActivityToString() {
        String activityString = activity.toString();
        assertNotNull(activityString);
        assertTrue(activityString.contains("ACT-001"));
        assertTrue(activityString.contains("Jakarta to Bali Flight"));
    }

    @Test
    void testActivityWithNullValues() {
        Activity activityWithNulls = Activity.builder()
                .id("ACT-005")
                .activityName("Test")
                .capacity(10)
                .price(100000L)
                .activityType("Flight")
                .startDate(startDate)
                .endDate(endDate)
                .startLocation(null)
                .endLocation(null)
                .activityItem(null)
                .build();

        assertNull(activityWithNulls.getStartLocation());
        assertNull(activityWithNulls.getEndLocation());
        assertNull(activityWithNulls.getActivityItem());
    }

    @Test
    void testActivityDifferentTypes() {
        Activity flightActivity = activity.toBuilder().activityType("Flight").build();
        Activity accommodationActivity = activity.toBuilder().activityType("Accommodation").build();
        Activity vehicleActivity = activity.toBuilder().activityType("Vehicle Rental").build();

        assertEquals("Flight", flightActivity.getActivityType());
        assertEquals("Accommodation", accommodationActivity.getActivityType());
        assertEquals("Vehicle Rental", vehicleActivity.getActivityType());
    }
}

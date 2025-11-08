package apap.ti._5.tour_package_2306240111_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderedQuantityTest {

    private OrderedQuantity orderedQuantity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private UUID id;
    private UUID planId;

    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.of(2025, 11, 8, 10, 0);
        endDate = LocalDateTime.of(2025, 11, 9, 12, 0);
        id = UUID.randomUUID();
        planId = UUID.randomUUID();
        
        orderedQuantity = OrderedQuantity.builder()
                .id(id)
                .planId(planId)
                .activityId("ACT-FL-00001")
                .orderedQuota(5)
                .quota(10)
                .price(1500000L)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    @Test
    void testOrderedQuantityBuilder() {
        assertNotNull(orderedQuantity);
        assertEquals(id, orderedQuantity.getId());
        assertEquals(planId, orderedQuantity.getPlanId());
        assertEquals("ACT-FL-00001", orderedQuantity.getActivityId());
        assertEquals(5, orderedQuantity.getOrderedQuota());
        assertEquals(10, orderedQuantity.getQuota());
        assertEquals(1500000L, orderedQuantity.getPrice());
        assertEquals(startDate, orderedQuantity.getStartDate());
        assertEquals(endDate, orderedQuantity.getEndDate());
    }

    @Test
    void testOrderedQuantitySetters() {
        orderedQuantity.setOrderedQuota(8);
        orderedQuantity.setQuota(20);
        orderedQuantity.setPrice(2000000L);

        assertEquals(8, orderedQuantity.getOrderedQuota());
        assertEquals(20, orderedQuantity.getQuota());
        assertEquals(2000000L, orderedQuantity.getPrice());
    }

    @Test
    void testOrderedQuantityNoArgsConstructor() {
        OrderedQuantity emptyOrderedQuantity = new OrderedQuantity();
        assertNull(emptyOrderedQuantity.getId());
        assertNull(emptyOrderedQuantity.getPlanId());
    }

    @Test
    void testOrderedQuantityAllArgsConstructor() {
        UUID testId = UUID.randomUUID();
        UUID testPlanId = UUID.randomUUID();
        OrderedQuantity fullOrderedQuantity = new OrderedQuantity(
                testId,
                testPlanId,
                "ACT-ACC-00001",
                3,
                15,
                3000000L,
                startDate,
                endDate,
                null,
                null
        );

        assertEquals(testId, fullOrderedQuantity.getId());
        assertEquals(testPlanId, fullOrderedQuantity.getPlanId());
        assertEquals("ACT-ACC-00001", fullOrderedQuantity.getActivityId());
        assertEquals(3, fullOrderedQuantity.getOrderedQuota());
    }

    @Test
    void testOrderedQuantityToBuilder() {
        OrderedQuantity modifiedOrderedQuantity = orderedQuantity.toBuilder()
                .orderedQuota(7)
                .price(1800000L)
                .build();

        assertEquals(7, modifiedOrderedQuantity.getOrderedQuota());
        assertEquals(1800000L, modifiedOrderedQuantity.getPrice());
        assertEquals("ACT-FL-00001", modifiedOrderedQuantity.getActivityId());
    }

    @Test
    void testOrderedQuantityEquality() {
        OrderedQuantity orderedQuantity2 = OrderedQuantity.builder()
                .id(id)
                .planId(planId)
                .activityId("ACT-FL-00001")
                .orderedQuota(5)
                .quota(10)
                .price(1500000L)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        assertEquals(orderedQuantity, orderedQuantity2);
    }

    @Test
    void testOrderedQuantityHashCode() {
        OrderedQuantity orderedQuantity2 = OrderedQuantity.builder()
                .id(id)
                .planId(planId)
                .activityId("ACT-FL-00001")
                .orderedQuota(5)
                .quota(10)
                .price(1500000L)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        assertEquals(orderedQuantity.hashCode(), orderedQuantity2.hashCode());
    }

    @Test
    void testOrderedQuantityToString() {
        String orderedQuantityString = orderedQuantity.toString();
        assertNotNull(orderedQuantityString);
        assertTrue(orderedQuantityString.contains("ACT-FL-00001"));
    }

    @Test
    void testOrderedQuantityWithDifferentQuotas() {
        OrderedQuantity smallOrder = orderedQuantity.toBuilder().orderedQuota(1).quota(5).build();
        OrderedQuantity largeOrder = orderedQuantity.toBuilder().orderedQuota(50).quota(100).build();

        assertEquals(1, smallOrder.getOrderedQuota());
        assertEquals(5, smallOrder.getQuota());
        assertEquals(50, largeOrder.getOrderedQuota());
        assertEquals(100, largeOrder.getQuota());
    }

    @Test
    void testOrderedQuantityWithDifferentActivityTypes() {
        OrderedQuantity flightOrder = orderedQuantity.toBuilder().activityId("ACT-FL-00002").build();
        OrderedQuantity accommodationOrder = orderedQuantity.toBuilder().activityId("ACT-ACC-00002").build();
        OrderedQuantity vehicleOrder = orderedQuantity.toBuilder().activityId("ACT-VH-00001").build();

        assertEquals("ACT-FL-00002", flightOrder.getActivityId());
        assertEquals("ACT-ACC-00002", accommodationOrder.getActivityId());
        assertEquals("ACT-VH-00001", vehicleOrder.getActivityId());
    }

    @Test
    void testOrderedQuantityWithPlanEntity() {
        Plan planEntity = Plan.builder()
                .id(planId)
                .packageId("PACK-USER123-001")
                .planName("Test Plan")
                .price(1500000L)
                .activityType("Flight")
                .status("Unfulfilled")
                .startDate(startDate)
                .endDate(endDate)
                .isDeleted(false)
                .build();

        OrderedQuantity orderedQuantityWithPlan = orderedQuantity.toBuilder()
                .plan(planEntity)
                .build();

        assertNotNull(orderedQuantityWithPlan.getPlan());
        assertEquals("Test Plan", orderedQuantityWithPlan.getPlan().getPlanName());
    }

    @Test
    void testOrderedQuantityWithActivityEntity() {
        Activity activityEntity = Activity.builder()
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

        OrderedQuantity orderedQuantityWithActivity = orderedQuantity.toBuilder()
                .activity(activityEntity)
                .build();

        assertNotNull(orderedQuantityWithActivity.getActivity());
        assertEquals("Jakarta to Bali Flight", orderedQuantityWithActivity.getActivity().getActivityName());
    }

    @Test
    void testOrderedQuantityWithVariousPrices() {
        OrderedQuantity cheapOrder = orderedQuantity.toBuilder().price(500000L).build();
        OrderedQuantity expensiveOrder = orderedQuantity.toBuilder().price(5000000L).build();

        assertEquals(500000L, cheapOrder.getPrice());
        assertEquals(5000000L, expensiveOrder.getPrice());
    }
}

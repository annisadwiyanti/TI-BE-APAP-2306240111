package apap.ti._5.tour_package_2306240111_be.repository;

import apap.ti._5.tour_package_2306240111_be.model.OrderedQuantity;
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
class OrderedQuantityRepositoryTest {

    @Autowired
    private OrderedQuantityRepository orderedQuantityRepository;

    private OrderedQuantity orderedQuantity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private UUID planId;

    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.of(2025, 11, 8, 10, 0);
        endDate = LocalDateTime.of(2025, 11, 9, 12, 0);
        planId = UUID.randomUUID();
        
        orderedQuantity = OrderedQuantity.builder()
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
    void testSaveOrderedQuantity() {
        OrderedQuantity saved = orderedQuantityRepository.save(orderedQuantity);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("ACT-FL-00001", saved.getActivityId());
    }

    @Test
    void testFindOrderedQuantityById() {
        OrderedQuantity saved = orderedQuantityRepository.save(orderedQuantity);
        Optional<OrderedQuantity> found = orderedQuantityRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("ACT-FL-00001", found.get().getActivityId());
    }

    @Test
    void testFindByPlanId() {
        orderedQuantityRepository.save(orderedQuantity);
        OrderedQuantity orderedQuantity2 = orderedQuantity.toBuilder()
                .id(null)
                .activityId("ACT-ACC-00001")
                .build();
        orderedQuantityRepository.save(orderedQuantity2);

        List<OrderedQuantity> found = orderedQuantityRepository.findByPlanId(planId);

        assertEquals(2, found.size());
    }

    @Test
    void testFindByPlanIdEmpty() {
        UUID nonExistentPlanId = UUID.randomUUID();
        List<OrderedQuantity> found = orderedQuantityRepository.findByPlanId(nonExistentPlanId);

        assertTrue(found.isEmpty());
    }

    @Test
    void testUpdateOrderedQuantity() {
        OrderedQuantity saved = orderedQuantityRepository.save(orderedQuantity);
        saved.setOrderedQuota(8);
        saved.setPrice(2000000L);
        OrderedQuantity updated = orderedQuantityRepository.save(saved);

        assertEquals(8, updated.getOrderedQuota());
        assertEquals(2000000L, updated.getPrice());
    }

    @Test
    void testDeleteOrderedQuantity() {
        OrderedQuantity saved = orderedQuantityRepository.save(orderedQuantity);
        orderedQuantityRepository.delete(saved);

        Optional<OrderedQuantity> found = orderedQuantityRepository.findById(saved.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByYearAndMonth() {
        LocalDateTime november2025 = LocalDateTime.of(2025, 11, 8, 10, 0);
        LocalDateTime december2025 = LocalDateTime.of(2025, 12, 8, 10, 0);
        LocalDateTime january2026 = LocalDateTime.of(2026, 1, 8, 10, 0);

        OrderedQuantity oq1 = orderedQuantity.toBuilder()
                .id(null)
                .startDate(november2025)
                .endDate(november2025.plusDays(1))
                .build();
        OrderedQuantity oq2 = orderedQuantity.toBuilder()
                .id(null)
                .planId(UUID.randomUUID())
                .startDate(december2025)
                .endDate(december2025.plusDays(1))
                .build();
        OrderedQuantity oq3 = orderedQuantity.toBuilder()
                .id(null)
                .planId(UUID.randomUUID())
                .startDate(january2026)
                .endDate(january2026.plusDays(1))
                .build();

        orderedQuantityRepository.save(oq1);
        orderedQuantityRepository.save(oq2);
        orderedQuantityRepository.save(oq3);

        List<OrderedQuantity> november = orderedQuantityRepository.findByYearAndMonth(2025, 11);
        List<OrderedQuantity> december = orderedQuantityRepository.findByYearAndMonth(2025, 12);
        List<OrderedQuantity> allYear = orderedQuantityRepository.findByYearAndMonth(2025, 0);

        assertEquals(1, november.size());
        assertEquals(1, december.size());
        assertEquals(2, allYear.size());
    }

    @Test
    void testMultipleOrderedQuantitiesPerPlan() {
        for (int i = 0; i < 5; i++) {
            orderedQuantityRepository.save(orderedQuantity.toBuilder()
                    .id(null)
                    .activityId("ACT-" + i)
                    .build());
        }

        List<OrderedQuantity> found = orderedQuantityRepository.findByPlanId(planId);
        assertEquals(5, found.size());
    }

    @Test
    void testOrderedQuantityWithDifferentActivities() {
        OrderedQuantity flight = orderedQuantity.toBuilder()
                .id(null)
                .activityId("ACT-FL-00001")
                .build();
        OrderedQuantity accommodation = orderedQuantity.toBuilder()
                .id(null)
                .planId(UUID.randomUUID())
                .activityId("ACT-ACC-00001")
                .build();

        orderedQuantityRepository.save(flight);
        orderedQuantityRepository.save(accommodation);

        assertEquals(2, orderedQuantityRepository.count());
    }
}

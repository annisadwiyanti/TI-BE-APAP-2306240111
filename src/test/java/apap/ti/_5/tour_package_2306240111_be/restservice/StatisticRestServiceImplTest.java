package apap.ti._5.tour_package_2306240111_be.restservice;

import apap.ti._5.tour_package_2306240111_be.model.Activity;
import apap.ti._5.tour_package_2306240111_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306240111_be.repository.ActivityRepository;
import apap.ti._5.tour_package_2306240111_be.repository.OrderedQuantityRepository;
import apap.ti._5.tour_package_2306240111_be.restdto.response.statistic.ActivityTypeRevenueDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.statistic.RevenueStatisticDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticRestServiceImplTest {

    @Mock
    private OrderedQuantityRepository orderedQuantityRepository;

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private StatisticRestServiceImpl statisticRestService;

    private Activity flight, hotel, tour;
    private OrderedQuantity oqF1, oqH1, oqT1, oqF2;

    @BeforeEach
    void init() {
        flight = Activity.builder()
                .id("ACT-FLIGHT-001").activityName("Flight Jakarta-Bali")
                .activityType("Flight").capacity(100).price(1_000_000L)
                .startDate(LocalDateTime.of(2025, 12, 1, 8, 0))
                .endDate(LocalDateTime.of(2025, 12, 1, 10, 0))
                .startLocation("Jakarta").endLocation("Bali").build();

        hotel = Activity.builder()
                .id("ACT-HOTEL-001").activityName("Hotel Bali")
                .activityType("Accommodation").capacity(50).price(500_000L)
                .startDate(LocalDateTime.of(2025, 12, 1, 14, 0))
                .endDate(LocalDateTime.of(2025, 12, 2, 12, 0))
                .startLocation("Bali").endLocation("Bali").build();

        tour = Activity.builder()
                .id("ACT-TOUR-001").activityName("Bali Tour")
                .activityType("Tour").capacity(30).price(300_000L)
                .startDate(LocalDateTime.of(2025, 12, 2, 9, 0))
                .endDate(LocalDateTime.of(2025, 12, 2, 17, 0))
                .startLocation("Bali").endLocation("Bali").build();

        oqF1 = OrderedQuantity.builder()
                .id(UUID.randomUUID()).planId(UUID.randomUUID())
                .activityId("ACT-FLIGHT-001").orderedQuota(10).quota(100)
                .price(1_000_000L)
                .startDate(LocalDateTime.of(2025, 12, 1, 8, 0))
                .endDate(LocalDateTime.of(2025, 12, 1, 10, 0)).build();

        oqH1 = OrderedQuantity.builder()
                .id(UUID.randomUUID()).planId(UUID.randomUUID())
                .activityId("ACT-HOTEL-001").orderedQuota(5).quota(50)
                .price(500_000L)
                .startDate(LocalDateTime.of(2025, 12, 1, 14, 0))
                .endDate(LocalDateTime.of(2025, 12, 2, 12, 0)).build();

        oqT1 = OrderedQuantity.builder()
                .id(UUID.randomUUID()).planId(UUID.randomUUID())
                .activityId("ACT-TOUR-001").orderedQuota(8).quota(30)
                .price(300_000L)
                .startDate(LocalDateTime.of(2025, 12, 2, 9, 0))
                .endDate(LocalDateTime.of(2025, 12, 2, 17, 0)).build();

        oqF2 = OrderedQuantity.builder()
                .id(UUID.randomUUID()).planId(UUID.randomUUID())
                .activityId("ACT-FLIGHT-001").orderedQuota(15).quota(100)
                .price(1_000_000L)
                .startDate(LocalDateTime.of(2025, 12, 1, 8, 0))
                .endDate(LocalDateTime.of(2025, 12, 1, 10, 0)).build();
    }

    @Test
    @DisplayName("Hitung revenue multi activity type & sorted desc")
    void getRevenueStatistics_success_multiTypes_sorted() {
        int year = 2025, month = 12;
        when(orderedQuantityRepository.findByYearAndMonth(year, month))
                .thenReturn(List.of(oqF1, oqH1, oqT1, oqF2));
        when(activityRepository.findById("ACT-FLIGHT-001")).thenReturn(Optional.of(flight));
        when(activityRepository.findById("ACT-HOTEL-001")).thenReturn(Optional.of(hotel));
        when(activityRepository.findById("ACT-TOUR-001")).thenReturn(Optional.of(tour));

        RevenueStatisticDTO dto = statisticRestService.getRevenueStatistics(year, month);

        assertEquals(year, dto.getYear());
        assertEquals(month, dto.getMonth());
        assertEquals(3, dto.getRevenueByActivityType().size());

        // Flight: (10+15) * 1_000_000 = 25_000_000
        Map<String, Long> map = toMap(dto.getRevenueByActivityType());
        assertEquals(25_000_000L, map.get("Flight"));
        assertEquals(2_500_000L, map.get("Accommodation"));
        assertEquals(2_400_000L, map.get("Tour"));

        // Sorted desc by revenue
        List<ActivityTypeRevenueDTO> list = dto.getRevenueByActivityType();
        assertTrue(list.get(0).getTotalRevenue() >= list.get(1).getTotalRevenue());
        assertTrue(list.get(1).getTotalRevenue() >= list.get(2).getTotalRevenue());

        // findById dipanggil sekali per OQ (total 4)
        verify(activityRepository, times(4)).findById(anyString());
        verify(orderedQuantityRepository, times(1)).findByYearAndMonth(year, month);
        verifyNoMoreInteractions(activityRepository);
    }

    @Test
    @DisplayName("Tidak ada data -> list kosong & tidak query activity")
    void getRevenueStatistics_noData() {
        int year = 2025, month = 1;
        when(orderedQuantityRepository.findByYearAndMonth(year, month))
                .thenReturn(Collections.emptyList());

        RevenueStatisticDTO dto = statisticRestService.getRevenueStatistics(year, month);

        assertNotNull(dto);
        assertEquals(0, dto.getRevenueByActivityType().size());
        verify(orderedQuantityRepository, times(1)).findByYearAndMonth(year, month);
        verifyNoInteractions(activityRepository);
    }

    @Test
    @DisplayName("Activity tidak ditemukan -> diskip tanpa error")
    void getRevenueStatistics_activityNotFound() {
        int year = 2025, month = 12;
        when(orderedQuantityRepository.findByYearAndMonth(year, month))
                .thenReturn(List.of(oqF1));
        when(activityRepository.findById("ACT-FLIGHT-001"))
                .thenReturn(Optional.empty());

        RevenueStatisticDTO dto = statisticRestService.getRevenueStatistics(year, month);

        assertEquals(0, dto.getRevenueByActivityType().size());
        verify(activityRepository, times(1)).findById("ACT-FLIGHT-001");
    }

    @Test
    @DisplayName("Semua OQ tipe sama -> revenue di-aggregate")
    void getRevenueStatistics_sameTypeAggregated() {
        int year = 2025, month = 12;
        when(orderedQuantityRepository.findByYearAndMonth(year, month))
                .thenReturn(List.of(oqF1, oqF2)); // dua-duanya flight
        when(activityRepository.findById("ACT-FLIGHT-001")).thenReturn(Optional.of(flight));

        RevenueStatisticDTO dto = statisticRestService.getRevenueStatistics(year, month);

        assertEquals(1, dto.getRevenueByActivityType().size());
        assertEquals("Flight", dto.getRevenueByActivityType().get(0).getActivityType());
        assertEquals(25_000_000L, dto.getRevenueByActivityType().get(0).getTotalRevenue());

        // dipanggil 2x karena ada 2 OQ
        verify(activityRepository, times(2)).findById("ACT-FLIGHT-001");
    }

    @Test
    @DisplayName("Ordered quota 0 -> revenue 0 tetap muncul")
    void getRevenueStatistics_zeroRevenue() {
        int year = 2025, month = 12;
        OrderedQuantity zero = OrderedQuantity.builder()
                .id(UUID.randomUUID()).planId(UUID.randomUUID())
                .activityId("ACT-FLIGHT-001")
                .orderedQuota(0).quota(100).price(1_000_000L)
                .startDate(LocalDateTime.of(2025, 12, 1, 8, 0))
                .endDate(LocalDateTime.of(2025, 12, 1, 10, 0)).build();

        when(orderedQuantityRepository.findByYearAndMonth(year, month))
                .thenReturn(List.of(zero));
        when(activityRepository.findById("ACT-FLIGHT-001")).thenReturn(Optional.of(flight));

        RevenueStatisticDTO dto = statisticRestService.getRevenueStatistics(year, month);

        assertEquals(1, dto.getRevenueByActivityType().size());
        assertEquals(0L, dto.getRevenueByActivityType().get(0).getTotalRevenue());
        verify(activityRepository, times(1)).findById("ACT-FLIGHT-001");
    }

    @Test
    @DisplayName("Revenue besar (cek overflow long)")
    void getRevenueStatistics_largeRevenue() {
        int year = 2025, month = 12;
        OrderedQuantity large = OrderedQuantity.builder()
                .id(UUID.randomUUID()).planId(UUID.randomUUID())
                .activityId("ACT-FLIGHT-001")
                .orderedQuota(100).quota(100).price(10_000_000L) // 100 * 10M = 1_000_000_000
                .startDate(LocalDateTime.of(2025, 12, 1, 8, 0))
                .endDate(LocalDateTime.of(2025, 12, 1, 10, 0)).build();

        when(orderedQuantityRepository.findByYearAndMonth(year, month))
                .thenReturn(List.of(large));
        when(activityRepository.findById("ACT-FLIGHT-001")).thenReturn(Optional.of(flight));

        RevenueStatisticDTO dto = statisticRestService.getRevenueStatistics(year, month);

        assertEquals(1, dto.getRevenueByActivityType().size());
        assertEquals(1_000_000_000L, dto.getRevenueByActivityType().get(0).getTotalRevenue());
        verify(activityRepository, times(1)).findById("ACT-FLIGHT-001");
    }

    // helper
    private static Map<String, Long> toMap(List<ActivityTypeRevenueDTO> list) {
        Map<String, Long> m = new HashMap<>();
        for (ActivityTypeRevenueDTO d : list) m.put(d.getActivityType(), d.getTotalRevenue());
        return m;
    }
}

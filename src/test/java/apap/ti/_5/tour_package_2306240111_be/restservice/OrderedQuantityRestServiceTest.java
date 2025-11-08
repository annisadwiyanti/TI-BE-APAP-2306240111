package apap.ti._5.tour_package_2306240111_be.restservice;

import apap.ti._5.tour_package_2306240111_be.model.Activity;
import apap.ti._5.tour_package_2306240111_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306240111_be.model.Package;
import apap.ti._5.tour_package_2306240111_be.model.Plan;
import apap.ti._5.tour_package_2306240111_be.repository.ActivityRepository;
import apap.ti._5.tour_package_2306240111_be.repository.OrderedQuantityRepository;
import apap.ti._5.tour_package_2306240111_be.repository.PackageRepository;
import apap.ti._5.tour_package_2306240111_be.repository.PlanRepository;
import apap.ti._5.tour_package_2306240111_be.restdto.request.orderedquantity.CreateOrderedQuantityRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.request.orderedquantity.UpdateOrderedQuantityRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderedQuantityRestServiceImplTest {

    @Mock private OrderedQuantityRepository orderedQuantityRepository;
    @Mock private PlanRepository planRepository;
    @Mock private PackageRepository packageRepository;
    @Mock private ActivityRepository activityRepository;

    @InjectMocks
    private OrderedQuantityRestServiceImpl service;

    private UUID planId, oqId;
    private String activityId;
    private String packageId;
    private Activity actGeneric, actFlight;
    private Plan planGeneric, planFlight;
    private Package pkgPending, pkgProcessed;
    private OrderedQuantity oqSaved;

    @BeforeEach
    void init() {
        planId = UUID.randomUUID();
        oqId = UUID.randomUUID();
        activityId = "ACT-001";
        packageId = "PACK-001";

        actGeneric = Activity.builder()
                .id(activityId)
                .activityName("Diving")
                .activityType("Water Sport")
                .startLocation("Bali")
                .endLocation("Bali")
                .price(500_000L)
                .capacity(20)
                .startDate(LocalDateTime.of(2025, 12, 16, 9, 0))
                .endDate(LocalDateTime.of(2025, 12, 16, 17, 0))
                .build();

        actFlight = Activity.builder()
                .id("ACT-FLIGHT")
                .activityName("CGK-DPS")
                .activityType("Flight")
                .startLocation("DKI Jakarta")
                .endLocation("Bali (Provinsi)")
                .price(1_000_000L)
                .capacity(200)
                .startDate(LocalDateTime.of(2025, 12, 3, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 3, 12, 0))
                .build();

        planGeneric = Plan.builder()
                .id(planId)
                .packageId(packageId)
                .planName("Day-1 WS")
                .activityType("Water Sport")
                .status("Unfulfilled")
                .price(0L)
                .startDate(LocalDateTime.of(2025, 12, 16, 9, 0))
                .endDate(LocalDateTime.of(2025, 12, 16, 17, 0))
                .startLocation("Bali")
                .endLocation("Bali")
                .build();

        planFlight = Plan.builder()
                .id(planId)
                .packageId(packageId)
                .planName("Plane")
                .activityType("Flight")
                .status("Unfulfilled")
                .price(0L)
                .startDate(LocalDateTime.of(2025, 12, 3, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 3, 12, 0))
                .startLocation("Jakarta")
                .endLocation("Bali")
                .build();

        pkgPending = Package.builder()
                .id(packageId)
                .userId("USER-1")
                .packageName("Bali Pkg")
                .quota(50)
                .price(0L)
                .status("Pending")
                .startDate(LocalDateTime.of(2025, 12, 1, 0, 0))
                .endDate(LocalDateTime.of(2025, 12, 31, 23, 59))
                .build();

        pkgProcessed = pkgPending.toBuilder().status("Processed").build();

        oqSaved = OrderedQuantity.builder()
                .id(oqId)
                .planId(planId)
                .activityId(activityId)
                .orderedQuota(10)
                .quota(20)
                .price(500_000L)
                .startDate(planGeneric.getStartDate())
                .endDate(planGeneric.getEndDate())
                .build();
    }

    // ---------- getAll & eligible ----------

    @Test
    void getAllActivities_ok() {
        when(activityRepository.findAll()).thenReturn(List.of(actGeneric, actFlight));
        assertThat(service.getAllActivities()).hasSize(2).contains(actGeneric, actFlight);
        verify(activityRepository).findAll();
    }

    @Test
    void getEligibleActivities_planNotFound() {
        assertThatThrownBy(() -> service.getEligibleActivities(planId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Plan not found");
        verify(planRepository).findById(planId);
        verifyNoInteractions(activityRepository);
    }

    @Test
    void getEligibleActivities_matchTypeLocationMonth() {
        when(planRepository.findById(planId)).thenReturn(Optional.of(planGeneric));
        when(activityRepository.findAll()).thenReturn(List.of(actGeneric));
        assertThat(service.getEligibleActivities(planId)).hasSize(1).contains(actGeneric);
    }

    @Test
    void getEligibleActivities_filterByTypeLocationMonth() {
        Activity wrongType = actGeneric.toBuilder().id("X1").activityType("Hotel").build();
        Activity wrongLoc  = actGeneric.toBuilder().id("X2").startLocation("Jakarta").build();
        Activity wrongMonth= actGeneric.toBuilder().id("X3")
                .startDate(LocalDateTime.of(2025,11,16,9,0))
                .endDate(LocalDateTime.of(2025,11,16,17,0)).build();

        when(planRepository.findById(planId)).thenReturn(Optional.of(planGeneric));
        when(activityRepository.findAll()).thenReturn(List.of(actGeneric, wrongType, wrongLoc, wrongMonth));

        List<Activity> r = service.getEligibleActivities(planId);
        assertThat(r).hasSize(1).containsExactly(actGeneric);
    }

    // ---------- create ----------

    @Test
    @DisplayName("Create success (non-Flight): lokasi match, bulan sama, kuota ok")
    void create_success_generic() {
        CreateOrderedQuantityRequestDTO req = CreateOrderedQuantityRequestDTO.builder()
                .planId(planId).activityId(activityId).orderedQuota(10).build();

        when(planRepository.findById(planId)).thenReturn(Optional.of(planGeneric));
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(actGeneric));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(pkgPending));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(Collections.emptyList());
        when(orderedQuantityRepository.save(any(OrderedQuantity.class))).thenReturn(oqSaved);
        when(planRepository.save(any(Plan.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderedQuantity out = service.createOrderedQuantity(req);

        assertThat(out.getId()).isEqualTo(oqId);
        verify(orderedQuantityRepository).save(any(OrderedQuantity.class));
        verify(planRepository).save(any(Plan.class));
    }

    @Test
    @DisplayName("Create gagal: package bukan Pending")
    void create_fail_pkgNotPending() {
        CreateOrderedQuantityRequestDTO req = CreateOrderedQuantityRequestDTO.builder()
                .planId(planId).activityId(activityId).orderedQuota(5).build();

        when(planRepository.findById(planId)).thenReturn(Optional.of(planGeneric));
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(actGeneric));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(pkgProcessed));

        assertThatThrownBy(() -> service.createOrderedQuantity(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Package status must be 'Pending'");
        verify(orderedQuantityRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create gagal: tipe activity != tipe plan")
    void create_fail_typeMismatch() {
        CreateOrderedQuantityRequestDTO req = CreateOrderedQuantityRequestDTO.builder()
                .planId(planId).activityId(activityId).orderedQuota(5).build();

        Activity hotel = actGeneric.toBuilder().activityType("Hotel").build();

        when(planRepository.findById(planId)).thenReturn(Optional.of(planGeneric));
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(hotel));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(pkgPending));

        assertThatThrownBy(() -> service.createOrderedQuantity(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Activity type must match Plan activity type");
    }

    @Test
    @DisplayName("Create gagal: orderedQuota > capacity")
    void create_fail_capacity() {
        CreateOrderedQuantityRequestDTO req = CreateOrderedQuantityRequestDTO.builder()
                .planId(planId).activityId(activityId).orderedQuota(99).build();

        when(planRepository.findById(planId)).thenReturn(Optional.of(planGeneric));
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(actGeneric));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(pkgPending));

        assertThatThrownBy(() -> service.createOrderedQuantity(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ordered quantity cannot exceed activity capacity");
    }

    @Test
    @DisplayName("Create gagal: total OQ plan > kuota package")
    void create_fail_planQuotaExceed() {
        CreateOrderedQuantityRequestDTO req = CreateOrderedQuantityRequestDTO.builder()
                .planId(planId).activityId(activityId).orderedQuota(40).build();

        OrderedQuantity exist = OrderedQuantity.builder()
                .id(UUID.randomUUID()).planId(planId).activityId(activityId)
                .orderedQuota(20).quota(20).price(500_000L)
                .startDate(planGeneric.getStartDate()).endDate(planGeneric.getEndDate()).build();

        when(planRepository.findById(planId)).thenReturn(Optional.of(planGeneric));
        Activity highCap = actGeneric.toBuilder().capacity(100).build();
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(highCap));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(pkgPending));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(List.of(exist));

        assertThatThrownBy(() -> service.createOrderedQuantity(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Total ordered quantity for this plan cannot exceed package quota");

        verify(orderedQuantityRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create success: normalisasi lokasi (DKI/Provinsi)")
    void create_success_locationNormalization() {
        CreateOrderedQuantityRequestDTO req = CreateOrderedQuantityRequestDTO.builder()
                .planId(planId).activityId(activityId).orderedQuota(10).build();

        Activity a = actGeneric.toBuilder()
                .startLocation("Bali (Provinsi)").endLocation("Bali").build();
        Plan p = planGeneric.toBuilder().startLocation("DKI Bali").endLocation("Bali").build();

        when(planRepository.findById(planId)).thenReturn(Optional.of(p));
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(a));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(pkgPending));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(Collections.emptyList());
        when(orderedQuantityRepository.save(any())).thenReturn(oqSaved);
        when(planRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrderedQuantity out = service.createOrderedQuantity(req);
        assertThat(out).isNotNull();
    }

    @Test
    @DisplayName("Create Flight success: waktu HARUS sama persis")
    void create_success_flightExactTime() {
        CreateOrderedQuantityRequestDTO req = CreateOrderedQuantityRequestDTO.builder()
                .planId(planId).activityId("ACT-FLIGHT").orderedQuota(3).build();

        when(planRepository.findById(planId)).thenReturn(Optional.of(planFlight));
        when(activityRepository.findById("ACT-FLIGHT")).thenReturn(Optional.of(actFlight));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(pkgPending));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(Collections.emptyList());
        when(orderedQuantityRepository.save(any())).thenReturn(
                OrderedQuantity.builder().id(oqId).planId(planId).activityId("ACT-FLIGHT")
                        .orderedQuota(3).quota(200).price(1_000_000L)
                        .startDate(planFlight.getStartDate()).endDate(planFlight.getEndDate()).build()
        );
        when(planRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrderedQuantity out = service.createOrderedQuantity(req);
        assertThat(out.getOrderedQuota()).isEqualTo(3);
    }

    @Test
    @DisplayName("Create Flight gagal: waktu tidak sama persis")
    void create_fail_flightTimeMismatch() {
        CreateOrderedQuantityRequestDTO req = CreateOrderedQuantityRequestDTO.builder()
                .planId(planId).activityId("ACT-FLIGHT").orderedQuota(1).build();

        Activity diffTime = actFlight.toBuilder()
                .startDate(actFlight.getStartDate().plusMinutes(5))
                .build();

        when(planRepository.findById(planId)).thenReturn(Optional.of(planFlight));
        when(activityRepository.findById("ACT-FLIGHT")).thenReturn(Optional.of(diffTime));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(pkgPending));

        assertThatThrownBy(() -> service.createOrderedQuantity(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Flight dates and times must match Plan exactly");
    }

    // ---------- getById ----------

    @Test
    void getById_ok() {
        when(orderedQuantityRepository.findById(oqId)).thenReturn(Optional.of(oqSaved));
        assertThat(service.getOrderedQuantityById(oqId)).isSameAs(oqSaved);
    }

    @Test
    void getById_notFound() {
        assertThatThrownBy(() -> service.getOrderedQuantityById(oqId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ordered Quantity not found");
    }

    @Test
    @DisplayName("Update success & hitung ulang price + status Unfulfilled/Fulfilled")
    void update_success_and_planStatus() {
        UpdateOrderedQuantityRequestDTO req = UpdateOrderedQuantityRequestDTO.builder()
                .orderedQuota(25).build();

        OrderedQuantity other = oqSaved.toBuilder().id(UUID.randomUUID()).orderedQuota(10).build();

        when(orderedQuantityRepository.findById(oqId)).thenReturn(Optional.of(oqSaved));
        when(planRepository.findById(planId)).thenReturn(Optional.of(planGeneric));
       
        Activity highCap = actGeneric.toBuilder().capacity(100).build();
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(highCap));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(pkgPending));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(List.of(other, oqSaved));
        when(orderedQuantityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(planRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrderedQuantity out = service.updateOrderedQuantity(oqId, req);
        assertThat(out.getOrderedQuota()).isEqualTo(25);

        verify(planRepository).save(argThat(p -> p.getPrice() == 17_500_000L));
    }


    @Test
    void update_fail_notFound() {
        UpdateOrderedQuantityRequestDTO req = UpdateOrderedQuantityRequestDTO.builder().orderedQuota(1).build();
        assertThatThrownBy(() -> service.updateOrderedQuantity(oqId, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ordered Quantity not found");
    }

    @Test
    void update_fail_pkgNotPending() {
        UpdateOrderedQuantityRequestDTO req = UpdateOrderedQuantityRequestDTO.builder().orderedQuota(1).build();

        when(orderedQuantityRepository.findById(oqId)).thenReturn(Optional.of(oqSaved));
        when(planRepository.findById(planId)).thenReturn(Optional.of(planGeneric));
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(actGeneric));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(pkgProcessed));

        assertThatThrownBy(() -> service.updateOrderedQuantity(oqId, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Package status must be 'Pending'");
    }

    @Test
    void update_fail_capacity() {
        UpdateOrderedQuantityRequestDTO req = UpdateOrderedQuantityRequestDTO.builder().orderedQuota(999).build();

        when(orderedQuantityRepository.findById(oqId)).thenReturn(Optional.of(oqSaved));
        when(planRepository.findById(planId)).thenReturn(Optional.of(planGeneric));
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(actGeneric));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(pkgPending));

        assertThatThrownBy(() -> service.updateOrderedQuantity(oqId, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ordered quantity cannot exceed activity capacity");
    }

    @Test
    void update_fail_planQuotaExceed() {
        UpdateOrderedQuantityRequestDTO req = UpdateOrderedQuantityRequestDTO.builder().orderedQuota(60).build();

        OrderedQuantity other = oqSaved.toBuilder().id(UUID.randomUUID()).orderedQuota(20).build();

        when(orderedQuantityRepository.findById(oqId)).thenReturn(Optional.of(oqSaved));
        when(planRepository.findById(planId)).thenReturn(Optional.of(planGeneric));
        // capacity dinaikkan
        Activity highCap = actGeneric.toBuilder().capacity(100).build();
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(highCap));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(pkgPending));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(List.of(other, oqSaved));

        assertThatThrownBy(() -> service.updateOrderedQuantity(oqId, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Total ordered quantity for this plan cannot exceed package quota");
    }

    @Test
    @DisplayName("Delete success: recalc price & status")
    void delete_success_recalc() {
        OrderedQuantity other = oqSaved.toBuilder().id(UUID.randomUUID()).orderedQuota(15).build();

        when(orderedQuantityRepository.findById(oqId)).thenReturn(Optional.of(oqSaved));
        when(planRepository.findById(planId)).thenReturn(Optional.of(planGeneric));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(pkgPending));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(List.of(other));
        when(planRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.deleteOrderedQuantity(oqId);

        verify(orderedQuantityRepository).delete(oqSaved);
        // price jadi 15 * 500k = 7.5jt
        verify(planRepository).save(argThat(p -> p.getPrice() == 7_500_000L));
    }

    @Test
    void delete_fail_notFound() {
        assertThatThrownBy(() -> service.deleteOrderedQuantity(oqId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ordered Quantity not found");
        verify(orderedQuantityRepository, never()).delete(any());
    }

    @Test
    void delete_fail_pkgNotPending() {
        when(orderedQuantityRepository.findById(oqId)).thenReturn(Optional.of(oqSaved));
        when(planRepository.findById(planId)).thenReturn(Optional.of(planGeneric));
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(pkgProcessed));

        assertThatThrownBy(() -> service.deleteOrderedQuantity(oqId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Package status must be 'Pending'");
        verify(orderedQuantityRepository, never()).delete(any());
    }
}

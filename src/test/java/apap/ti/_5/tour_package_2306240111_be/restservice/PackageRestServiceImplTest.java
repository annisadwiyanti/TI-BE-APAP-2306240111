package apap.ti._5.tour_package_2306240111_be.restservice;

import apap.ti._5.tour_package_2306240111_be.model.Package;
import apap.ti._5.tour_package_2306240111_be.model.Plan;
import apap.ti._5.tour_package_2306240111_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306240111_be.repository.PackageRepository;
import apap.ti._5.tour_package_2306240111_be.repository.PlanRepository;
import apap.ti._5.tour_package_2306240111_be.repository.OrderedQuantityRepository;
import apap.ti._5.tour_package_2306240111_be.restdto.request.packagereq.CreatePackageRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.request.packagereq.UpdatePackageRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PackageRestServiceImplTest {

    @Mock
    private PackageRepository packageRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private OrderedQuantityRepository orderedQuantityRepository;

    @InjectMocks
    private PackageRestServiceImpl packageRestService;

    private Package package_;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private CreatePackageRequestDTO createDTO;
    private UpdatePackageRequestDTO updateDTO;

    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.of(2025, 11, 8, 10, 0);
        endDate = LocalDateTime.of(2025, 11, 15, 12, 0);

        package_ = Package.builder()
                .id("PACK-USER123-001")
                .userId("USER123")
                .packageName("Bali Holiday Package")
                .quota(10)
                .price(50000000L)
                .status("Pending")
                .startDate(startDate)
                .endDate(endDate)
                .isDeleted(false)
                .build();

        createDTO = new CreatePackageRequestDTO();
        createDTO.setUserId("USER123");
        createDTO.setPackageName("Bali Holiday Package");
        createDTO.setQuota(10);
        createDTO.setStartDate(startDate);
        createDTO.setEndDate(endDate);

        updateDTO = new UpdatePackageRequestDTO();
        updateDTO.setPackageName("Updated Package");
        updateDTO.setQuota(15);
        updateDTO.setStartDate(startDate);
        updateDTO.setEndDate(endDate);
    }

    @Test
    void testGetAllPackagesSuccess() {
        List<Package> packages = new ArrayList<>();
        packages.add(package_);
        Page<Package> page = new PageImpl<>(packages);
        Pageable pageable = PageRequest.of(0, 10);

        when(packageRepository.findAll(pageable)).thenReturn(page);

        Page<Package> result = packageRestService.getAllPackages(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(packageRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetPackageByIdSuccess() {
        when(packageRepository.findById("PACK-USER123-001")).thenReturn(Optional.of(package_));

        Package result = packageRestService.getPackageById("PACK-USER123-001");

        assertNotNull(result);
        assertEquals("Bali Holiday Package", result.getPackageName());
        verify(packageRepository, times(1)).findById("PACK-USER123-001");
    }

    @Test
    void testGetPackageByIdNotFound() {
        when(packageRepository.findById("NON-EXISTENT")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            packageRestService.getPackageById("NON-EXISTENT");
        });
        
        verify(packageRepository, times(1)).findById("NON-EXISTENT");
    }

    @Test
    void testCreatePackageSuccess() {
        when(packageRepository.countByUserId("USER123")).thenReturn(0L);
        when(packageRepository.save(any(Package.class))).thenReturn(package_);

        Package result = packageRestService.createPackage(createDTO);

        assertNotNull(result);
        assertEquals("PACK-USER123-001", result.getId());
        assertEquals("Pending", result.getStatus());
        assertEquals(0L, result.getPrice());
        verify(packageRepository, times(1)).save(any(Package.class));
    }

    @Test
    void testCreatePackageInvalidDateRange() {
        createDTO.setStartDate(LocalDateTime.of(2025, 11, 15, 12, 0));
        createDTO.setEndDate(LocalDateTime.of(2025, 11, 8, 10, 0));

        assertThrows(RuntimeException.class, () -> {
            packageRestService.createPackage(createDTO);
        });
    }

    @Test
    void testCreatePackageSameDateRange() {
        createDTO.setStartDate(LocalDateTime.of(2025, 11, 8, 10, 0));
        createDTO.setEndDate(LocalDateTime.of(2025, 11, 8, 10, 0));

        assertThrows(RuntimeException.class, () -> {
            packageRestService.createPackage(createDTO);
        });
    }

    @Test
    void testGeneratePackageIdFirstPackage() {
        when(packageRepository.countByUserId("USER123")).thenReturn(0L);

        String result = packageRestService.generatePackageId("USER123");

        assertEquals("PACK-USER123-001", result);
    }

    @Test
    void testGeneratePackageIdMultiplePackages() {
        when(packageRepository.countByUserId("USER123")).thenReturn(5L);

        String result = packageRestService.generatePackageId("USER123");

        assertEquals("PACK-USER123-006", result);
    }

    @Test
    void testDeletePackageSuccess() {
        when(packageRepository.findById("PACK-USER123-001")).thenReturn(Optional.of(package_));
        when(planRepository.findByPackageId("PACK-USER123-001")).thenReturn(new ArrayList<>());
        when(packageRepository.save(any(Package.class))).thenReturn(package_);

        packageRestService.deletePackage("PACK-USER123-001");

        assertTrue(package_.getIsDeleted());
        verify(packageRepository, times(1)).save(any(Package.class));
    }

    @Test
    void testDeletePackageNotFound() {
        when(packageRepository.findById("NON-EXISTENT")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            packageRestService.deletePackage("NON-EXISTENT");
        });
    }

    @Test
    void testDeletePackageNotPending() {
        Package processedPackage = package_.toBuilder().status("Processed").build();
        when(packageRepository.findById("PACK-USER123-001")).thenReturn(Optional.of(processedPackage));

        assertThrows(RuntimeException.class, () -> {
            packageRestService.deletePackage("PACK-USER123-001");
        });
    }

    @Test
    void testDeletePackageWithPlans() {
        List<Plan> plans = new ArrayList<>();
        Plan plan = Plan.builder()
                .id(UUID.randomUUID())
                .packageId("PACK-USER123-001")
                .planName("Test Plan")
                .price(1500000L)
                .activityType("Flight")
                .status("Unfulfilled")
                .startDate(startDate)
                .endDate(endDate)
                .isDeleted(false)
                .build();
        plans.add(plan);

        when(packageRepository.findById("PACK-USER123-001")).thenReturn(Optional.of(package_));
        when(planRepository.findByPackageId("PACK-USER123-001")).thenReturn(plans);
        when(orderedQuantityRepository.findByPlanId(plan.getId())).thenReturn(new ArrayList<>());
        when(packageRepository.save(any(Package.class))).thenReturn(package_);

        packageRestService.deletePackage("PACK-USER123-001");

        verify(planRepository, times(1)).delete(plan);
        verify(packageRepository, times(1)).save(any(Package.class));
    }

    @Test
    void testUpdatePackageSuccess() {
        when(packageRepository.findById("PACK-USER123-001")).thenReturn(Optional.of(package_));
        when(planRepository.findByPackageId("PACK-USER123-001")).thenReturn(new ArrayList<>());
        when(packageRepository.save(any(Package.class))).thenReturn(package_);

        Package result = packageRestService.updatePackage("PACK-USER123-001", updateDTO);

        assertNotNull(result);
        verify(packageRepository, times(1)).save(any(Package.class));
    }

    @Test
    void testUpdatePackageNotFound() {
        when(packageRepository.findById("NON-EXISTENT")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            packageRestService.updatePackage("NON-EXISTENT", updateDTO);
        });
    }

    @Test
    void testUpdatePackageNotPending() {
        Package processedPackage = package_.toBuilder().status("Processed").build();
        when(packageRepository.findById("PACK-USER123-001")).thenReturn(Optional.of(processedPackage));

        assertThrows(RuntimeException.class, () -> {
            packageRestService.updatePackage("PACK-USER123-001", updateDTO);
        });
    }

    @Test
    void testUpdatePackageWithExistingPlans() {
        List<Plan> plans = new ArrayList<>();
        plans.add(Plan.builder()
                .id(UUID.randomUUID())
                .packageId("PACK-USER123-001")
                .planName("Test Plan")
                .price(1500000L)
                .activityType("Flight")
                .status("Unfulfilled")
                .startDate(startDate)
                .endDate(endDate)
                .isDeleted(false)
                .build());

        when(packageRepository.findById("PACK-USER123-001")).thenReturn(Optional.of(package_));
        when(planRepository.findByPackageId("PACK-USER123-001")).thenReturn(plans);

        assertThrows(RuntimeException.class, () -> {
            packageRestService.updatePackage("PACK-USER123-001", updateDTO);
        });
    }

    @Test
    void testUpdatePackageInvalidDateRange() {
        when(packageRepository.findById("PACK-USER123-001")).thenReturn(Optional.of(package_));
        when(planRepository.findByPackageId("PACK-USER123-001")).thenReturn(new ArrayList<>());

        updateDTO.setStartDate(LocalDateTime.of(2025, 11, 15, 12, 0));
        updateDTO.setEndDate(LocalDateTime.of(2025, 11, 8, 10, 0));

        assertThrows(RuntimeException.class, () -> {
            packageRestService.updatePackage("PACK-USER123-001", updateDTO);
        });
    }

    @Test
    void testProcessPackageSuccess() {
        UUID planId = UUID.randomUUID();
        List<Plan> plans = new ArrayList<>();
        Plan fulfilledPlan = Plan.builder()
                .id(planId)
                .packageId("PACK-USER123-001")
                .planName("Fulfilled Plan")
                .price(1500000L)
                .activityType("Flight")
                .status("Fulfilled")
                .startDate(startDate)
                .endDate(endDate)
                .isDeleted(false)
                .build();
        plans.add(fulfilledPlan);

        // Create ordered quantities for the plan
        OrderedQuantity oq1 = OrderedQuantity.builder()
                .id(UUID.randomUUID())
                .planId(planId)
                .activityId("ACT-FL-00001")
                .orderedQuota(5)
                .quota(10)
                .price(1500000L)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        List<OrderedQuantity> orderedQuantities = new ArrayList<>();
        orderedQuantities.add(oq1);

        when(packageRepository.findById("PACK-USER123-001")).thenReturn(Optional.of(package_));
        when(planRepository.findByPackageId("PACK-USER123-001")).thenReturn(plans);
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(orderedQuantities);
        when(orderedQuantityRepository.save(any(OrderedQuantity.class))).thenReturn(oq1);
        when(packageRepository.save(any(Package.class))).thenReturn(package_);

        Package result = packageRestService.processPackage("PACK-USER123-001");

        assertNotNull(result);
        assertEquals("Processed", result.getStatus());
        verify(orderedQuantityRepository, times(1)).findByPlanId(planId);
        verify(orderedQuantityRepository, times(1)).save(any(OrderedQuantity.class));
        verify(packageRepository, times(1)).save(any(Package.class));
    }

    @Test
    void testProcessPackageReducesQuota() {
        UUID planId = UUID.randomUUID();
        List<Plan> plans = new ArrayList<>();
        Plan fulfilledPlan = Plan.builder()
                .id(planId)
                .packageId("PACK-USER123-001")
                .planName("Fulfilled Plan")
                .price(1500000L)
                .activityType("Flight")
                .status("Fulfilled")
                .startDate(startDate)
                .endDate(endDate)
                .isDeleted(false)
                .build();
        plans.add(fulfilledPlan);

        // Create ordered quantity with quota 10, ordered_quota 3
        OrderedQuantity oq = OrderedQuantity.builder()
                .id(UUID.randomUUID())
                .planId(planId)
                .activityId("ACT-FL-00001")
                .orderedQuota(3)
                .quota(10)
                .price(1500000L)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        List<OrderedQuantity> orderedQuantities = new ArrayList<>();
        orderedQuantities.add(oq);

        when(packageRepository.findById("PACK-USER123-001")).thenReturn(Optional.of(package_));
        when(planRepository.findByPackageId("PACK-USER123-001")).thenReturn(plans);
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(orderedQuantities);
        when(orderedQuantityRepository.save(any(OrderedQuantity.class))).thenAnswer(invocation -> {
            OrderedQuantity savedOq = invocation.getArgument(0);
            // Verify quota was reduced correctly: 10 - 3 = 7
            assertEquals(7, savedOq.getQuota());
            return savedOq;
        });
        when(packageRepository.save(any(Package.class))).thenReturn(package_);

        Package result = packageRestService.processPackage("PACK-USER123-001");

        assertNotNull(result);
        assertEquals("Processed", result.getStatus());
        verify(orderedQuantityRepository, times(1)).save(any(OrderedQuantity.class));
    }

    @Test
    void testProcessPackageMultipleOrderedQuantities() {
        UUID planId = UUID.randomUUID();
        List<Plan> plans = new ArrayList<>();
        Plan fulfilledPlan = Plan.builder()
                .id(planId)
                .packageId("PACK-USER123-001")
                .planName("Fulfilled Plan")
                .price(1500000L)
                .activityType("Flight")
                .status("Fulfilled")
                .startDate(startDate)
                .endDate(endDate)
                .isDeleted(false)
                .build();
        plans.add(fulfilledPlan);

        // Create multiple ordered quantities
        OrderedQuantity oq1 = OrderedQuantity.builder()
                .id(UUID.randomUUID())
                .planId(planId)
                .activityId("ACT-FL-00001")
                .orderedQuota(5)
                .quota(15)
                .price(1500000L)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        OrderedQuantity oq2 = OrderedQuantity.builder()
                .id(UUID.randomUUID())
                .planId(planId)
                .activityId("ACT-ACC-00001")
                .orderedQuota(2)
                .quota(8)
                .price(2000000L)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        List<OrderedQuantity> orderedQuantities = new ArrayList<>();
        orderedQuantities.add(oq1);
        orderedQuantities.add(oq2);

        when(packageRepository.findById("PACK-USER123-001")).thenReturn(Optional.of(package_));
        when(planRepository.findByPackageId("PACK-USER123-001")).thenReturn(plans);
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(orderedQuantities);
        when(orderedQuantityRepository.save(any(OrderedQuantity.class))).thenReturn(oq1);
        when(packageRepository.save(any(Package.class))).thenReturn(package_);

        Package result = packageRestService.processPackage("PACK-USER123-001");

        assertNotNull(result);
        assertEquals("Processed", result.getStatus());
        // Verify save was called for each ordered quantity
        verify(orderedQuantityRepository, times(2)).save(any(OrderedQuantity.class));
        verify(packageRepository, times(1)).save(any(Package.class));
    }

    @Test
    void testProcessPackageNotFound() {
        when(packageRepository.findById("NON-EXISTENT")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            packageRestService.processPackage("NON-EXISTENT");
        });
    }

    @Test
    void testProcessPackageUnfulfilledPlans() {
        List<Plan> plans = new ArrayList<>();
        Plan unfulfilledPlan = Plan.builder()
                .id(UUID.randomUUID())
                .packageId("PACK-USER123-001")
                .planName("Unfulfilled Plan")
                .price(1500000L)
                .activityType("Flight")
                .status("Unfulfilled")
                .startDate(startDate)
                .endDate(endDate)
                .isDeleted(false)
                .build();
        plans.add(unfulfilledPlan);

        when(packageRepository.findById("PACK-USER123-001")).thenReturn(Optional.of(package_));
        when(planRepository.findByPackageId("PACK-USER123-001")).thenReturn(plans);

        assertThrows(RuntimeException.class, () -> {
            packageRestService.processPackage("PACK-USER123-001");
        });
    }
}

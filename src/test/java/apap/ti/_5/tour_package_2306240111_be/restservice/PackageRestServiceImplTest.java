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

    private Package testPackage;
    private Plan testPlan;
    private OrderedQuantity testOrderedQuantity;
    private String packageId;
    private UUID planId;

    @BeforeEach
    void setUp() {
        packageId = "PACK-USER001-001";
        planId = UUID.randomUUID();

        testPackage = Package.builder()
                .id(packageId)
                .userId("USER001")
                .packageName("Bali Tour Package")
                .quota(10)
                .price(0L)
                .status("Pending")
                .startDate(LocalDateTime.of(2025, 12, 1, 0, 0))
                .endDate(LocalDateTime.of(2025, 12, 10, 23, 59))
                .isDeleted(false)
                .build();

        testPlan = Plan.builder()
                .id(planId)
                .packageId(packageId)
                .planName("Day 1 Flight")
                .price(1000000L)
                .activityType("Flight")
                .status("Fulfilled")
                .startDate(LocalDateTime.of(2025, 12, 1, 8, 0))
                .endDate(LocalDateTime.of(2025, 12, 1, 10, 0))
                .startLocation("Jakarta")
                .endLocation("Bali")
                .build();

        testOrderedQuantity = OrderedQuantity.builder()
                .id(UUID.randomUUID())
                .planId(planId)
                .activityId("ACT-001")
                .orderedQuota(5)
                .quota(10)
                .price(200000L)
                .startDate(LocalDateTime.of(2025, 12, 1, 8, 0))
                .endDate(LocalDateTime.of(2025, 12, 1, 10, 0))
                .build();
    }

    @Test
    void testGetAllPackages_Success() {
        List<Package> packages = List.of(testPackage);
        Page<Package> packagePage = new PageImpl<>(packages);
        Pageable pageable = PageRequest.of(0, 10);

        when(packageRepository.findAll(pageable)).thenReturn(packagePage);

        Page<Package> result = packageRestService.getAllPackages(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(packageId, result.getContent().get(0).getId());
        verify(packageRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllPackages_EmptyList() {
        Page<Package> emptyPage = new PageImpl<>(new ArrayList<>());
        Pageable pageable = PageRequest.of(0, 10);

        when(packageRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<Package> result = packageRestService.getAllPackages(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(packageRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllPackages_MultiplePage() {
        Package pkg2 = Package.builder()
                .id("PACK-USER001-002")
                .userId("USER001")
                .packageName("Jakarta Tour")
                .quota(15)
                .price(0L)
                .status("Pending")
                .startDate(LocalDateTime.of(2025, 11, 1, 0, 0))
                .endDate(LocalDateTime.of(2025, 11, 10, 23, 59))
                .isDeleted(false)
                .build();

        List<Package> packages = List.of(testPackage, pkg2);
        Page<Package> packagePage = new PageImpl<>(packages);
        Pageable pageable = PageRequest.of(0, 10);

        when(packageRepository.findAll(pageable)).thenReturn(packagePage);

        Page<Package> result = packageRestService.getAllPackages(pageable);

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testGetPackageById_Success() {
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));

        Package result = packageRestService.getPackageById(packageId);

        assertNotNull(result);
        assertEquals(packageId, result.getId());
        assertEquals("Bali Tour Package", result.getPackageName());
        verify(packageRepository, times(1)).findById(packageId);
    }

    @Test
    void testGetPackageById_NotFound() {
        when(packageRepository.findById(packageId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            packageRestService.getPackageById(packageId);
        });

        assertTrue(exception.getMessage().contains("Package not found with id"));
        verify(packageRepository, times(1)).findById(packageId);
    }

    @Test
    void testGetPackageById_VerifyAllFields() {
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));

        Package result = packageRestService.getPackageById(packageId);

        assertEquals("USER001", result.getUserId());
        assertEquals(10, result.getQuota());
        assertEquals("Pending", result.getStatus());
        assertFalse(result.getIsDeleted());
    }

    @Test
    void testCreatePackage_Success() {
        CreatePackageRequestDTO requestDTO = new CreatePackageRequestDTO();
        requestDTO.setUserId("USER001");
        requestDTO.setPackageName("New Package");
        requestDTO.setQuota(20);
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 1, 0, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 10, 0, 0));

        when(packageRepository.countByUserId("USER001")).thenReturn(2L);
        when(packageRepository.save(any(Package.class))).thenAnswer(invocation -> {
            Package pkg = invocation.getArgument(0);
            assertEquals("PACK-USER001-003", pkg.getId());
            assertEquals("Pending", pkg.getStatus());
            assertEquals(0L, pkg.getPrice());
            assertFalse(pkg.getIsDeleted());
            return pkg;
        });

        Package result = packageRestService.createPackage(requestDTO);

        assertNotNull(result);
        verify(packageRepository, times(1)).countByUserId("USER001");
        verify(packageRepository, times(1)).save(any(Package.class));
    }

    @Test
    void testCreatePackage_EndDateBeforeStartDate() {
        CreatePackageRequestDTO requestDTO = new CreatePackageRequestDTO();
        requestDTO.setUserId("USER001");
        requestDTO.setPackageName("Invalid Package");
        requestDTO.setQuota(20);
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 10, 0, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 1, 0, 0));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            packageRestService.createPackage(requestDTO);
        });

        assertTrue(exception.getMessage().contains("End date must be after start date"));
        verify(packageRepository, never()).save(any(Package.class));
    }

    @Test
    void testCreatePackage_EndDateEqualsStartDate() {
        CreatePackageRequestDTO requestDTO = new CreatePackageRequestDTO();
        requestDTO.setUserId("USER001");
        requestDTO.setPackageName("Invalid Package");
        requestDTO.setQuota(20);
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 1, 0, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 1, 0, 0));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            packageRestService.createPackage(requestDTO);
        });

        assertTrue(exception.getMessage().contains("End date must be after start date"));
        verify(packageRepository, never()).save(any(Package.class));
    }

    @Test
    void testCreatePackage_WithMinimalQuota() {
        CreatePackageRequestDTO requestDTO = new CreatePackageRequestDTO();
        requestDTO.setUserId("USER002");
        requestDTO.setPackageName("Minimal Package");
        requestDTO.setQuota(1);
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 1, 0, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 2, 0, 0));

        when(packageRepository.countByUserId("USER002")).thenReturn(0L);
        when(packageRepository.save(any(Package.class))).thenAnswer(i -> i.getArgument(0));

        Package result = packageRestService.createPackage(requestDTO);

        assertNotNull(result);
        assertEquals(1, result.getQuota());
    }

    @Test
    void testGeneratePackageId_FirstPackage() {
        when(packageRepository.countByUserId("USER001")).thenReturn(0L);

        String result = packageRestService.generatePackageId("USER001");

        assertEquals("PACK-USER001-001", result);
        verify(packageRepository, times(1)).countByUserId("USER001");
    }

    @Test
    void testGeneratePackageId_MultiplePackages() {
        when(packageRepository.countByUserId("USER001")).thenReturn(5L);

        String result = packageRestService.generatePackageId("USER001");

        assertEquals("PACK-USER001-006", result);
        verify(packageRepository, times(1)).countByUserId("USER001");
    }

    @Test
    void testGeneratePackageId_LargeCount() {
        when(packageRepository.countByUserId("USER001")).thenReturn(99L);

        String result = packageRestService.generatePackageId("USER001");

        assertEquals("PACK-USER001-100", result);
    }

    @Test
    void testGeneratePackageId_DifferentUsers() {
        when(packageRepository.countByUserId("USER999")).thenReturn(0L);

        String result = packageRestService.generatePackageId("USER999");

        assertEquals("PACK-USER999-001", result);
    }

    @Test
    void testDeletePackage_Success() {
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(planRepository.findByPackageId(packageId)).thenReturn(new ArrayList<>());
        when(packageRepository.save(any(Package.class))).thenAnswer(invocation -> {
            Package pkg = invocation.getArgument(0);
            assertTrue(pkg.getIsDeleted());
            return pkg;
        });

        packageRestService.deletePackage(packageId);

        verify(packageRepository, times(1)).findById(packageId);
        verify(planRepository, times(1)).findByPackageId(packageId);
        verify(packageRepository, times(1)).save(any(Package.class));
    }

    @Test
    void testDeletePackage_NotFound() {
        when(packageRepository.findById(packageId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            packageRestService.deletePackage(packageId);
        });

        assertTrue(exception.getMessage().contains("Package not found"));
        verify(packageRepository, never()).save(any(Package.class));
    }

    @Test
    void testDeletePackage_NotPendingStatus() {
        testPackage.setStatus("Processed");
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            packageRestService.deletePackage(packageId);
        });

        assertTrue(exception.getMessage().contains("Package status must be 'Pending'"));
        verify(packageRepository, times(1)).findById(packageId);
        verify(packageRepository, never()).save(any(Package.class));
    }

    @Test
    void testDeletePackage_WithPlansAndOrderedQuantities() {
        List<Plan> plans = List.of(testPlan);
        List<OrderedQuantity> orderedQuantities = List.of(testOrderedQuantity);

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(planRepository.findByPackageId(packageId)).thenReturn(plans);
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(orderedQuantities);
        when(packageRepository.save(any(Package.class))).thenAnswer(i -> i.getArgument(0));

        packageRestService.deletePackage(packageId);

        verify(orderedQuantityRepository, times(1)).deleteAll(orderedQuantities);
        verify(planRepository, times(1)).delete(testPlan);
        verify(packageRepository, times(1)).save(any(Package.class));
    }

    @Test
    void testDeletePackage_WithMultiplePlans() {
        Plan plan2 = Plan.builder()
                .id(UUID.randomUUID())
                .packageId(packageId)
                .planName("Day 2 Hotel")
                .status("Fulfilled")
                .build();

        List<Plan> plans = List.of(testPlan, plan2);

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(planRepository.findByPackageId(packageId)).thenReturn(plans);
        when(orderedQuantityRepository.findByPlanId(any())).thenReturn(new ArrayList<>());
        when(packageRepository.save(any(Package.class))).thenAnswer(i -> i.getArgument(0));

        packageRestService.deletePackage(packageId);

        verify(planRepository, times(2)).delete(any(Plan.class));
    }

    @Test
    void testUpdatePackage_Success() {
        UpdatePackageRequestDTO requestDTO = new UpdatePackageRequestDTO();
        requestDTO.setPackageName("Updated Package");
        requestDTO.setQuota(30);
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 1, 0, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 15, 0, 0));

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(planRepository.findByPackageId(packageId)).thenReturn(new ArrayList<>());
        when(packageRepository.save(any(Package.class))).thenAnswer(invocation -> {
            Package pkg = invocation.getArgument(0);
            assertEquals("Updated Package", pkg.getPackageName());
            assertEquals(30, pkg.getQuota());
            return pkg;
        });

        Package result = packageRestService.updatePackage(packageId, requestDTO);

        assertNotNull(result);
        verify(packageRepository, times(1)).findById(packageId);
        verify(planRepository, times(1)).findByPackageId(packageId);
        verify(packageRepository, times(1)).save(any(Package.class));
    }

    @Test
    void testUpdatePackage_NotFound() {
        UpdatePackageRequestDTO requestDTO = new UpdatePackageRequestDTO();
        requestDTO.setPackageName("Updated Package");
        requestDTO.setQuota(30);
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 1, 0, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 15, 0, 0));

        when(packageRepository.findById(packageId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            packageRestService.updatePackage(packageId, requestDTO);
        });

        assertTrue(exception.getMessage().contains("Package not found"));
        verify(packageRepository, never()).save(any(Package.class));
    }

    @Test
    void testUpdatePackage_NotPendingStatus() {
        testPackage.setStatus("Processed");
        UpdatePackageRequestDTO requestDTO = new UpdatePackageRequestDTO();
        requestDTO.setPackageName("Updated Package");
        requestDTO.setQuota(30);
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 1, 0, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 15, 0, 0));

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            packageRestService.updatePackage(packageId, requestDTO);
        });

        assertTrue(exception.getMessage().contains("Package status must be 'Pending'"));
        verify(packageRepository, never()).save(any(Package.class));
    }

    @Test
    void testUpdatePackage_WithExistingPlans() {
        UpdatePackageRequestDTO requestDTO = new UpdatePackageRequestDTO();
        requestDTO.setPackageName("Updated Package");
        requestDTO.setQuota(30);
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 1, 0, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 15, 0, 0));

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(planRepository.findByPackageId(packageId)).thenReturn(List.of(testPlan));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            packageRestService.updatePackage(packageId, requestDTO);
        });

        assertTrue(exception.getMessage().contains("Package has associated plans"));
        verify(packageRepository, never()).save(any(Package.class));
    }

    @Test
    void testUpdatePackage_InvalidDates() {
        UpdatePackageRequestDTO requestDTO = new UpdatePackageRequestDTO();
        requestDTO.setPackageName("Updated Package");
        requestDTO.setQuota(30);
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 15, 0, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 1, 0, 0));

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(planRepository.findByPackageId(packageId)).thenReturn(new ArrayList<>());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            packageRestService.updatePackage(packageId, requestDTO);
        });

        assertTrue(exception.getMessage().contains("End date must be after start date"));
        verify(packageRepository, never()).save(any(Package.class));
    }

    @Test
    void testUpdatePackage_UserIdNotChanged() {
        UpdatePackageRequestDTO requestDTO = new UpdatePackageRequestDTO();
        requestDTO.setPackageName("Updated Package");
        requestDTO.setQuota(30);
        requestDTO.setStartDate(LocalDateTime.of(2025, 12, 1, 0, 0));
        requestDTO.setEndDate(LocalDateTime.of(2025, 12, 15, 0, 0));

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(planRepository.findByPackageId(packageId)).thenReturn(new ArrayList<>());
        when(packageRepository.save(any(Package.class))).thenAnswer(invocation -> {
            Package pkg = invocation.getArgument(0);
            assertEquals("USER001", pkg.getUserId());
            return pkg;
        });

        packageRestService.updatePackage(packageId, requestDTO);

        verify(packageRepository).save(any(Package.class));
    }

    @Test
    void testProcessPackage_Success() {
        testPlan.setStatus("Fulfilled");
        List<Plan> plans = List.of(testPlan);
        List<OrderedQuantity> orderedQuantities = List.of(testOrderedQuantity);

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(planRepository.findByPackageId(packageId)).thenReturn(plans);
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(orderedQuantities);
        when(orderedQuantityRepository.save(any(OrderedQuantity.class))).thenAnswer(invocation -> {
            OrderedQuantity oq = invocation.getArgument(0);
            assertEquals(5, oq.getQuota()); // 10 - 5 = 5
            return oq;
        });
        when(packageRepository.save(any(Package.class))).thenAnswer(invocation -> {
            Package pkg = invocation.getArgument(0);
            assertEquals("Processed", pkg.getStatus());
            return pkg;
        });

        Package result = packageRestService.processPackage(packageId);

        assertNotNull(result);
        verify(packageRepository, times(1)).findById(packageId);
        verify(planRepository, times(1)).findByPackageId(packageId);
        verify(orderedQuantityRepository, times(1)).findByPlanId(planId);
        verify(packageRepository, times(1)).save(any(Package.class));
    }

    @Test
    void testProcessPackage_PlanNotFulfilled() {
        testPlan.setStatus("Unfulfilled");
        List<Plan> plans = List.of(testPlan);

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(planRepository.findByPackageId(packageId)).thenReturn(plans);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            packageRestService.processPackage(packageId);
        });

        assertTrue(exception.getMessage().contains("All plans must have status 'Fulfilled'"));
        verify(packageRepository, never()).save(any(Package.class));
    }

    @Test
    void testProcessPackage_PackageNotFound() {
        when(packageRepository.findById(packageId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            packageRestService.processPackage(packageId);
        });

        assertTrue(exception.getMessage().contains("Package not found"));
        verify(planRepository, never()).findByPackageId(anyString());
    }

    @Test
    void testProcessPackage_MultiplePlansAllFulfilled() {
        UUID plan2Id = UUID.randomUUID();
        Plan plan2 = Plan.builder()
                .id(plan2Id)
                .packageId(packageId)
                .status("Fulfilled")
                .build();

        List<Plan> plans = List.of(testPlan, plan2);

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(planRepository.findByPackageId(packageId)).thenReturn(plans);
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(new ArrayList<>());
        when(orderedQuantityRepository.findByPlanId(plan2Id)).thenReturn(new ArrayList<>());
        when(packageRepository.save(any(Package.class))).thenAnswer(i -> i.getArgument(0));

        Package result = packageRestService.processPackage(packageId);

        assertNotNull(result);
        verify(orderedQuantityRepository, times(1)).findByPlanId(planId);
        verify(orderedQuantityRepository, times(1)).findByPlanId(plan2Id);
    }

    @Test
    void testProcessPackage_MultipleOrderedQuantities() {
        OrderedQuantity oq2 = OrderedQuantity.builder()
                .id(UUID.randomUUID())
                .planId(planId)
                .activityId("ACT-002")
                .orderedQuota(3)
                .quota(8)
                .build();

        List<OrderedQuantity> orderedQuantities = List.of(testOrderedQuantity, oq2);

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(planRepository.findByPackageId(packageId)).thenReturn(List.of(testPlan));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(orderedQuantities);
        when(orderedQuantityRepository.save(any(OrderedQuantity.class))).thenAnswer(i -> i.getArgument(0));
        when(packageRepository.save(any(Package.class))).thenAnswer(i -> i.getArgument(0));

        packageRestService.processPackage(packageId);

        verify(orderedQuantityRepository, times(2)).save(any(OrderedQuantity.class));
    }

    @Test
    void testProcessPackage_QuotaReductionCalculation() {
        testOrderedQuantity.setQuota(20);
        testOrderedQuantity.setOrderedQuota(7);
        
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(planRepository.findByPackageId(packageId)).thenReturn(List.of(testPlan));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(List.of(testOrderedQuantity));
        when(orderedQuantityRepository.save(any(OrderedQuantity.class))).thenAnswer(invocation -> {
            OrderedQuantity oq = invocation.getArgument(0);
            assertEquals(13, oq.getQuota()); // 20 - 7 = 13
            return oq;
        });
        when(packageRepository.save(any(Package.class))).thenAnswer(i -> i.getArgument(0));

        packageRestService.processPackage(packageId);

        verify(orderedQuantityRepository).save(any(OrderedQuantity.class));
    }

    @Test
    void testProcessPackage_EmptyPlans() {
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(planRepository.findByPackageId(packageId)).thenReturn(new ArrayList<>());
        when(packageRepository.save(any(Package.class))).thenAnswer(i -> {
            Package pkg = i.getArgument(0);
            assertEquals("Processed", pkg.getStatus());
            return pkg;
        });

        Package result = packageRestService.processPackage(packageId);

        assertNotNull(result);
        assertEquals("Processed", result.getStatus());
    }

    @Test
    void testProcessPackage_NoOrderedQuantities() {
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(testPackage));
        when(planRepository.findByPackageId(packageId)).thenReturn(List.of(testPlan));
        when(orderedQuantityRepository.findByPlanId(planId)).thenReturn(new ArrayList<>());
        when(packageRepository.save(any(Package.class))).thenAnswer(i -> i.getArgument(0));

        Package result = packageRestService.processPackage(packageId);

        assertNotNull(result);
        verify(orderedQuantityRepository, never()).save(any());
    }
}
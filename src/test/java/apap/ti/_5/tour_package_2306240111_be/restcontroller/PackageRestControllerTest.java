package apap.ti._5.tour_package_2306240111_be.restcontroller;

import apap.ti._5.tour_package_2306240111_be.model.Package;
import apap.ti._5.tour_package_2306240111_be.model.Plan;
import apap.ti._5.tour_package_2306240111_be.repository.PlanRepository;
import apap.ti._5.tour_package_2306240111_be.restdto.request.packagereq.CreatePackageRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.request.packagereq.UpdatePackageRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restservice.PackageRestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class PackageRestControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private PackageRestService packageRestService;

    @MockBean
    private PlanRepository planRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper;

    private Package testPackage;
    private Plan testPlan;
    private String packageId;
    private UUID planId;

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Configure ObjectMapper to handle LocalDateTime
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        packageId = "PKG-2025-001";
        planId = UUID.randomUUID();

        testPackage = Package.builder()
                .id(packageId)
                .packageName("Bali Holiday Package")
                .userId("USER-001")
                .quota(50)
                .price(5000000L)
                .status("AVAILABLE")
                .startDate(LocalDateTime.of(2025, 12, 15, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 20, 18, 0))
                .build();

        testPlan = Plan.builder()
                .id(planId)
                .packageId(packageId)
                .planName("Day 1 - Jakarta to Bali Flight")
                .price(1500000L)
                .activityType("Flight")
                .status("ACTIVE")
                .startDate(LocalDateTime.of(2025, 12, 15, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 15, 12, 0))
                .startLocation("Jakarta")
                .endLocation("Bali")
                .build();
    }

    @Test
    void testGetAllPackagesSuccess() throws Exception {
        List<Package> packageList = new ArrayList<>();
        packageList.add(testPackage);

        Page<Package> packagePage = new PageImpl<>(packageList, PageRequest.of(0, 10), 1);

        when(packageRestService.getAllPackages(any(Pageable.class)))
                .thenReturn(packagePage);

        mockMvc.perform(get("/api/packages")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success retrieve all packages"))
                .andExpect(jsonPath("$.data.packages", hasSize(1)))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.totalItems").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.packages[0].id").value(packageId))
                .andExpect(jsonPath("$.data.packages[0].packageName").value("Bali Holiday Package"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(packageRestService, times(1)).getAllPackages(any(Pageable.class));
    }

    @Test
    void testGetAllPackagesEmpty() throws Exception {
        Page<Package> emptyPage = new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 10), 0);

        when(packageRestService.getAllPackages(any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/packages")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success retrieve all packages"))
                .andExpect(jsonPath("$.data.packages", hasSize(0)))
                .andExpect(jsonPath("$.data.totalItems").value(0))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(packageRestService, times(1)).getAllPackages(any(Pageable.class));
    }

    @Test
    void testGetAllPackagesException() throws Exception {
        when(packageRestService.getAllPackages(any(Pageable.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/packages")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Error: Database error"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(packageRestService, times(1)).getAllPackages(any(Pageable.class));
    }

    @Test
    void testGetAllPackagesMultiplePages() throws Exception {
        List<Package> packageList = new ArrayList<>();
        packageList.add(testPackage);

        Package pkg2 = testPackage.toBuilder()
                .id("PKG-2025-002")
                .packageName("Lombok Holiday")
                .build();
        packageList.add(pkg2);

        Page<Package> packagePage = new PageImpl<>(packageList, PageRequest.of(0, 2), 5);

        when(packageRestService.getAllPackages(any(Pageable.class)))
                .thenReturn(packagePage);

        mockMvc.perform(get("/api/packages")
                .param("page", "0")
                .param("size", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.packages", hasSize(2)))
                .andExpect(jsonPath("$.data.totalItems").value(5))
                .andExpect(jsonPath("$.data.totalPages").value(3));

        verify(packageRestService, times(1)).getAllPackages(any(Pageable.class));
    }

    @Test
    void testGetPackageDetailSuccess() throws Exception {
        List<Plan> planList = new ArrayList<>();
        planList.add(testPlan);

        when(packageRestService.getPackageById(packageId))
                .thenReturn(testPackage);
        when(planRepository.findByPackageId(packageId))
                .thenReturn(planList);

        mockMvc.perform(get("/api/packages/{id}", packageId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success retrieve package detail"))
                .andExpect(jsonPath("$.data.id").value(packageId))
                .andExpect(jsonPath("$.data.packageName").value("Bali Holiday Package"))
                .andExpect(jsonPath("$.data.quota").value(50))
                .andExpect(jsonPath("$.data.price").value(5000000))
                .andExpect(jsonPath("$.data.plans", hasSize(1)))
                .andExpect(jsonPath("$.data.plans[0].id").value(planId.toString()))
                .andExpect(jsonPath("$.data.plans[0].planName").value("Day 1 - Jakarta to Bali Flight"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(packageRestService, times(1)).getPackageById(packageId);
        verify(planRepository, times(1)).findByPackageId(packageId);
    }

    @Test
    void testGetPackageDetailNotFound() throws Exception {
        when(packageRestService.getPackageById(packageId))
                .thenThrow(new RuntimeException("Package not found"));

        mockMvc.perform(get("/api/packages/{id}", packageId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Error: Package not found"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(packageRestService, times(1)).getPackageById(packageId);
    }

    @Test
    void testGetCreatePackageForm() throws Exception {
        mockMvc.perform(get("/api/packages/create")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Create package form structure"))
                .andExpect(jsonPath("$.data.packageName").value("text input"))
                .andExpect(jsonPath("$.data.userId").value("text input"))
                .andExpect(jsonPath("$.data.quota").value("numeric input"))
                .andExpect(jsonPath("$.data.startDate").value("date input"))
                .andExpect(jsonPath("$.data.endDate").value("date input"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testCreatePackageSuccess() throws Exception {
        CreatePackageRequestDTO request = CreatePackageRequestDTO.builder()
                .packageName("Bali Holiday Package")
                .userId("USER-001")
                .quota(50)
                .startDate(LocalDateTime.of(2025, 12, 15, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 20, 18, 0))
                .build();

        when(packageRestService.createPackage(any(CreatePackageRequestDTO.class)))
                .thenReturn(testPackage);

        mockMvc.perform(post("/api/packages/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Package created successfully"))
                .andExpect(jsonPath("$.data.id").value(packageId))
                .andExpect(jsonPath("$.data.packageName").value("Bali Holiday Package"))
                .andExpect(jsonPath("$.data.userId").value("USER-001"))
                .andExpect(jsonPath("$.data.quota").value(50))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(packageRestService, times(1)).createPackage(any(CreatePackageRequestDTO.class));
    }

    @Test
    void testCreatePackageRuntimeException() throws Exception {
        CreatePackageRequestDTO request = CreatePackageRequestDTO.builder()
                .packageName("Test Package")
                .userId("INVALID-USER")
                .quota(50)
                .startDate(LocalDateTime.of(2025, 12, 15, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 20, 18, 0))
                .build();

        when(packageRestService.createPackage(any(CreatePackageRequestDTO.class)))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(post("/api/packages/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error: User not found"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(packageRestService, times(1)).createPackage(any(CreatePackageRequestDTO.class));
    }

    @Test
    void testDeletePackageSuccess() throws Exception {
        doNothing().when(packageRestService).deletePackage(packageId);

        mockMvc.perform(delete("/api/packages/{id}/delete", packageId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Package deleted successfully"))
                .andExpect(jsonPath("$.data").value("Package with id " + packageId + " has been deleted along with all associated plans"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(packageRestService, times(1)).deletePackage(packageId);
    }

    @Test
    void testDeletePackageBadRequest() throws Exception {
        doThrow(new RuntimeException("Cannot delete, package is in use"))
                .when(packageRestService).deletePackage(packageId);

        mockMvc.perform(delete("/api/packages/{id}/delete", packageId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error: Cannot delete, package is in use"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(packageRestService, times(1)).deletePackage(packageId);
    }

    @Test
    void testGetEditPackageFormSuccess() throws Exception {
        List<Plan> planList = new ArrayList<>();
        planList.add(testPlan);

        when(packageRestService.getPackageById(packageId))
                .thenReturn(testPackage);
        when(planRepository.findByPackageId(packageId))
                .thenReturn(planList);

        mockMvc.perform(get("/api/packages/{id}/edit", packageId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Edit package form with prefilled data and plans"))
                .andExpect(jsonPath("$.data.id").value(packageId))
                .andExpect(jsonPath("$.data.packageName").value("Bali Holiday Package"))
                .andExpect(jsonPath("$.data.plans", hasSize(1)))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(packageRestService, times(1)).getPackageById(packageId);
        verify(planRepository, times(1)).findByPackageId(packageId);
    }

    @Test
    void testGetEditPackageFormNotFound() throws Exception {
        when(packageRestService.getPackageById(packageId))
                .thenThrow(new RuntimeException("Package not found"));

        mockMvc.perform(get("/api/packages/{id}/edit", packageId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Error: Package not found"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(packageRestService, times(1)).getPackageById(packageId);
    }

    @Test
    void testUpdatePackageSuccess() throws Exception {
        UpdatePackageRequestDTO request = UpdatePackageRequestDTO.builder()
                .packageName("Updated Bali Package")
                .quota(60)
                .startDate(LocalDateTime.of(2025, 12, 15, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 20, 18, 0))
                .build();

        Package updatedPackage = testPackage.toBuilder()
                .packageName("Updated Bali Package")
                .quota(60)
                .build();

        when(packageRestService.updatePackage(eq(packageId), any(UpdatePackageRequestDTO.class)))
                .thenReturn(updatedPackage);

        mockMvc.perform(put("/api/packages/{id}/edit", packageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Package updated successfully"))
                .andExpect(jsonPath("$.data.id").value(packageId))
                .andExpect(jsonPath("$.data.packageName").value("Updated Bali Package"))
                .andExpect(jsonPath("$.data.quota").value(60))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(packageRestService, times(1)).updatePackage(eq(packageId), any(UpdatePackageRequestDTO.class));
    }

    @Test
    void testUpdatePackageBadRequest() throws Exception {
        UpdatePackageRequestDTO request = UpdatePackageRequestDTO.builder()
                .packageName("Updated Package")
                .quota(200)
                .startDate(LocalDateTime.of(2025, 12, 15, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 20, 18, 0))
                .build();

        when(packageRestService.updatePackage(eq(packageId), any(UpdatePackageRequestDTO.class)))
                .thenThrow(new RuntimeException("Cannot update, package is locked"));

        mockMvc.perform(put("/api/packages/{id}/edit", packageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error: Cannot update, package is locked"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(packageRestService, times(1)).updatePackage(eq(packageId), any(UpdatePackageRequestDTO.class));
    }

    @Test
    void testProcessPackageSuccess() throws Exception {
        Package processedPackage = testPackage.toBuilder()
                .status("PROCESSED")
                .build();

        when(packageRestService.processPackage(packageId))
                .thenReturn(processedPackage);

        mockMvc.perform(put("/api/packages/{id}/process", packageId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Package processed successfully"))
                .andExpect(jsonPath("$.data.id").value(packageId))
                .andExpect(jsonPath("$.data.status").value("PROCESSED"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(packageRestService, times(1)).processPackage(packageId);
    }

    @Test
    void testProcessPackageBadRequest() throws Exception {
        when(packageRestService.processPackage(packageId))
                .thenThrow(new RuntimeException("Package is incomplete"));

        mockMvc.perform(put("/api/packages/{id}/process", packageId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error: Package is incomplete"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(packageRestService, times(1)).processPackage(packageId);
    }

    @Test
    void testGetPackageDetailNoPlansList() throws Exception {
        when(packageRestService.getPackageById(packageId))
                .thenReturn(testPackage);
        when(planRepository.findByPackageId(packageId))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/packages/{id}", packageId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.plans", hasSize(0)))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(packageRestService, times(1)).getPackageById(packageId);
        verify(planRepository, times(1)).findByPackageId(packageId);
    }

    @Test
    void testGetAllPackagesResponseStructure() throws Exception {
        List<Package> packageList = new ArrayList<>();
        packageList.add(testPackage);

        Page<Package> packagePage = new PageImpl<>(packageList, PageRequest.of(0, 10), 1);

        when(packageRestService.getAllPackages(any(Pageable.class)))
                .thenReturn(packagePage);

        mockMvc.perform(get("/api/packages")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").exists());

        verify(packageRestService, times(1)).getAllPackages(any(Pageable.class));
    }

    @Test
    void testCreatePackageResponseStructure() throws Exception {
        CreatePackageRequestDTO request = CreatePackageRequestDTO.builder()
                .packageName("Test Package")
                .userId("USER-001")
                .quota(50)
                .startDate(LocalDateTime.of(2025, 12, 15, 10, 0))
                .endDate(LocalDateTime.of(2025, 12, 20, 18, 0))
                .build();

        when(packageRestService.createPackage(any(CreatePackageRequestDTO.class)))
                .thenReturn(testPackage);

        mockMvc.perform(post("/api/packages/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").exists());

        verify(packageRestService, times(1)).createPackage(any(CreatePackageRequestDTO.class));
    }

    @Test
    void testGetEditPackageFormEmptyPlans() throws Exception {
        when(packageRestService.getPackageById(packageId))
                .thenReturn(testPackage);
        when(planRepository.findByPackageId(packageId))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/packages/{id}/edit", packageId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.plans", hasSize(0)));

        verify(packageRestService, times(1)).getPackageById(packageId);
        verify(planRepository, times(1)).findByPackageId(packageId);
    }

    @Test
    void testGetAllPackagesWithDefaultParameters() throws Exception {
        List<Package> packageList = new ArrayList<>();
        packageList.add(testPackage);

        Page<Package> packagePage = new PageImpl<>(packageList, PageRequest.of(0, 10), 1);

        when(packageRestService.getAllPackages(any(Pageable.class)))
                .thenReturn(packagePage);

        mockMvc.perform(get("/api/packages")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.packages", hasSize(1)))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(packageRestService, times(1)).getAllPackages(any(Pageable.class));
    }


        @Test
        void testGetAllPackages_InvalidPageSize_Returns500_BeforeServiceCalled() throws Exception {
        // size=0 bikin PageRequest.of(0, 0) lempar IllegalArgumentException di dalam controller (sebelum call service)
        mockMvc.perform(get("/api/packages")
                .param("page", "0")
                .param("size", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message", containsString("Error:")))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());

        // verify service TIDAK dipanggil (karena gagal di PageRequest)
        verify(packageRestService, never()).getAllPackages(any(Pageable.class));
   }

        @Test
        void testGetAllPackages_NegativePage_Returns500() throws Exception {
        mockMvc.perform(get("/api/packages")
                .param("page", "-1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message", containsString("Error:")))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(packageRestService, never()).getAllPackages(any(Pageable.class));
    }

        @Test
        void testCreatePackage_ServerErrorBranch() throws Exception {
        CreatePackageRequestDTO req = CreatePackageRequestDTO.builder()
                .packageName("Pkg")
                .userId("U1")
                .quota(10)
                .startDate(LocalDateTime.of(2025,12,1,10,0))
                .endDate(LocalDateTime.of(2025,12,2,10,0))
                .build();

        when(packageRestService.createPackage(any(CreatePackageRequestDTO.class)))
                .thenThrow(new RuntimeException("unexpected"));

        mockMvc.perform(post("/api/packages/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error: unexpected"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(packageRestService, times(1)).createPackage(any(CreatePackageRequestDTO.class));
    }

        @Test
        void testUpdatePackage_ResponseStructureWithManyPlansMapping() throws Exception {
        UpdatePackageRequestDTO req = UpdatePackageRequestDTO.builder()
                .packageName("Super Bali")
                .quota(120)
                .startDate(LocalDateTime.of(2025,12,10,8,0))
                .endDate(LocalDateTime.of(2025,12,20,20,0))
                .build();

        Package updated = testPackage.toBuilder()
                .packageName("Super Bali")
                .quota(120)
                .status("PENDING_REVIEW")
                .build();

        when(packageRestService.updatePackage(eq(packageId), any(UpdatePackageRequestDTO.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/packages/{id}/edit", packageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(packageId))
                .andExpect(jsonPath("$.data.packageName").value("Super Bali"))
                .andExpect(jsonPath("$.data.quota").value(120))
                .andExpect(jsonPath("$.data.status").value("PENDING_REVIEW"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(packageRestService, times(1)).updatePackage(eq(packageId), any(UpdatePackageRequestDTO.class));
    }

        @Test
        void testProcessPackage_ResponseStructureOnly() throws Exception {
        Package processed = testPackage.toBuilder().status("PROCESSED").price(9999999L).build();
        when(packageRestService.processPackage(packageId)).thenReturn(processed);

        mockMvc.perform(put("/api/packages/{id}/process", packageId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Package processed successfully"))
                .andExpect(jsonPath("$.data.status").value("PROCESSED"))
                .andExpect(jsonPath("$.data.price").value(9999999))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(packageRestService, times(1)).processPackage(packageId);
    }

        @Test
        void testGetPackageDetail_MultiplePlans_EnsurePlanMappingFields() throws Exception {
        Plan p2 = testPlan.toBuilder()
                .id(UUID.randomUUID())
                .planName("Day 2 - Activities")
                .activityType("Tour")
                .status("ACTIVE")
                .startLocation("Bali")
                .endLocation("Bali")
                .build();

        when(packageRestService.getPackageById(packageId)).thenReturn(testPackage);
        when(planRepository.findByPackageId(packageId)).thenReturn(List.of(testPlan, p2));

        mockMvc.perform(get("/api/packages/{id}", packageId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.plans", hasSize(2)))
                .andExpect(jsonPath("$.data.plans[0].planName").value("Day 1 - Jakarta to Bali Flight"))
                .andExpect(jsonPath("$.data.plans[1].planName").value("Day 2 - Activities"))
                .andExpect(jsonPath("$.data.plans[1].activityType").value("Tour"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(packageRestService, times(1)).getPackageById(packageId);
        verify(planRepository, times(1)).findByPackageId(packageId);
    }
}
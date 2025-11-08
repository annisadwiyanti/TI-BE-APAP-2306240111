package apap.ti._5.tour_package_2306240111_be.restcontroller;

import apap.ti._5.tour_package_2306240111_be.model.Package;
import apap.ti._5.tour_package_2306240111_be.model.Plan;
import apap.ti._5.tour_package_2306240111_be.repository.PlanRepository;
import apap.ti._5.tour_package_2306240111_be.restdto.request.packagereq.CreatePackageRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.BaseResponseDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.pkg.PackageDetailResponseDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.pkg.PackageResponseDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.plan.PlanResponseDTO;
import apap.ti._5.tour_package_2306240111_be.restservice.PackageRestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/packages")
public class PackageRestController {
    
    private final PackageRestService packageRestService;
    private final PlanRepository planRepository;
    
    public PackageRestController(PackageRestService packageRestService, PlanRepository planRepository) {
        this.packageRestService = packageRestService;
        this.planRepository = planRepository;
    }
    
    // Fitur 2: Read All Packages
    @GetMapping("")
    public ResponseEntity<BaseResponseDTO<Map<String, Object>>> getAllPackages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Package> packagePage = packageRestService.getAllPackages(pageable);

            List<PackageResponseDTO> packageDTOs = packagePage.getContent().stream()
                    .map(this::convertToPackageResponseDTO)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("packages", packageDTOs);
            response.put("currentPage", packagePage.getNumber());
            response.put("totalItems", packagePage.getTotalElements());
            response.put("totalPages", packagePage.getTotalPages());
            
            BaseResponseDTO<Map<String, Object>> baseResponse = new BaseResponseDTO<>();
            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setMessage("Success retrieve all packages");
            baseResponse.setTimestamp(new Date());
            baseResponse.setData(response);
            
            return ResponseEntity.ok(baseResponse);
            
        } catch (Exception e) {
            BaseResponseDTO<Map<String, Object>> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    

    // Fitur 3: Detail Package
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<PackageDetailResponseDTO>> getPackageDetail(@PathVariable String id) {
        
        try {
            Package pkg = packageRestService.getPackageById(id);
            
            List<Plan> plans = planRepository.findByPackageId(id);
            
            PackageDetailResponseDTO packageDetailDTO = convertToPackageDetailResponseDTO(pkg, plans);
            
            BaseResponseDTO<PackageDetailResponseDTO> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Success retrieve package detail");
            response.setTimestamp(new Date());
            response.setData(packageDetailDTO);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            BaseResponseDTO<PackageDetailResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            
        } catch (Exception e) {
            BaseResponseDTO<PackageDetailResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // Fitur 4: Create Package (GET form)
    @GetMapping("/create")
    public ResponseEntity<BaseResponseDTO<Map<String, String>>> getCreatePackageForm() {
        
        Map<String, String> formFields = new HashMap<>();
        formFields.put("packageName", "text input");
        formFields.put("userId", "text input");
        formFields.put("quota", "numeric input");
        formFields.put("startDate", "date input");
        formFields.put("endDate", "date input");
        
        BaseResponseDTO<Map<String, String>> response = new BaseResponseDTO<>();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Create package form structure");
        response.setTimestamp(new Date());
        response.setData(formFields);
        
        return ResponseEntity.ok(response);
    }
    
    // Fitur 4: Create Package (POST)
    @PostMapping("/create")
    public ResponseEntity<BaseResponseDTO<PackageResponseDTO>> createPackage(
            @RequestBody CreatePackageRequestDTO requestDTO) {
        
        try {
            Package newPackage = packageRestService.createPackage(requestDTO);
            
            PackageResponseDTO packageDTO = convertToPackageResponseDTO(newPackage);
            
            BaseResponseDTO<PackageResponseDTO> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Package created successfully");
            response.setTimestamp(new Date());
            response.setData(packageDTO);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
            
        } catch (RuntimeException e) {
            BaseResponseDTO<PackageResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            BaseResponseDTO<PackageResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    private PackageResponseDTO convertToPackageResponseDTO(Package pkg) {
        return PackageResponseDTO.builder()
                .id(pkg.getId())
                .packageName(pkg.getPackageName())
                .userId(pkg.getUserId())
                .quota(pkg.getQuota())
                .price(pkg.getPrice())
                .status(pkg.getStatus())
                .startDate(pkg.getStartDate())
                .endDate(pkg.getEndDate())
                .build();
    }
    
    private PackageDetailResponseDTO convertToPackageDetailResponseDTO(Package pkg, List<Plan> plans) {
        List<PlanResponseDTO> planDTOs = plans.stream()
                .map(this::convertToPlanResponseDTO)
                .collect(Collectors.toList());
        
        return PackageDetailResponseDTO.builder()
                .id(pkg.getId())
                .packageName(pkg.getPackageName())
                .userId(pkg.getUserId())
                .quota(pkg.getQuota())
                .price(pkg.getPrice())
                .status(pkg.getStatus())
                .startDate(pkg.getStartDate())
                .endDate(pkg.getEndDate())
                .plans(planDTOs)
                .build();
    }
    
    private PlanResponseDTO convertToPlanResponseDTO(Plan plan) {
        return PlanResponseDTO.builder()
                .id(plan.getId())
                .packageId(plan.getPackageId())
                .planName(plan.getPlanName())
                .price(plan.getPrice())
                .activityType(plan.getActivityType())
                .status(plan.getStatus())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .startLocation(plan.getStartLocation())
                .endLocation(plan.getEndLocation())
                .build();
    }
    
    // Fitur 5: Delete Package (Soft Delete)
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<BaseResponseDTO<String>> deletePackage(@PathVariable String id) {
        
        try {
            packageRestService.deletePackage(id);
            
            BaseResponseDTO<String> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Package deleted successfully");
            response.setTimestamp(new Date());
            response.setData("Package with id " + id + " has been deleted along with all associated plans");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            BaseResponseDTO<String> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            BaseResponseDTO<String> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // Fitur 6: Edit Package (GET form with plans)
    @GetMapping("/{id}/edit")
    public ResponseEntity<BaseResponseDTO<PackageDetailResponseDTO>> getEditPackageForm(@PathVariable String id) {
        
        try {
            Package pkg = packageRestService.getPackageById(id);
            List<Plan> plans = planRepository.findByPackageId(id);
            
            // Convert to DTO with plans for edit form
            PackageDetailResponseDTO packageDTO = convertToPackageDetailResponseDTO(pkg, plans);
            
            BaseResponseDTO<PackageDetailResponseDTO> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Edit package form with prefilled data and plans");
            response.setTimestamp(new Date());
            response.setData(packageDTO);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            BaseResponseDTO<PackageDetailResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            BaseResponseDTO<PackageDetailResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // Fitur 6: Edit Package (PUT)
    @PutMapping("/{id}/edit")
    public ResponseEntity<BaseResponseDTO<PackageResponseDTO>> updatePackage(
            @PathVariable String id,
            @RequestBody apap.ti._5.tour_package_2306240111_be.restdto.request.packagereq.UpdatePackageRequestDTO requestDTO) {
        
        try {
            Package updatedPackage = packageRestService.updatePackage(id, requestDTO);
            
            PackageResponseDTO packageDTO = convertToPackageResponseDTO(updatedPackage);
            
            BaseResponseDTO<PackageResponseDTO> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Package updated successfully");
            response.setTimestamp(new Date());
            response.setData(packageDTO);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            BaseResponseDTO<PackageResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            BaseResponseDTO<PackageResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Fitur 7: Process Package
    @PutMapping("/{id}/process")
    public ResponseEntity<BaseResponseDTO<PackageResponseDTO>> processPackage(@PathVariable String id) {
        
        try {
            Package processedPackage = packageRestService.processPackage(id);
            
            PackageResponseDTO packageDTO = convertToPackageResponseDTO(processedPackage);
            
            BaseResponseDTO<PackageResponseDTO> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Package processed successfully");
            response.setTimestamp(new Date());
            response.setData(packageDTO);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            BaseResponseDTO<PackageResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            BaseResponseDTO<PackageResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

package apap.ti._5.tour_package_2306240111_be.restcontroller;

import apap.ti._5.tour_package_2306240111_be.model.Activity;
import apap.ti._5.tour_package_2306240111_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306240111_be.model.Package;
import apap.ti._5.tour_package_2306240111_be.model.Plan;
import apap.ti._5.tour_package_2306240111_be.repository.ActivityRepository;
import apap.ti._5.tour_package_2306240111_be.repository.OrderedQuantityRepository;
import apap.ti._5.tour_package_2306240111_be.repository.PackageRepository;
import apap.ti._5.tour_package_2306240111_be.restdto.request.plan.CreatePlanRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.BaseResponseDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.orderedquantity.OrderedQuantityResponseDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.plan.PlanDetailResponseDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.plan.PlanResponseDTO;
import apap.ti._5.tour_package_2306240111_be.restservice.PlanRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class PlanRestController {
    
    @Autowired
    private PlanRestService planRestService;
    
    @Autowired
    private PackageRepository packageRepository;
    
    @Autowired
    private OrderedQuantityRepository orderedQuantityRepository;
    
    @Autowired
    private ActivityRepository activityRepository;
    
    @GetMapping("/api/packages/{packageId}/plans/create")
    public ResponseEntity<BaseResponseDTO<Map<String, Object>>> getCreatePlanForm(
            @PathVariable String packageId) {
        
        try {
            Package pkg = packageRepository.findById(packageId)
                    .orElseThrow(() -> new RuntimeException("Package not found"));
            
            if (!"Pending".equals(pkg.getStatus())) {
                throw new RuntimeException("Cannot create plan. Package status must be 'Pending'");
            }
            
            Map<String, Object> formData = new HashMap<>();
            formData.put("packageId", pkg.getId());
            formData.put("packageName", pkg.getPackageName());
            formData.put("packageStartDate", pkg.getStartDate());
            formData.put("packageEndDate", pkg.getEndDate());
            
            Map<String, String> formFields = new HashMap<>();
            formFields.put("planName", "text input");
            formFields.put("activityType", "dropdown: Flight, Accommodation, Vehicle Rental");
            formFields.put("startDate", "date input");
            formFields.put("endDate", "date input");
            formFields.put("startLocation", "dropdown from provinces/regencies API");
            formFields.put("endLocation", "dropdown from provinces/regencies API");
            
            formData.put("fields", formFields);
            
            BaseResponseDTO<Map<String, Object>> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Create plan form structure");
            response.setTimestamp(new Date());
            response.setData(formData);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            BaseResponseDTO<Map<String, Object>> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            BaseResponseDTO<Map<String, Object>> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PostMapping("/api/packages/{packageId}/plans/create")
    public ResponseEntity<BaseResponseDTO<PlanResponseDTO>> createPlan(
            @PathVariable String packageId,
            @RequestBody CreatePlanRequestDTO requestDTO) {
        
        try {
            Plan newPlan = planRestService.createPlan(packageId, requestDTO);
            
            PlanResponseDTO planDTO = convertToPlanResponseDTO(newPlan);
            
            BaseResponseDTO<PlanResponseDTO> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Plan created successfully");
            response.setTimestamp(new Date());
            response.setData(planDTO);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
            
        } catch (RuntimeException e) {
            BaseResponseDTO<PlanResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            BaseResponseDTO<PlanResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/api/plans/{planId}")
    public ResponseEntity<BaseResponseDTO<PlanDetailResponseDTO>> getPlanDetail(
            @PathVariable UUID planId) {
        
        try {
            Plan plan = planRestService.getPlanById(planId);
            
            // Get package info
            Package pkg = packageRepository.findById(plan.getPackageId())
                    .orElseThrow(() -> new RuntimeException("Package not found"));
            
            // Get ordered quantities
            List<OrderedQuantity> orderedQuantities = orderedQuantityRepository.findByPlanId(planId);
            
            // Convert to DTO
            PlanDetailResponseDTO planDetailDTO = convertToPlanDetailResponseDTO(plan, pkg, orderedQuantities);
            
            BaseResponseDTO<PlanDetailResponseDTO> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Success retrieve plan detail");
            response.setTimestamp(new Date());
            response.setData(planDetailDTO);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            BaseResponseDTO<PlanDetailResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            
        } catch (Exception e) {
            BaseResponseDTO<PlanDetailResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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
    
    private PlanDetailResponseDTO convertToPlanDetailResponseDTO(
            Plan plan, Package pkg, List<OrderedQuantity> orderedQuantities) {
        
        List<OrderedQuantityResponseDTO> orderedActivityDTOs = orderedQuantities.stream()
                .map(this::convertToOrderedQuantityResponseDTO)
                .collect(Collectors.toList());
        
        return PlanDetailResponseDTO.builder()
                .id(plan.getId())
                .packageId(plan.getPackageId())
                .packageName(pkg.getPackageName())
                .planName(plan.getPlanName())
                .activityType(plan.getActivityType())
                .status(plan.getStatus())
                .totalPrice(plan.getPrice())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .startLocation(plan.getStartLocation())
                .endLocation(plan.getEndLocation())
                .orderedActivities(orderedActivityDTOs)
                .build();
    }
    
    private OrderedQuantityResponseDTO convertToOrderedQuantityResponseDTO(OrderedQuantity oq) {
        Activity activity = activityRepository.findById(oq.getActivityId()).orElse(null);
        
        return OrderedQuantityResponseDTO.builder()
                .id(oq.getId())
                .activityId(oq.getActivityId())
                .activityName(activity != null ? activity.getActivityName() : "")
                .activityItem(activity != null ? activity.getActivityItem() : "")
                .capacity(activity != null ? activity.getCapacity() : 0)
                .price(oq.getPrice())
                .orderedQuota(oq.getOrderedQuota())
                .quota(oq.getQuota())
                .startDate(oq.getStartDate())
                .endDate(oq.getEndDate())
                .total(oq.getOrderedQuota() * oq.getPrice())
                .build();
    }
}

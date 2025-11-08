package apap.ti._5.tour_package_2306240111_be.restcontroller;

import apap.ti._5.tour_package_2306240111_be.model.Activity;
import apap.ti._5.tour_package_2306240111_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306240111_be.model.Package;
import apap.ti._5.tour_package_2306240111_be.model.Plan;
import apap.ti._5.tour_package_2306240111_be.repository.ActivityRepository;
import apap.ti._5.tour_package_2306240111_be.repository.OrderedQuantityRepository;
import apap.ti._5.tour_package_2306240111_be.repository.PackageRepository;
import apap.ti._5.tour_package_2306240111_be.repository.PlanRepository;
import apap.ti._5.tour_package_2306240111_be.restdto.request.plan.CreatePlanRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.request.plan.UpdatePlanRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.BaseResponseDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.orderedquantity.OrderedQuantityResponseDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.plan.PlanDetailResponseDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.plan.PlanResponseDTO;
import apap.ti._5.tour_package_2306240111_be.restservice.PlanRestService;
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
    
    private final PlanRestService planRestService;
    private final PackageRepository packageRepository;
    private final PlanRepository planRepository;
    private final OrderedQuantityRepository orderedQuantityRepository;
    private final ActivityRepository activityRepository;
    
    public PlanRestController(
            PlanRestService planRestService,
            PackageRepository packageRepository,
            PlanRepository planRepository,
            OrderedQuantityRepository orderedQuantityRepository,
            ActivityRepository activityRepository) {
        this.planRestService = planRestService;
        this.packageRepository = packageRepository;
        this.planRepository = planRepository;
        this.orderedQuantityRepository = orderedQuantityRepository;
        this.activityRepository = activityRepository;
    }
    
    @GetMapping("/api/activities")
    public ResponseEntity<BaseResponseDTO<List<Activity>>> getAllActivities() {
        try {
            List<Activity> activities = activityRepository.findAll();
            
            BaseResponseDTO<List<Activity>> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Success retrieve all activities");
            response.setTimestamp(new Date());
            response.setData(activities);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BaseResponseDTO<List<Activity>> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/api/plans/{planId}/available-activities")
    public ResponseEntity<BaseResponseDTO<List<Activity>>> getAvailableActivitiesForPlan(
            @PathVariable String planId) {
        try {
            Plan plan = planRepository.findById(UUID.fromString(planId))
                    .orElseThrow(() -> new RuntimeException("Plan not found"));
            
            System.out.println("=== PLAN DATES ===");
            System.out.println("Plan startDate: " + plan.getStartDate());
            System.out.println("Plan endDate: " + plan.getEndDate());
            System.out.println("Plan type: " + plan.getActivityType());
            
            List<Activity> allActivities = activityRepository.findAll();
            System.out.println("Total activities loaded: " + allActivities.size());
            
            List<Activity> eligibleActivities = allActivities.stream()
                    .filter(activity -> {
                        // must match Plan Activity Type
                        if (!activity.getActivityType().equals(plan.getActivityType())) {
                            return false;
                        }
                        
                        // startDate >= Plan startDate
                        if (activity.getStartDate().isBefore(plan.getStartDate())) {
                            System.out.println("Activity " + activity.getId() + " rejected: start date before plan");
                            System.out.println("  Activity: " + activity.getStartDate() + " < Plan: " + plan.getStartDate());
                            return false;
                        }
                        
                        // endDate <= Plan endDate
                        if (activity.getEndDate().isAfter(plan.getEndDate())) {
                            System.out.println("Activity " + activity.getId() + " rejected: end date after plan");
                            System.out.println("  Activity: " + activity.getEndDate() + " > Plan: " + plan.getEndDate());
                            return false;
                        }
                        
                        // startLocation = Plan startLocation
                        if (!activity.getStartLocation().equals(plan.getStartLocation())) {
                            return false;
                        }
                        
                        // Activity endLocation = Plan endLocation
                        if (!activity.getEndLocation().equals(plan.getEndLocation())) {
                            return false;
                        }
                        
                        System.out.println("Activity " + activity.getId() + " PASSED!");
                        return true;
                    })
                    .collect(Collectors.toList());
            
            System.out.println("=== FILTER RESULT: " + eligibleActivities.size() + " activities passed ===");
            
            BaseResponseDTO<List<Activity>> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Success retrieve available activities for plan");
            response.setTimestamp(new Date());
            response.setData(eligibleActivities);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BaseResponseDTO<List<Activity>> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
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
            
            Package pkg = packageRepository.findById(plan.getPackageId())
                    .orElseThrow(() -> new RuntimeException("Package not found"));
            
            List<OrderedQuantity> orderedQuantities = orderedQuantityRepository.findByPlanId(planId);
            
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

    // Fitur 10: Get Edit Plan Form
    @GetMapping("/api/plans/{id}/edit")
    public ResponseEntity<BaseResponseDTO<PlanDetailResponseDTO>> getEditPlanForm(@PathVariable String id) {
        try {
            UUID planId = UUID.fromString(id);
            Plan plan = planRestService.getPlanById(planId);
            
            Package pkg = packageRepository.findById(plan.getPackageId())
                    .orElseThrow(() -> new RuntimeException("Package not found with id: " + plan.getPackageId()));
            
            List<OrderedQuantity> orderedQuantities = orderedQuantityRepository.findByPlanId(planId);
            
            PlanDetailResponseDTO planDetail = convertToPlanDetailResponseDTO(plan, pkg, orderedQuantities);
            
            BaseResponseDTO<PlanDetailResponseDTO> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Edit plan form data retrieved successfully");
            response.setTimestamp(new Date());
            response.setData(planDetail);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            BaseResponseDTO<PlanDetailResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            BaseResponseDTO<PlanDetailResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Fitur 10: Update Plan
    @PutMapping("/api/plans/{id}/edit")
    public ResponseEntity<BaseResponseDTO<PlanResponseDTO>> updatePlan(
            @PathVariable String id,
            @RequestBody UpdatePlanRequestDTO requestDTO) {
        try {
            UUID planId = UUID.fromString(id);
            Plan updatedPlan = planRestService.updatePlan(planId, requestDTO);
            
            PlanResponseDTO planResponse = convertToPlanResponseDTO(updatedPlan);
            
            BaseResponseDTO<PlanResponseDTO> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Plan updated successfully");
            response.setTimestamp(new Date());
            response.setData(planResponse);
            
            return ResponseEntity.ok(response);
            
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

    // Fitur 14: Delete Plan (Soft Delete)
    @DeleteMapping("/api/plans/{id}")
    public ResponseEntity<BaseResponseDTO<String>> deletePlan(@PathVariable String id) {
        try {
            UUID planId = UUID.fromString(id);
            planRestService.deletePlan(planId);
            
            BaseResponseDTO<String> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Plan deleted successfully");
            response.setTimestamp(new Date());
            response.setData("Plan with id " + id + " has been deleted");
            
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

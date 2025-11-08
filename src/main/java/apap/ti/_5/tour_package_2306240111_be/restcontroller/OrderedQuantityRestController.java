package apap.ti._5.tour_package_2306240111_be.restcontroller;

import apap.ti._5.tour_package_2306240111_be.model.Activity;
import apap.ti._5.tour_package_2306240111_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306240111_be.restdto.request.orderedquantity.CreateOrderedQuantityRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.request.orderedquantity.UpdateOrderedQuantityRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.BaseResponseDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.orderedquantity.OrderedQuantityResponseDTO;
import apap.ti._5.tour_package_2306240111_be.restservice.OrderedQuantityRestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ordered-activities")
public class OrderedQuantityRestController {
    
    private final OrderedQuantityRestService orderedQuantityRestService;
    
    public OrderedQuantityRestController(OrderedQuantityRestService orderedQuantityRestService) {
        this.orderedQuantityRestService = orderedQuantityRestService;
    }
    

    //Get all activities 
    @GetMapping("/activities")
    public ResponseEntity<BaseResponseDTO<List<Activity>>> getAllActivities() {
        
        try {
            List<Activity> activities = orderedQuantityRestService.getAllActivities();
            
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
    

     //Fitur 11: Add Ordered Quantity to Plan
    @PostMapping("/create")
    public ResponseEntity<BaseResponseDTO<OrderedQuantityResponseDTO>> createOrderedQuantity(
            @RequestBody CreateOrderedQuantityRequestDTO requestDTO) {
        
        try {
            OrderedQuantity newOrderedQuantity = orderedQuantityRestService.createOrderedQuantity(requestDTO);
            
            OrderedQuantityResponseDTO oqDTO = OrderedQuantityResponseDTO.builder()
                    .id(newOrderedQuantity.getId())
                    .activityId(newOrderedQuantity.getActivityId())
                    .activityName("") 
                    .activityItem("") 
                    .capacity(newOrderedQuantity.getQuota())
                    .price(newOrderedQuantity.getPrice())
                    .orderedQuota(newOrderedQuantity.getOrderedQuota())
                    .quota(newOrderedQuantity.getQuota())
                    .startDate(newOrderedQuantity.getStartDate())
                    .endDate(newOrderedQuantity.getEndDate())
                    .total(newOrderedQuantity.getPrice() * newOrderedQuantity.getOrderedQuota())
                    .build();
            
            BaseResponseDTO<OrderedQuantityResponseDTO> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Activity added to plan successfully");
            response.setTimestamp(new Date());
            response.setData(oqDTO);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
            
        } catch (RuntimeException e) {
            BaseResponseDTO<OrderedQuantityResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            BaseResponseDTO<OrderedQuantityResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    

    @GetMapping("/{id}/edit")
    public ResponseEntity<BaseResponseDTO<OrderedQuantityResponseDTO>> getOrderedQuantityForEdit(
            @PathVariable UUID id) {
        
        try {
            OrderedQuantity orderedQuantity = orderedQuantityRestService.getOrderedQuantityById(id);
            
            OrderedQuantityResponseDTO oqDTO = OrderedQuantityResponseDTO.builder()
                    .id(orderedQuantity.getId())
                    .activityId(orderedQuantity.getActivityId())
                    .activityName("") 
                    .activityItem("") 
                    .capacity(orderedQuantity.getQuota())
                    .price(orderedQuantity.getPrice())
                    .orderedQuota(orderedQuantity.getOrderedQuota())
                    .quota(orderedQuantity.getQuota())
                    .startDate(orderedQuantity.getStartDate())
                    .endDate(orderedQuantity.getEndDate())
                    .total(orderedQuantity.getPrice() * orderedQuantity.getOrderedQuota())
                    .build();
            
            BaseResponseDTO<OrderedQuantityResponseDTO> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Success retrieve ordered quantity for edit");
            response.setTimestamp(new Date());
            response.setData(oqDTO);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            BaseResponseDTO<OrderedQuantityResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            
        } catch (Exception e) {
            BaseResponseDTO<OrderedQuantityResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    
    //Update ordered quantity
    @PutMapping("/{id}/edit")
    public ResponseEntity<BaseResponseDTO<OrderedQuantityResponseDTO>> updateOrderedQuantity(
            @PathVariable UUID id,
            @RequestBody UpdateOrderedQuantityRequestDTO requestDTO) {
        
        try {
            OrderedQuantity updated = orderedQuantityRestService.updateOrderedQuantity(id, requestDTO);
            
            OrderedQuantityResponseDTO oqDTO = OrderedQuantityResponseDTO.builder()
                    .id(updated.getId())
                    .activityId(updated.getActivityId())
                    .activityName("") 
                    .activityItem("") 
                    .capacity(updated.getQuota())
                    .price(updated.getPrice())
                    .orderedQuota(updated.getOrderedQuota())
                    .quota(updated.getQuota())
                    .startDate(updated.getStartDate())
                    .endDate(updated.getEndDate())
                    .total(updated.getPrice() * updated.getOrderedQuota())
                    .build();
            
            BaseResponseDTO<OrderedQuantityResponseDTO> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Ordered quantity updated successfully");
            response.setTimestamp(new Date());
            response.setData(oqDTO);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            BaseResponseDTO<OrderedQuantityResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            BaseResponseDTO<OrderedQuantityResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/eligible")
    public ResponseEntity<BaseResponseDTO<List<Activity>>> getEligibleActivities(
            @RequestParam UUID planId) {
        
        try {
            List<Activity> eligibleActivities = orderedQuantityRestService.getEligibleActivities(planId);
            
            BaseResponseDTO<List<Activity>> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Success retrieve eligible activities for plan");
            response.setTimestamp(new Date());
            response.setData(eligibleActivities);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            BaseResponseDTO<List<Activity>> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            
        } catch (Exception e) {
            BaseResponseDTO<List<Activity>> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // Fitur 13: Delete Ordered Quantity (Soft Delete)
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<BaseResponseDTO<String>> deleteOrderedQuantity(
            @PathVariable UUID id) {
        
        try {
            orderedQuantityRestService.deleteOrderedQuantity(id);
            
            BaseResponseDTO<String> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Ordered quantity deleted successfully");
            response.setTimestamp(new Date());
            response.setData("Successfully deleted");
            
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
}

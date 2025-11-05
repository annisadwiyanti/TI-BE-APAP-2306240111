package apap.ti._5.tour_package_2306240111_be.restdto.response.plan;

import com.fasterxml.jackson.annotation.JsonFormat;

import apap.ti._5.tour_package_2306240111_be.restdto.response.orderedquantity.OrderedQuantityResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDetailResponseDTO {
    private UUID id;
    private String packageId;
    private String planName;
    private String activityType;
    private String status;
    private Long totalPrice;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", timezone = "Asia/Jakarta")
    private LocalDateTime startDate;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", timezone = "Asia/Jakarta")
    private LocalDateTime endDate;
    
    private String startLocation;
    private String endLocation;
    
    private String packageName; 
    
    private List<OrderedQuantityResponseDTO> orderedActivities;
}

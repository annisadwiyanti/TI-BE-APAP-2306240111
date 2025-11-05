package apap.ti._5.tour_package_2306240111_be.restdto.response.orderedquantity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderedQuantityResponseDTO {
    private UUID id;
    private String activityId;
    private String activityName;
    private String activityItem;
    private int capacity;
    private Long price;
    private int orderedQuota;
    private int quota;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm, d MMMM yyyy", timezone = "Asia/Jakarta")
    private LocalDateTime startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm, d MMMM yyyy", timezone = "Asia/Jakarta")
    private LocalDateTime endDate;
    
    private Long total; // orderedQuota * price
}

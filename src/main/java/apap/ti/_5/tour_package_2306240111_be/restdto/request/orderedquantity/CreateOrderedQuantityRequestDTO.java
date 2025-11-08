package apap.ti._5.tour_package_2306240111_be.restdto.request.orderedquantity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderedQuantityRequestDTO {
    
    private UUID planId;
    private String activityId;
    private int orderedQuota;
}

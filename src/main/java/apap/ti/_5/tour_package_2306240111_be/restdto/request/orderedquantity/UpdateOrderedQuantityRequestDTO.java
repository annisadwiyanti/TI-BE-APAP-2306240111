package apap.ti._5.tour_package_2306240111_be.restdto.request.orderedquantity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderedQuantityRequestDTO {
    private int orderedQuota;
}

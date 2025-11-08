package apap.ti._5.tour_package_2306240111_be.restdto.response.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RevenueStatisticDTO {
    private int year;
    private int month;
    private List<ActivityTypeRevenueDTO> revenueByActivityType;
}
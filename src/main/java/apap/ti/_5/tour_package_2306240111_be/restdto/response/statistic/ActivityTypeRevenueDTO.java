package apap.ti._5.tour_package_2306240111_be.restdto.response.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityTypeRevenueDTO {
    private String activityType;
    private Long totalRevenue;
}
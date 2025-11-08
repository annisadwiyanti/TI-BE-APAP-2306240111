package apap.ti._5.tour_package_2306240111_be.restservice;

import apap.ti._5.tour_package_2306240111_be.model.Activity;
import apap.ti._5.tour_package_2306240111_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306240111_be.repository.ActivityRepository;
import apap.ti._5.tour_package_2306240111_be.repository.OrderedQuantityRepository;
import apap.ti._5.tour_package_2306240111_be.restdto.response.statistic.ActivityTypeRevenueDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.statistic.RevenueStatisticDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class StatisticRestServiceImpl implements StatisticRestService {
    
    private final OrderedQuantityRepository orderedQuantityRepository;
    private final ActivityRepository activityRepository;
    
    public StatisticRestServiceImpl(
            OrderedQuantityRepository orderedQuantityRepository,
            ActivityRepository activityRepository) {
        this.orderedQuantityRepository = orderedQuantityRepository;
        this.activityRepository = activityRepository;
    }
    
    @Override
    public RevenueStatisticDTO getRevenueStatistics(int year, int month) {
        List<OrderedQuantity> filteredOrderedQuantities = orderedQuantityRepository.findByYearAndMonth(year, month);
        

        Map<String, Long> revenueByActivityType = new HashMap<>();
        
        for (OrderedQuantity oq : filteredOrderedQuantities) {

            Activity activity = activityRepository.findById(oq.getActivityId())
                    .orElse(null);
            
            if (activity != null) {
                String activityType = activity.getActivityType();
                Long revenue = oq.getPrice() * oq.getOrderedQuota();
                
                revenueByActivityType.merge(activityType, revenue, Long::sum);
            }
        }
        
        List<ActivityTypeRevenueDTO> revenueList = revenueByActivityType.entrySet().stream()
                .map(entry -> ActivityTypeRevenueDTO.builder()
                        .activityType(entry.getKey())
                        .totalRevenue(entry.getValue())
                        .build())
                .sorted(Comparator.comparingLong(ActivityTypeRevenueDTO::getTotalRevenue).reversed())
                .collect(Collectors.toList());
        
        return RevenueStatisticDTO.builder()
                .year(year)
                .month(month)
                .revenueByActivityType(revenueList)
                .build();
    }
}
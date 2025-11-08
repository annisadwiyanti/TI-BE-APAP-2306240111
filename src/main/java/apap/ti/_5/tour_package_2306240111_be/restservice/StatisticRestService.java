package apap.ti._5.tour_package_2306240111_be.restservice;

import apap.ti._5.tour_package_2306240111_be.restdto.response.statistic.RevenueStatisticDTO;

public interface StatisticRestService {
    
    RevenueStatisticDTO getRevenueStatistics(int year, int month);
}

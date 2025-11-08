package apap.ti._5.tour_package_2306240111_be.restcontroller;

import apap.ti._5.tour_package_2306240111_be.restdto.response.BaseResponseDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.statistic.RevenueStatisticDTO;
import apap.ti._5.tour_package_2306240111_be.restservice.StatisticRestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/statistics")
public class StatisticRestController {
    
    private final StatisticRestService statisticRestService;
    
    public StatisticRestController(StatisticRestService statisticRestService) {
        this.statisticRestService = statisticRestService;
    }
    
    // Fitur 15: Get Revenue Statistics
    @GetMapping("")
    public ResponseEntity<BaseResponseDTO<RevenueStatisticDTO>> getRevenueStatistics(
            @RequestParam Optional<Integer> year,
            @RequestParam(required = false, defaultValue = "0") int month) {
        
        try {
            if (!year.isPresent()) {
                BaseResponseDTO<RevenueStatisticDTO> errorResponse = new BaseResponseDTO<>();
                errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                errorResponse.setMessage("Error: Required parameter 'year' is missing");
                errorResponse.setTimestamp(new Date());
                errorResponse.setData(null);
                
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            RevenueStatisticDTO statistic = statisticRestService.getRevenueStatistics(year.get(), month);
            
            BaseResponseDTO<RevenueStatisticDTO> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Revenue statistics retrieved successfully");
            response.setTimestamp(new Date());
            response.setData(statistic);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            BaseResponseDTO<RevenueStatisticDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

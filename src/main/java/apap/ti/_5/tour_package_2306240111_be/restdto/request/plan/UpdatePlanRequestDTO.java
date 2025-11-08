package apap.ti._5.tour_package_2306240111_be.restdto.request.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePlanRequestDTO {
    
    private String planName;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", locale = "id_ID")
    private LocalDateTime startDate;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", locale = "id_ID")
    private LocalDateTime endDate;
    
    private String startLocation;
    private String endLocation;
}

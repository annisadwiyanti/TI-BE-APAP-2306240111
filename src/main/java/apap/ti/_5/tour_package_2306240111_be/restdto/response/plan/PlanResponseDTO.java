package apap.ti._5.tour_package_2306240111_be.restdto.response.plan;

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
public class PlanResponseDTO {
    private UUID id;
    private String packageId;
    private String planName;
    private Long price;
    private String activityType;
    private String status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm", timezone = "Asia/Jakarta")
    private LocalDateTime startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm", timezone = "Asia/Jakarta")
    private LocalDateTime endDate;
    
    private String startLocation;
    private String endLocation;
}

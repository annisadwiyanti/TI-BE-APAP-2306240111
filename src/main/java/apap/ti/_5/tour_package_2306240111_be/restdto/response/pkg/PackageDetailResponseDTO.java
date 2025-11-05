package apap.ti._5.tour_package_2306240111_be.restdto.response.pkg;

import apap.ti._5.tour_package_2306240111_be.restdto.response.plan.PlanResponseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageDetailResponseDTO {
    private String id;
    private String packageName;
    private String userId;
    private int quota;
    private Long price;
    private String status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "d MMMM yyyy", timezone = "Asia/Jakarta")
    private LocalDateTime startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "d MMMM yyyy", timezone = "Asia/Jakarta")
    private LocalDateTime endDate;
    
    private List<PlanResponseDTO> plans;
}

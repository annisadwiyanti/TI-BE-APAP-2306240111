package apap.ti._5.tour_package_2306240111_be.restdto.request.packagereq;

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
public class CreatePackageRequestDTO {
    
    private String packageName;
    private String userId;
    private Integer quota;
    
    // Accept full ISO-like datetime including seconds (e.g. 2025-10-07T08:00:00)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Jakarta")
    private LocalDateTime startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Jakarta")
    private LocalDateTime endDate;
}

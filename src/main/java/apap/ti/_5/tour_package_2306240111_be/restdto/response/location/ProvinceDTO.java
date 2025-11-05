package apap.ti._5.tour_package_2306240111_be.restdto.response.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceDTO {
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("name")
    private String name;
}

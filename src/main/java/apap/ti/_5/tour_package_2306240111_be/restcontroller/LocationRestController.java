package apap.ti._5.tour_package_2306240111_be.restcontroller;

import apap.ti._5.tour_package_2306240111_be.restdto.response.BaseResponseDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.location.ProvinceDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.location.RegencyDTO;
import apap.ti._5.tour_package_2306240111_be.restservice.LocationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/location")
public class LocationRestController {
    
    @Autowired
    private LocationRestService locationRestService;
    
    @GetMapping("/provinces")
    public ResponseEntity<BaseResponseDTO<List<ProvinceDTO>>> getAllProvinces() {
        try {
            List<ProvinceDTO> provinces = locationRestService.getAllProvinces();
            
            BaseResponseDTO<List<ProvinceDTO>> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Success retrieve all provinces");
            response.setTimestamp(new Date());
            response.setData(provinces);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            BaseResponseDTO<List<ProvinceDTO>> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/regencies/{provinceCode}")
    public ResponseEntity<BaseResponseDTO<List<RegencyDTO>>> getRegenciesByProvinceCode(
            @PathVariable String provinceCode) {
        try {
            List<RegencyDTO> regencies = locationRestService.getRegenciesByProvinceCode(provinceCode);
            
            BaseResponseDTO<List<RegencyDTO>> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Success retrieve regencies for province: " + provinceCode);
            response.setTimestamp(new Date());
            response.setData(regencies);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            BaseResponseDTO<List<RegencyDTO>> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

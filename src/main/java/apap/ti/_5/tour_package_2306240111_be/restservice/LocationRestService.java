package apap.ti._5.tour_package_2306240111_be.restservice;

import apap.ti._5.tour_package_2306240111_be.restdto.response.location.ProvinceDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.location.RegencyDTO;

import java.util.List;

public interface LocationRestService {
    
    List<ProvinceDTO> getAllProvinces();
    
    List<RegencyDTO> getRegenciesByProvinceCode(String provinceCode);
}

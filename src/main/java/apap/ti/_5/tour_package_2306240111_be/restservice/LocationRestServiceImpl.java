package apap.ti._5.tour_package_2306240111_be.restservice;

import apap.ti._5.tour_package_2306240111_be.restdto.response.location.ProvinceDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.location.RegencyDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationRestServiceImpl implements LocationRestService {
    
    private static final String PROVINCES_URL = "https://wilayah.id/api/provinces.json";
    private static final String REGENCIES_URL = "https://wilayah.id/api/regencies/%s.json";
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public LocationRestServiceImpl() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public List<ProvinceDTO> getAllProvinces() {
        try {
            String response = restTemplate.getForObject(PROVINCES_URL, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode dataNode = root.get("data");
            
            if (dataNode != null && dataNode.isArray()) {
                return objectMapper.convertValue(
                    dataNode, 
                    new TypeReference<List<ProvinceDTO>>() {}
                );
            }
            
            return new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch provinces: " + e.getMessage());
        }
    }
    
    @Override
    public List<RegencyDTO> getRegenciesByProvinceCode(String provinceCode) {
        try {
            String url = String.format(REGENCIES_URL, provinceCode);
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode dataNode = root.get("data");
            
            if (dataNode != null && dataNode.isArray()) {
                return objectMapper.convertValue(
                    dataNode, 
                    new TypeReference<List<RegencyDTO>>() {}
                );
            }
            
            return new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch regencies for province " + provinceCode + ": " + e.getMessage());
        }
    }
}

package apap.ti._5.tour_package_2306240111_be.restservice;

import apap.ti._5.tour_package_2306240111_be.restdto.response.location.ProvinceDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.location.RegencyDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class LocationRestServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private LocationRestServiceImpl locationRestService;

    private static final String PROVINCES_URL = "https://wilayah.id/api/provinces.json";
    private static final String REGENCIES_URL = "https://wilayah.id/api/regencies/%s.json";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        locationRestService = new LocationRestServiceImpl();
        // Use reflection to inject mocks
        try {
            java.lang.reflect.Field restTemplateField = LocationRestServiceImpl.class.getDeclaredField("restTemplate");
            restTemplateField.setAccessible(true);
            restTemplateField.set(locationRestService, restTemplate);

            java.lang.reflect.Field objectMapperField = LocationRestServiceImpl.class.getDeclaredField("objectMapper");
            objectMapperField.setAccessible(true);
            objectMapperField.set(locationRestService, objectMapper);
        } catch (Exception e) {
            fail("Failed to inject mocks: " + e.getMessage());
        }
    }

    @Test
    void testGetAllProvinces_Success() throws Exception {
        // Given
        String jsonResponse = "{\"data\":[" +
                "{\"code\":\"11\",\"name\":\"ACEH\"}," +
                "{\"code\":\"12\",\"name\":\"SUMATERA UTARA\"}," +
                "{\"code\":\"31\",\"name\":\"DKI JAKARTA\"}" +
                "]}";

        when(restTemplate.getForObject(PROVINCES_URL, String.class)).thenReturn(jsonResponse);
        when(objectMapper.readTree(jsonResponse)).thenReturn(new ObjectMapper().readTree(jsonResponse));
        
        List<ProvinceDTO> expectedProvinces = List.of(
                new ProvinceDTO("11", "ACEH"),
                new ProvinceDTO("12", "SUMATERA UTARA"),
                new ProvinceDTO("31", "DKI JAKARTA")
        );
        
        when(objectMapper.convertValue(any(), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .thenReturn(expectedProvinces);

        // When
        List<ProvinceDTO> result = locationRestService.getAllProvinces();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("11", result.get(0).getCode());
        assertEquals("ACEH", result.get(0).getName());
        assertEquals("31", result.get(2).getCode());
        assertEquals("DKI JAKARTA", result.get(2).getName());

        verify(restTemplate, times(1)).getForObject(PROVINCES_URL, String.class);
    }

    @Test
    void testGetAllProvinces_EmptyData() throws Exception {
        // Given
        String jsonResponse = "{\"data\":[]}";

        when(restTemplate.getForObject(PROVINCES_URL, String.class)).thenReturn(jsonResponse);
        when(objectMapper.readTree(jsonResponse)).thenReturn(new ObjectMapper().readTree(jsonResponse));
        when(objectMapper.convertValue(any(), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .thenReturn(List.of());

        // When
        List<ProvinceDTO> result = locationRestService.getAllProvinces();

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());

        verify(restTemplate, times(1)).getForObject(PROVINCES_URL, String.class);
    }

    @Test
    void testGetAllProvinces_NullData() throws Exception {
        // Given
        String jsonResponse = "{}";

        when(restTemplate.getForObject(PROVINCES_URL, String.class)).thenReturn(jsonResponse);
        when(objectMapper.readTree(jsonResponse)).thenReturn(new ObjectMapper().readTree(jsonResponse));

        // When
        List<ProvinceDTO> result = locationRestService.getAllProvinces();

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());

        verify(restTemplate, times(1)).getForObject(PROVINCES_URL, String.class);
    }

    @Test
    void testGetAllProvinces_RestTemplateThrowsException() {
        // Given
        when(restTemplate.getForObject(PROVINCES_URL, String.class))
                .thenThrow(new RestClientException("Connection timeout"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            locationRestService.getAllProvinces();
        });

        assertTrue(exception.getMessage().contains("Failed to fetch provinces"));
        verify(restTemplate, times(1)).getForObject(PROVINCES_URL, String.class);
    }

    @Test
    void testGetAllProvinces_JsonParsingException() throws Exception {
        // Given
        String invalidJson = "invalid json";

        when(restTemplate.getForObject(PROVINCES_URL, String.class)).thenReturn(invalidJson);
        when(objectMapper.readTree(invalidJson)).thenThrow(new com.fasterxml.jackson.core.JsonProcessingException("Invalid JSON") {});

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            locationRestService.getAllProvinces();
        });

        assertTrue(exception.getMessage().contains("Failed to fetch provinces"));
        verify(restTemplate, times(1)).getForObject(PROVINCES_URL, String.class);
    }

    @Test
    void testGetRegenciesByProvinceCode_Success() throws Exception {
        // Given
        String provinceCode = "31";
        String url = String.format(REGENCIES_URL, provinceCode);
        String jsonResponse = "{\"data\":[" +
                "{\"code\":\"3171\",\"name\":\"JAKARTA SELATAN\",\"province_code\":\"31\"}," +
                "{\"code\":\"3172\",\"name\":\"JAKARTA TIMUR\",\"province_code\":\"31\"}," +
                "{\"code\":\"3173\",\"name\":\"JAKARTA PUSAT\",\"province_code\":\"31\"}" +
                "]}";

        when(restTemplate.getForObject(url, String.class)).thenReturn(jsonResponse);
        when(objectMapper.readTree(jsonResponse)).thenReturn(new ObjectMapper().readTree(jsonResponse));
        
        List<RegencyDTO> expectedRegencies = List.of(
                new RegencyDTO("3171", "JAKARTA SELATAN", "31"),
                new RegencyDTO("3172", "JAKARTA TIMUR", "31"),
                new RegencyDTO("3173", "JAKARTA PUSAT", "31")
        );
        
        when(objectMapper.convertValue(any(), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .thenReturn(expectedRegencies);

        // When
        List<RegencyDTO> result = locationRestService.getRegenciesByProvinceCode(provinceCode);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("3171", result.get(0).getCode());
        assertEquals("JAKARTA SELATAN", result.get(0).getName());
        assertEquals("31", result.get(0).getProvinceCode());
        assertEquals("3173", result.get(2).getCode());
        assertEquals("JAKARTA PUSAT", result.get(2).getName());

        verify(restTemplate, times(1)).getForObject(url, String.class);
    }

    @Test
    void testGetRegenciesByProvinceCode_EmptyData() throws Exception {
        // Given
        String provinceCode = "99";
        String url = String.format(REGENCIES_URL, provinceCode);
        String jsonResponse = "{\"data\":[]}";

        when(restTemplate.getForObject(url, String.class)).thenReturn(jsonResponse);
        when(objectMapper.readTree(jsonResponse)).thenReturn(new ObjectMapper().readTree(jsonResponse));
        when(objectMapper.convertValue(any(), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .thenReturn(List.of());

        // When
        List<RegencyDTO> result = locationRestService.getRegenciesByProvinceCode(provinceCode);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());

        verify(restTemplate, times(1)).getForObject(url, String.class);
    }

    @Test
    void testGetRegenciesByProvinceCode_NullData() throws Exception {
        // Given
        String provinceCode = "31";
        String url = String.format(REGENCIES_URL, provinceCode);
        String jsonResponse = "{}";

        when(restTemplate.getForObject(url, String.class)).thenReturn(jsonResponse);
        when(objectMapper.readTree(jsonResponse)).thenReturn(new ObjectMapper().readTree(jsonResponse));

        // When
        List<RegencyDTO> result = locationRestService.getRegenciesByProvinceCode(provinceCode);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());

        verify(restTemplate, times(1)).getForObject(url, String.class);
    }

    @Test
    void testGetRegenciesByProvinceCode_RestTemplateThrowsException() {
        // Given
        String provinceCode = "31";
        String url = String.format(REGENCIES_URL, provinceCode);

        when(restTemplate.getForObject(url, String.class))
                .thenThrow(new RestClientException("Connection timeout"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            locationRestService.getRegenciesByProvinceCode(provinceCode);
        });

        assertTrue(exception.getMessage().contains("Failed to fetch regencies for province 31"));
        verify(restTemplate, times(1)).getForObject(url, String.class);
    }

    @Test
    void testGetRegenciesByProvinceCode_JsonParsingException() throws Exception {
        // Given
        String provinceCode = "31";
        String url = String.format(REGENCIES_URL, provinceCode);
        String invalidJson = "invalid json";

        when(restTemplate.getForObject(url, String.class)).thenReturn(invalidJson);
        when(objectMapper.readTree(invalidJson)).thenThrow(new com.fasterxml.jackson.core.JsonProcessingException("Invalid JSON") {});

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            locationRestService.getRegenciesByProvinceCode(provinceCode);
        });

        assertTrue(exception.getMessage().contains("Failed to fetch regencies for province 31"));
        verify(restTemplate, times(1)).getForObject(url, String.class);
    }

    @Test
    void testGetRegenciesByProvinceCode_MultipleProvinceCodes() throws Exception {
        // Test with different province codes
        String[] provinceCodes = {"11", "12", "32", "51"};

        for (String code : provinceCodes) {
            String url = String.format(REGENCIES_URL, code);
            String jsonResponse = "{\"data\":[" +
                    "{\"code\":\"" + code + "01\",\"name\":\"Regency 1\",\"province_code\":\"" + code + "\"}," +
                    "{\"code\":\"" + code + "02\",\"name\":\"Regency 2\",\"province_code\":\"" + code + "\"}" +
                    "]}";

            when(restTemplate.getForObject(url, String.class)).thenReturn(jsonResponse);
            when(objectMapper.readTree(jsonResponse)).thenReturn(new ObjectMapper().readTree(jsonResponse));

            List<RegencyDTO> expectedRegencies = List.of(
                    new RegencyDTO(code + "01", "Regency 1", code),
                    new RegencyDTO(code + "02", "Regency 2", code)
            );

            when(objectMapper.convertValue(any(), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                    .thenReturn(expectedRegencies);

            // When
            List<RegencyDTO> result = locationRestService.getRegenciesByProvinceCode(code);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(code, result.get(0).getProvinceCode());
            assertEquals(code, result.get(1).getProvinceCode());
        }
    }
}

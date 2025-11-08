package apap.ti._5.tour_package_2306240111_be.restcontroller;

import apap.ti._5.tour_package_2306240111_be.restdto.response.location.ProvinceDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.location.RegencyDTO;
import apap.ti._5.tour_package_2306240111_be.restservice.LocationRestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class LocationRestControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private LocationRestService locationRestService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private List<ProvinceDTO> testProvinces;
    private List<RegencyDTO> testRegencies;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Setup test data for provinces
        testProvinces = Arrays.asList(
                new ProvinceDTO("11", "ACEH"),
                new ProvinceDTO("12", "SUMATERA UTARA"),
                new ProvinceDTO("31", "DKI JAKARTA"),
                new ProvinceDTO("32", "JAWA BARAT"),
                new ProvinceDTO("51", "BALI")
        );

        // Setup test data for regencies
        testRegencies = Arrays.asList(
                new RegencyDTO("3101", "KEPULAUAN SERIBU", "31"),
                new RegencyDTO("3171", "JAKARTA SELATAN", "31"),
                new RegencyDTO("3172", "JAKARTA TIMUR", "31"),
                new RegencyDTO("3173", "JAKARTA PUSAT", "31"),
                new RegencyDTO("3174", "JAKARTA BARAT", "31"),
                new RegencyDTO("3175", "JAKARTA UTARA", "31")
        );
    }

    @Test
    void testGetAllProvinces_Success() throws Exception {
        // Given
        when(locationRestService.getAllProvinces()).thenReturn(testProvinces);

        // When & Then
        mockMvc.perform(get("/api/location/provinces")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Success retrieve all provinces")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.data[0].code", is("11")))
                .andExpect(jsonPath("$.data[0].name", is("ACEH")))
                .andExpect(jsonPath("$.data[1].code", is("12")))
                .andExpect(jsonPath("$.data[1].name", is("SUMATERA UTARA")))
                .andExpect(jsonPath("$.data[2].code", is("31")))
                .andExpect(jsonPath("$.data[2].name", is("DKI JAKARTA")))
                .andExpect(jsonPath("$.data[3].code", is("32")))
                .andExpect(jsonPath("$.data[3].name", is("JAWA BARAT")))
                .andExpect(jsonPath("$.data[4].code", is("51")))
                .andExpect(jsonPath("$.data[4].name", is("BALI")));

        verify(locationRestService, times(1)).getAllProvinces();
    }

    @Test
    void testGetAllProvinces_EmptyList() throws Exception {
        // Given
        when(locationRestService.getAllProvinces()).thenReturn(new ArrayList<>());

        // When & Then
        mockMvc.perform(get("/api/location/provinces")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Success retrieve all provinces")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(locationRestService, times(1)).getAllProvinces();
    }

    @Test
    void testGetAllProvinces_ServiceThrowsException() throws Exception {
        // Given
        when(locationRestService.getAllProvinces())
                .thenThrow(new RuntimeException("Failed to fetch provinces from external API"));

        // When & Then
        mockMvc.perform(get("/api/location/provinces")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.message", containsString("Error: Failed to fetch provinces from external API")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.data", nullValue()));

        verify(locationRestService, times(1)).getAllProvinces();
    }

    @Test
    void testGetRegenciesByProvinceCode_EmptyList() throws Exception {
        // Given
        String provinceCode = "99";
        when(locationRestService.getRegenciesByProvinceCode(provinceCode)).thenReturn(new ArrayList<>());

        // When & Then
        mockMvc.perform(get("/api/location/regencies/{provinceCode}", provinceCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Success retrieve regencies for province: 99")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(locationRestService, times(1)).getRegenciesByProvinceCode(provinceCode);
    }

    @Test
    void testGetRegenciesByProvinceCode_ServiceThrowsException() throws Exception {
        // Given
        String provinceCode = "31";
        when(locationRestService.getRegenciesByProvinceCode(provinceCode))
                .thenThrow(new RuntimeException("Failed to fetch regencies for province 31"));

        // When & Then
        mockMvc.perform(get("/api/location/regencies/{provinceCode}", provinceCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.message", containsString("Error: Failed to fetch regencies for province 31")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.data", nullValue()));

        verify(locationRestService, times(1)).getRegenciesByProvinceCode(provinceCode);
    }

    @Test
    void testGetRegenciesByProvinceCode_DifferentProvinceCodes() throws Exception {
        // Test with different province codes
        String[] provinceCodes = {"11", "12", "32", "51"};

        for (String code : provinceCodes) {
            List<RegencyDTO> regencies = Arrays.asList(
                    new RegencyDTO(code + "01", "Regency 1", code),
                    new RegencyDTO(code + "02", "Regency 2", code)
            );
            when(locationRestService.getRegenciesByProvinceCode(code)).thenReturn(regencies);

            mockMvc.perform(get("/api/location/regencies/{provinceCode}", code)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is(200)))
                    .andExpect(jsonPath("$.message", is("Success retrieve regencies for province: " + code)))
                    .andExpect(jsonPath("$.data", hasSize(2)));

            verify(locationRestService, times(1)).getRegenciesByProvinceCode(code);
        }
    }

    @Test
    void testGetAllProvinces_VerifyResponseStructure() throws Exception {
        // Given
        when(locationRestService.getAllProvinces()).thenReturn(testProvinces);

        // When & Then - verify response structure and data types
        mockMvc.perform(get("/api/location/provinces")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").isArray());

        verify(locationRestService, times(1)).getAllProvinces();
    }
}

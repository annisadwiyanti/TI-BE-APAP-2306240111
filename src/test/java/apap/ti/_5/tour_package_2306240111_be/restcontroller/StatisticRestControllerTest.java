package apap.ti._5.tour_package_2306240111_be.restcontroller;

import apap.ti._5.tour_package_2306240111_be.restdto.response.statistic.ActivityTypeRevenueDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.response.statistic.RevenueStatisticDTO;
import apap.ti._5.tour_package_2306240111_be.restservice.StatisticRestService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class StatisticRestControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private StatisticRestService statisticRestService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper = new ObjectMapper();
    private RevenueStatisticDTO revenueStatistic;
    private List<ActivityTypeRevenueDTO> revenueByActivityType;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        revenueByActivityType = new ArrayList<>();
        revenueByActivityType.add(ActivityTypeRevenueDTO.builder()
                .activityType("Flight")
                .totalRevenue(150000000L)
                .build());
        revenueByActivityType.add(ActivityTypeRevenueDTO.builder()
                .activityType("Accommodation")
                .totalRevenue(200000000L)
                .build());
        revenueByActivityType.add(ActivityTypeRevenueDTO.builder()
                .activityType("Transport")
                .totalRevenue(100000000L)
                .build());

        revenueStatistic = RevenueStatisticDTO.builder()
                .year(2025)
                .month(11)
                .revenueByActivityType(revenueByActivityType)
                .build();
    }

    @Test
    void testGetRevenueStatisticsWithYearAndMonthSuccess() throws Exception {
        when(statisticRestService.getRevenueStatistics(2025, 11))
                .thenReturn(revenueStatistic);

        mockMvc.perform(get("/api/statistics")
                .param("year", "2025")
                .param("month", "11")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Revenue statistics retrieved successfully"))
                .andExpect(jsonPath("$.data.year").value(2025))
                .andExpect(jsonPath("$.data.month").value(11))
                .andExpect(jsonPath("$.data.revenueByActivityType", hasSize(3)))
                .andExpect(jsonPath("$.data.revenueByActivityType[0].activityType").value("Flight"))
                .andExpect(jsonPath("$.data.revenueByActivityType[0].totalRevenue").value(150000000))
                .andExpect(jsonPath("$.data.revenueByActivityType[1].activityType").value("Accommodation"))
                .andExpect(jsonPath("$.data.revenueByActivityType[1].totalRevenue").value(200000000))
                .andExpect(jsonPath("$.data.revenueByActivityType[2].activityType").value("Transport"))
                .andExpect(jsonPath("$.data.revenueByActivityType[2].totalRevenue").value(100000000))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(statisticRestService, times(1)).getRevenueStatistics(2025, 11);
    }

    @Test
    void testGetRevenueStatisticsWithYearOnlySuccess() throws Exception {
        RevenueStatisticDTO yearStatistic = RevenueStatisticDTO.builder()
                .year(2025)
                .month(0)
                .revenueByActivityType(revenueByActivityType)
                .build();

        when(statisticRestService.getRevenueStatistics(2025, 0))
                .thenReturn(yearStatistic);

        mockMvc.perform(get("/api/statistics")
                .param("year", "2025")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Revenue statistics retrieved successfully"))
                .andExpect(jsonPath("$.data.year").value(2025))
                .andExpect(jsonPath("$.data.month").value(0))
                .andExpect(jsonPath("$.data.revenueByActivityType", hasSize(3)))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(statisticRestService, times(1)).getRevenueStatistics(2025, 0);
    }

    @Test
    void testGetRevenueStatisticsMissingYearParameter() throws Exception {
        mockMvc.perform(get("/api/statistics")
                .param("month", "11")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error: Required parameter 'year' is missing"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(statisticRestService, never()).getRevenueStatistics(anyInt(), anyInt());
    }

    @Test
    void testGetRevenueStatisticsException() throws Exception {
        when(statisticRestService.getRevenueStatistics(2025, 11))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/statistics")
                .param("year", "2025")
                .param("month", "11")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Error: Database error"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(statisticRestService, times(1)).getRevenueStatistics(2025, 11);
    }

    @Test
    void testGetRevenueStatisticsEmptyActivityTypeList() throws Exception {
        RevenueStatisticDTO emptyStatistic = RevenueStatisticDTO.builder()
                .year(2025)
                .month(11)
                .revenueByActivityType(new ArrayList<>())
                .build();

        when(statisticRestService.getRevenueStatistics(2025, 11))
                .thenReturn(emptyStatistic);

        mockMvc.perform(get("/api/statistics")
                .param("year", "2025")
                .param("month", "11")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.revenueByActivityType", hasSize(0)))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(statisticRestService, times(1)).getRevenueStatistics(2025, 11);
    }

    @Test
    void testGetRevenueStatisticsWithDifferentYear() throws Exception {
        RevenueStatisticDTO otherYearStatistic = RevenueStatisticDTO.builder()
                .year(2024)
                .month(12)
                .revenueByActivityType(revenueByActivityType)
                .build();

        when(statisticRestService.getRevenueStatistics(2024, 12))
                .thenReturn(otherYearStatistic);

        mockMvc.perform(get("/api/statistics")
                .param("year", "2024")
                .param("month", "12")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.year").value(2024))
                .andExpect(jsonPath("$.data.month").value(12))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(statisticRestService, times(1)).getRevenueStatistics(2024, 12);
    }

    @Test
    void testGetRevenueStatisticsWithSingleActivityType() throws Exception {
        List<ActivityTypeRevenueDTO> singleActivityType = new ArrayList<>();
        singleActivityType.add(ActivityTypeRevenueDTO.builder()
                .activityType("Flight")
                .totalRevenue(500000000L)
                .build());

        RevenueStatisticDTO singleActivityStatistic = RevenueStatisticDTO.builder()
                .year(2025)
                .month(11)
                .revenueByActivityType(singleActivityType)
                .build();

        when(statisticRestService.getRevenueStatistics(2025, 11))
                .thenReturn(singleActivityStatistic);

        mockMvc.perform(get("/api/statistics")
                .param("year", "2025")
                .param("month", "11")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.revenueByActivityType", hasSize(1)))
                .andExpect(jsonPath("$.data.revenueByActivityType[0].activityType").value("Flight"))
                .andExpect(jsonPath("$.data.revenueByActivityType[0].totalRevenue").value(500000000))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(statisticRestService, times(1)).getRevenueStatistics(2025, 11);
    }

    @Test
    void testGetRevenueStatisticsResponseStructure() throws Exception {
        when(statisticRestService.getRevenueStatistics(2025, 11))
                .thenReturn(revenueStatistic);

        mockMvc.perform(get("/api/statistics")
                .param("year", "2025")
                .param("month", "11")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").exists());

        verify(statisticRestService, times(1)).getRevenueStatistics(2025, 11);
    }

    @Test
    void testGetRevenueStatisticsWithMonth0() throws Exception {
        RevenueStatisticDTO month0Statistic = RevenueStatisticDTO.builder()
                .year(2025)
                .month(0)
                .revenueByActivityType(revenueByActivityType)
                .build();

        when(statisticRestService.getRevenueStatistics(2025, 0))
                .thenReturn(month0Statistic);

        mockMvc.perform(get("/api/statistics")
                .param("year", "2025")
                .param("month", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.month").value(0))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(statisticRestService, times(1)).getRevenueStatistics(2025, 0);
    }

    @Test
    void testGetRevenueStatisticsWithMonth12() throws Exception {
        RevenueStatisticDTO month12Statistic = RevenueStatisticDTO.builder()
                .year(2025)
                .month(12)
                .revenueByActivityType(revenueByActivityType)
                .build();

        when(statisticRestService.getRevenueStatistics(2025, 12))
                .thenReturn(month12Statistic);

        mockMvc.perform(get("/api/statistics")
                .param("year", "2025")
                .param("month", "12")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.month").value(12))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(statisticRestService, times(1)).getRevenueStatistics(2025, 12);
    }

    @Test
    void testGetRevenueStatisticsWithLargeRevenue() throws Exception {
        List<ActivityTypeRevenueDTO> largeRevenueList = new ArrayList<>();
        largeRevenueList.add(ActivityTypeRevenueDTO.builder()
                .activityType("Flight")
                .totalRevenue(9999999999L)
                .build());

        RevenueStatisticDTO largeRevenueStatistic = RevenueStatisticDTO.builder()
                .year(2025)
                .month(11)
                .revenueByActivityType(largeRevenueList)
                .build();

        when(statisticRestService.getRevenueStatistics(2025, 11))
                .thenReturn(largeRevenueStatistic);

        mockMvc.perform(get("/api/statistics")
                .param("year", "2025")
                .param("month", "11")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.revenueByActivityType[0].totalRevenue").value(9999999999L))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(statisticRestService, times(1)).getRevenueStatistics(2025, 11);
    }

    @Test
    void testGetRevenueStatisticsWithMultipleActivityTypes() throws Exception {
        List<ActivityTypeRevenueDTO> multipleActivityTypes = new ArrayList<>();
        multipleActivityTypes.add(ActivityTypeRevenueDTO.builder()
                .activityType("Flight")
                .totalRevenue(100000000L)
                .build());
        multipleActivityTypes.add(ActivityTypeRevenueDTO.builder()
                .activityType("Hotel")
                .totalRevenue(200000000L)
                .build());
        multipleActivityTypes.add(ActivityTypeRevenueDTO.builder()
                .activityType("Tour")
                .totalRevenue(300000000L)
                .build());
        multipleActivityTypes.add(ActivityTypeRevenueDTO.builder()
                .activityType("Transport")
                .totalRevenue(150000000L)
                .build());
        multipleActivityTypes.add(ActivityTypeRevenueDTO.builder()
                .activityType("Entertainment")
                .totalRevenue(50000000L)
                .build());

        RevenueStatisticDTO multiTypeStatistic = RevenueStatisticDTO.builder()
                .year(2025)
                .month(11)
                .revenueByActivityType(multipleActivityTypes)
                .build();

        when(statisticRestService.getRevenueStatistics(2025, 11))
                .thenReturn(multiTypeStatistic);

        mockMvc.perform(get("/api/statistics")
                .param("year", "2025")
                .param("month", "11")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.revenueByActivityType", hasSize(5)))
                .andExpect(jsonPath("$.data.revenueByActivityType[2].activityType").value("Tour"))
                .andExpect(jsonPath("$.data.revenueByActivityType[4].activityType").value("Entertainment"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(statisticRestService, times(1)).getRevenueStatistics(2025, 11);
    }

    @Test
    void testGetRevenueStatisticsNoYearParameterInDefaultCase() throws Exception {
        mockMvc.perform(get("/api/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error: Required parameter 'year' is missing"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(statisticRestService, never()).getRevenueStatistics(anyInt(), anyInt());
    }
}
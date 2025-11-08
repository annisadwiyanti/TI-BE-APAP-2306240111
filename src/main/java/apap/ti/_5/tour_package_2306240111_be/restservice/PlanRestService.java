package apap.ti._5.tour_package_2306240111_be.restservice;

import apap.ti._5.tour_package_2306240111_be.model.Plan;
import apap.ti._5.tour_package_2306240111_be.restdto.request.plan.CreatePlanRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.request.plan.UpdatePlanRequestDTO;

import java.util.UUID;

public interface PlanRestService {
    Plan createPlan(String packageId, CreatePlanRequestDTO requestDTO);

    Plan getPlanById(UUID planId);

    Plan updatePlan(UUID planId, UpdatePlanRequestDTO requestDTO);

    void deletePlan(UUID planId);
}

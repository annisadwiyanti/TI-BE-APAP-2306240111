package apap.ti._5.tour_package_2306240111_be.restservice;

import apap.ti._5.tour_package_2306240111_be.model.Package;
import apap.ti._5.tour_package_2306240111_be.model.Plan;
import apap.ti._5.tour_package_2306240111_be.repository.OrderedQuantityRepository;
import apap.ti._5.tour_package_2306240111_be.repository.PackageRepository;
import apap.ti._5.tour_package_2306240111_be.repository.PlanRepository;
import apap.ti._5.tour_package_2306240111_be.restdto.request.plan.CreatePlanRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.request.plan.UpdatePlanRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class PlanRestServiceImpl implements PlanRestService {
    
    private final PlanRepository planRepository;
    private final PackageRepository packageRepository;
    private final OrderedQuantityRepository orderedQuantityRepository;
    
    public PlanRestServiceImpl(
            PlanRepository planRepository,
            PackageRepository packageRepository,
            OrderedQuantityRepository orderedQuantityRepository) {
        this.planRepository = planRepository;
        this.packageRepository = packageRepository;
        this.orderedQuantityRepository = orderedQuantityRepository;
    }
    
    @Override
    public Plan createPlan(String packageId, CreatePlanRequestDTO requestDTO) {
        // package exists and status is Pending
        Package pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + packageId));
        
        if (!"Pending".equals(pkg.getStatus())) {
            throw new RuntimeException("Cannot create plan. Package status must be 'Pending'");
        }
        
        // endDate > startDate
        if (requestDTO.getEndDate().isBefore(requestDTO.getStartDate()) || 
            requestDTO.getEndDate().isEqual(requestDTO.getStartDate())) {
            throw new RuntimeException("Plan end date must be after start date");
        }
        
        // plan startDate >= package startDate
        if (requestDTO.getStartDate().isBefore(pkg.getStartDate())) {
            throw new RuntimeException("Plan start date cannot be before package start date");
        }
        
        // plan endDate <= package endDate
        if (requestDTO.getEndDate().isAfter(pkg.getEndDate())) {
            throw new RuntimeException("Plan end date cannot be after package end date");
        }
        
        // accommodation: startLocation == endLocation
        if ("Accommodation".equals(requestDTO.getActivityType())) {
            if (requestDTO.getStartLocation() == null || requestDTO.getEndLocation() == null ||
                !requestDTO.getStartLocation().equals(requestDTO.getEndLocation())) {
                throw new RuntimeException("For Accommodation, start location and end location must be the same");
            }
        }
        
        Plan newPlan = Plan.builder()
                .packageId(packageId)
                .planName(requestDTO.getPlanName())
                .activityType(requestDTO.getActivityType())
                .price(0L) 
                .status("Unfulfilled") 
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .startLocation(requestDTO.getStartLocation())
                .endLocation(requestDTO.getEndLocation())
                .build();
        
        return planRepository.save(newPlan);
    }
    
    @Override
    public Plan getPlanById(UUID planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + planId));
    }

    @Override
    public Plan updatePlan(UUID planId, UpdatePlanRequestDTO requestDTO) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + planId));
        
        Package pkg = packageRepository.findById(plan.getPackageId())
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + plan.getPackageId()));
        
        if (!"Pending".equals(pkg.getStatus())) {
            throw new RuntimeException("Cannot update plan. Package status must be 'Pending'");
        }
        
        if (!orderedQuantityRepository.findByPlanId(planId).isEmpty()) {
            throw new RuntimeException("Cannot update plan. Plan has ordered activities. Cannot update plan with ordered activities");
        }
        
        if (requestDTO.getEndDate().isBefore(requestDTO.getStartDate()) || 
            requestDTO.getEndDate().isEqual(requestDTO.getStartDate())) {
            throw new RuntimeException("Plan end date must be after start date");
        }
        
        // plan startDate >= package startDate
        if (requestDTO.getStartDate().isBefore(pkg.getStartDate())) {
            throw new RuntimeException("Plan start date cannot be before package start date");
        }
        
        // plan endDate <= package endDate
        if (requestDTO.getEndDate().isAfter(pkg.getEndDate())) {
            throw new RuntimeException("Plan end date cannot be after package end date");
        }
        
        // accommodation: startLocation == endLocation
        if ("Accommodation".equals(plan.getActivityType())) {
            if (requestDTO.getStartLocation() == null || requestDTO.getEndLocation() == null ||
                !requestDTO.getStartLocation().equals(requestDTO.getEndLocation())) {
                throw new RuntimeException("For Accommodation, start location and end location must be the same");
            }
        }
        
        plan.setPlanName(requestDTO.getPlanName());
        plan.setStartDate(requestDTO.getStartDate());
        plan.setEndDate(requestDTO.getEndDate());
        plan.setStartLocation(requestDTO.getStartLocation());
        plan.setEndLocation(requestDTO.getEndLocation());
        
        return planRepository.save(plan);
    }

    @Override
    public void deletePlan(UUID planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + planId));
        
        Package pkg = packageRepository.findById(plan.getPackageId())
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + plan.getPackageId()));
        
        if (!"Pending".equals(pkg.getStatus())) {
            throw new RuntimeException("Cannot delete plan. Package status must be 'Pending'");
        }
        
        plan.setIsDeleted(true);
        planRepository.save(plan);
    }
}

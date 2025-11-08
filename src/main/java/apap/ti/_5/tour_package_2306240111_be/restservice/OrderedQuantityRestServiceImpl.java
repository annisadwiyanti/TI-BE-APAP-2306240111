package apap.ti._5.tour_package_2306240111_be.restservice;

import apap.ti._5.tour_package_2306240111_be.model.Activity;
import apap.ti._5.tour_package_2306240111_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306240111_be.model.Package;
import apap.ti._5.tour_package_2306240111_be.model.Plan;
import apap.ti._5.tour_package_2306240111_be.repository.ActivityRepository;
import apap.ti._5.tour_package_2306240111_be.repository.OrderedQuantityRepository;
import apap.ti._5.tour_package_2306240111_be.repository.PackageRepository;
import apap.ti._5.tour_package_2306240111_be.repository.PlanRepository;
import apap.ti._5.tour_package_2306240111_be.restdto.request.orderedquantity.CreateOrderedQuantityRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.request.orderedquantity.UpdateOrderedQuantityRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderedQuantityRestServiceImpl implements OrderedQuantityRestService {
    
    private final OrderedQuantityRepository orderedQuantityRepository;
    private final PlanRepository planRepository;
    private final PackageRepository packageRepository;
    private final ActivityRepository activityRepository;
    
    public OrderedQuantityRestServiceImpl(
            OrderedQuantityRepository orderedQuantityRepository,
            PlanRepository planRepository,
            PackageRepository packageRepository,
            ActivityRepository activityRepository) {
        this.orderedQuantityRepository = orderedQuantityRepository;
        this.planRepository = planRepository;
        this.packageRepository = packageRepository;
        this.activityRepository = activityRepository;
    }
    
    private String normalizeLocation(String location) {
        if (location == null) return "";
        // remove common prefixes and suffixes
        return location
                .replaceAll("\\s*\\(Provinsi\\)\\s*", "")
                .replaceAll("\\s*\\(Daerah Istimewa\\)\\s*", "")
                .replaceAll("^DKI\\s+", "")
                .replaceAll("^Daerah Istimewa\\s+", "")  
                .replaceAll("\\s+Provinsi$", "")  
                .trim()
                .toLowerCase();
    }
    
    @Override
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }
    
    @Override
    public List<Activity> getEligibleActivities(UUID planId) {
        // Get plan
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + planId));
        
        // Get all activities
        List<Activity> allActivities = activityRepository.findAll();
        
        System.out.println("\n========================================");
        System.out.println("=== FILTERING ELIGIBLE ACTIVITIES ===");
        System.out.println("========================================");
        System.out.println("Plan criteria:");
        System.out.println("  Type: " + plan.getActivityType());
        System.out.println("  Dates: " + plan.getStartDate() + " to " + plan.getEndDate());
        System.out.println("  Route: '" + plan.getStartLocation() + "' -> '" + plan.getEndLocation() + "'");
        System.out.println("Total activities in DB: " + allActivities.size());
        
        String planStartLoc = normalizeLocation(plan.getStartLocation());
        String planEndLoc = normalizeLocation(plan.getEndLocation());
        
        System.out.println("  Normalized Route: '" + planStartLoc + "' -> '" + planEndLoc + "'");
        System.out.println("========================================\n");
        
        final int[] passedCount = {0};
        final int[] typeFailCount = {0};
        final int[] locationFailCount = {0};
        final int[] monthFailCount = {0};
        final int[] dateFailCount = {0};
        
        // Sample some matching activities by type first
        System.out.println("Sample activities of type '" + plan.getActivityType() + "':");
        allActivities.stream()
            .filter(a -> a.getActivityType().equals(plan.getActivityType()))
            .limit(5)
            .forEach(a -> {
                System.out.println("  - " + a.getActivityName());
                System.out.println("    Raw locations: '" + a.getStartLocation() + "' -> '" + a.getEndLocation() + "'");
                System.out.println("    Normalized: '" + normalizeLocation(a.getStartLocation()) + "' -> '" + normalizeLocation(a.getEndLocation()) + "'");
                System.out.println("    Dates: " + a.getStartDate() + " to " + a.getEndDate());
            });
        System.out.println();
        
        // Filter activities
        List<Activity> eligibleActivities = allActivities.stream()
                .filter(activity -> {
                    // Filter 1: Activity type must match
                    if (!activity.getActivityType().equals(plan.getActivityType())) {
                        typeFailCount[0]++;
                        return false;
                    }
                    
                    // Filter 2: Locations must match (normalized)
                    String actStartLoc = normalizeLocation(activity.getStartLocation());
                    String actEndLoc = normalizeLocation(activity.getEndLocation());
                    
                    // DEBUG activities that match type
                    if (locationFailCount[0] < 5) {
                        System.out.println("Checking Activity " + activity.getId() + ":");
                        System.out.println("  Name: " + activity.getActivityName());
                        System.out.println("  Raw locations: '" + activity.getStartLocation() + "' -> '" + activity.getEndLocation() + "'");
                        System.out.println("  Normalized: '" + actStartLoc + "' -> '" + actEndLoc + "'");
                        System.out.println("  Expected: '" + planStartLoc + "' -> '" + planEndLoc + "'");
                    }
                    
                    if (!actStartLoc.equals(planStartLoc) || !actEndLoc.equals(planEndLoc)) {
                        locationFailCount[0]++;
                        if (locationFailCount[0] <= 5) {
                            System.out.println("  ✗ LOCATION MISMATCH");
                            System.out.println("    Start: '" + actStartLoc + "' vs '" + planStartLoc + "' = " + actStartLoc.equals(planStartLoc));
                            System.out.println("    End: '" + actEndLoc + "' vs '" + planEndLoc + "' = " + actEndLoc.equals(planEndLoc));
                        }
                        return false;
                    }
                    
                    // Filter 3: Date check - SAME MONTH AND YEAR
                    boolean sameMonth = activity.getStartDate().getMonth() == plan.getStartDate().getMonth() &&
                                       activity.getStartDate().getYear() == plan.getStartDate().getYear();
                    
                    if (!sameMonth) {
                        monthFailCount[0]++;
                        if (monthFailCount[0] <= 5) {
                            System.out.println("  ✗ MONTH MISMATCH");
                            System.out.println("    Activity month: " + activity.getStartDate().getMonth() + " " + activity.getStartDate().getYear());
                            System.out.println("    Plan month: " + plan.getStartDate().getMonth() + " " + plan.getStartDate().getYear());
                        }
                        return false;
                    }
                    
                    // Filter 4: For Flights - dates and times must match exactly
                    if ("Flight".equals(activity.getActivityType())) {
                        boolean startMatches = activity.getStartDate().equals(plan.getStartDate());
                        boolean endMatches = activity.getEndDate().equals(plan.getEndDate());
                        
                        if (!startMatches || !endMatches) {
                            dateFailCount[0]++;
                            if (dateFailCount[0] <= 5) {
                                System.out.println("  ✗ Flight date/time mismatch");
                                System.out.println("    Activity: " + activity.getStartDate() + " to " + activity.getEndDate());
                                System.out.println("    Plan: " + plan.getStartDate() + " to " + plan.getEndDate());
                            }
                            return false;
                        }
                    }
                    
                    passedCount[0]++;
                    if (passedCount[0] <= 5) {
                        System.out.println("  ✓✓✓ Activity " + activity.getId() + " PASSED all filters!");
                    }
                    return true;
                })
                .toList();
        
        System.out.println("\n========================================");
        System.out.println("=== FILTER RESULTS ===");
        System.out.println("========================================");
        System.out.println("Failed by type: " + typeFailCount[0]);
        System.out.println("Failed by location: " + locationFailCount[0]);
        System.out.println("Failed by month: " + monthFailCount[0]);
        System.out.println("Failed by date/time: " + dateFailCount[0]);
        System.out.println("✓ PASSED: " + eligibleActivities.size() + " eligible activities");
        System.out.println("========================================\n");
        
        return eligibleActivities;
    }
    
    @Override
    public OrderedQuantity createOrderedQuantity(CreateOrderedQuantityRequestDTO requestDTO) {
        Plan plan = planRepository.findById(requestDTO.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + requestDTO.getPlanId()));
        
        Activity activity = activityRepository.findById(requestDTO.getActivityId())
                .orElseThrow(() -> new RuntimeException("Activity not found with id: " + requestDTO.getActivityId()));
        
        Package pkg = packageRepository.findById(plan.getPackageId())
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + plan.getPackageId()));
        
        // package status must be "Pending"
        if (!"Pending".equals(pkg.getStatus())) {
            throw new RuntimeException("Cannot add activity. Package status must be 'Pending'");
        }
        
        // activity type must match Plan activity type
        if (!activity.getActivityType().equals(plan.getActivityType())) {
            throw new RuntimeException("Activity type must match Plan activity type");
        }

        // flights: must match exactly start and end date/time
        if ("Flight".equals(activity.getActivityType())) {
            boolean startMatches = activity.getStartDate().equals(plan.getStartDate());
            boolean endMatches = activity.getEndDate().equals(plan.getEndDate());
            
            if (!startMatches || !endMatches) {
                throw new RuntimeException("Flight dates and times must match Plan exactly");
            }
        } else {
            /// check same month
            boolean sameMonth = activity.getStartDate().getMonth() == plan.getStartDate().getMonth() &&
                               activity.getStartDate().getYear() == plan.getStartDate().getYear();
            
            if (!sameMonth) {
                throw new RuntimeException("Activity must be in the same month as Plan");
            }
        }
        
        // activity locations must match Plan locations
        String activityStartLoc = normalizeLocation(activity.getStartLocation());
        String activityEndLoc = normalizeLocation(activity.getEndLocation());
        String planStartLoc = normalizeLocation(plan.getStartLocation());
        String planEndLoc = normalizeLocation(plan.getEndLocation());
        
        System.out.println("=== LOCATION VALIDATION ===");
        System.out.println("Activity: '" + activity.getStartLocation() + "' -> '" + activity.getEndLocation() + "'");
        System.out.println("Normalized Activity: '" + activityStartLoc + "' -> '" + activityEndLoc + "'");
        System.out.println("Plan: '" + plan.getStartLocation() + "' -> '" + plan.getEndLocation() + "'");
        System.out.println("Normalized Plan: '" + planStartLoc + "' -> '" + planEndLoc + "'");
        
        if (!activityStartLoc.equals(planStartLoc) || !activityEndLoc.equals(planEndLoc)) {
            throw new RuntimeException("Activity start and end location must match Plan locations");
        }
        
        // ordered quantity cannot exceed activity capacity
        if (requestDTO.getOrderedQuota() > activity.getCapacity()) {
            throw new RuntimeException("Ordered quantity cannot exceed activity capacity");
        }
        
        // total ordered quantity for THIS PLAN cannot exceed package quota
        List<OrderedQuantity> planOQForValidation = orderedQuantityRepository.findByPlanId(plan.getId());
        int totalOrderedQuotaForThisPlan = 0;
        for (OrderedQuantity oq : planOQForValidation) {
            totalOrderedQuotaForThisPlan += oq.getOrderedQuota();
        }
        
        if (totalOrderedQuotaForThisPlan + requestDTO.getOrderedQuota() > pkg.getQuota()) {
            throw new RuntimeException("Total ordered quantity for this plan cannot exceed package quota");
        }
        
        OrderedQuantity newOrderedQuantity = OrderedQuantity.builder()
                .planId(requestDTO.getPlanId())
                .activityId(requestDTO.getActivityId())
                .orderedQuota(requestDTO.getOrderedQuota())
                .quota(activity.getCapacity())
                .price(activity.getPrice())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .build();
        
        OrderedQuantity saved = orderedQuantityRepository.save(newOrderedQuantity);
        
        // update plan price
        List<OrderedQuantity> planOrderedQuantities = orderedQuantityRepository.findByPlanId(plan.getId());
        long totalPrice = 0;
        int totalOrderedInThisPlan = 0;
        for (OrderedQuantity oq : planOrderedQuantities) {
            totalPrice += oq.getPrice() * oq.getOrderedQuota();
            totalOrderedInThisPlan += oq.getOrderedQuota();
        }
        plan.setPrice(totalPrice);
        
        if (totalOrderedInThisPlan == pkg.getQuota()) {
            plan.setStatus("Fulfilled");
        } else {
            plan.setStatus("Unfulfilled");
        }
        
        planRepository.save(plan);
        
        return saved;
    }
    
    @Override
    public OrderedQuantity getOrderedQuantityById(UUID id) {
        return orderedQuantityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordered Quantity not found with id: " + id));
    }
    
    @Override
    public OrderedQuantity updateOrderedQuantity(UUID id, UpdateOrderedQuantityRequestDTO requestDTO) {
        OrderedQuantity existingOQ = orderedQuantityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordered Quantity not found with id: " + id));
        
        Plan plan = planRepository.findById(existingOQ.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + existingOQ.getPlanId()));
        
        Activity activity = activityRepository.findById(existingOQ.getActivityId())
                .orElseThrow(() -> new RuntimeException("Activity not found with id: " + existingOQ.getActivityId()));
        
        Package pkg = packageRepository.findById(plan.getPackageId())
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + plan.getPackageId()));
        
        // package status must be "Pending"
        if (!"Pending".equals(pkg.getStatus())) {
            throw new RuntimeException("Cannot edit ordered quantity. Package status must be 'Pending'");
        }
        
        // ordered quota cannot exceed activity capacity
        if (requestDTO.getOrderedQuota() > activity.getCapacity()) {
            throw new RuntimeException("Ordered quantity cannot exceed activity capacity");
        }
        
        // total ordered quota for THIS PLAN cannot exceed package quota
        List<OrderedQuantity> planOQForValidation = orderedQuantityRepository.findByPlanId(plan.getId());
        int totalOrderedQuotaForThisPlan = 0;
        for (OrderedQuantity oq : planOQForValidation) {
            if (!oq.getId().equals(id)) {
                totalOrderedQuotaForThisPlan += oq.getOrderedQuota();
            }
        }

        if (totalOrderedQuotaForThisPlan + requestDTO.getOrderedQuota() > pkg.getQuota()) {
            throw new RuntimeException("Total ordered quantity for this plan cannot exceed package quota");
        }
        
        // update ordered quantity
        existingOQ.setOrderedQuota(requestDTO.getOrderedQuota());
        OrderedQuantity updated = orderedQuantityRepository.save(existingOQ);
        
        // recalculate plan price
        List<OrderedQuantity> planOrderedQuantities = orderedQuantityRepository.findByPlanId(plan.getId());
        long totalPrice = 0;
        for (OrderedQuantity oq : planOrderedQuantities) {
            totalPrice += oq.getPrice() * oq.getOrderedQuota();
        }
        plan.setPrice(totalPrice);
        
        if (totalOrderedQuotaForThisPlan + requestDTO.getOrderedQuota() == pkg.getQuota()) {
            plan.setStatus("Fulfilled");
        } else {
            plan.setStatus("Unfulfilled");
        }
        
        planRepository.save(plan);
        
        return updated;
    }
    
    @Override
    public void deleteOrderedQuantity(UUID id) {
        OrderedQuantity existingOQ = orderedQuantityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordered Quantity not found with id: " + id));
        
        Plan plan = planRepository.findById(existingOQ.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + existingOQ.getPlanId()));
        
        Package pkg = packageRepository.findById(plan.getPackageId())
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + plan.getPackageId()));
        
  
        if (!"Pending".equals(pkg.getStatus())) {
            throw new RuntimeException("Cannot delete ordered quantity. Package status must be 'Pending'");
        }
        
        orderedQuantityRepository.delete(existingOQ);
        
        // recalculate plan price after deletion
        List<OrderedQuantity> planOrderedQuantities = orderedQuantityRepository.findByPlanId(plan.getId());
        long totalPrice = 0;
        for (OrderedQuantity oq : planOrderedQuantities) {
            totalPrice += oq.getPrice() * oq.getOrderedQuota();
        }
        plan.setPrice(totalPrice);
        

        int totalOrderedQuotaForThisPlan = 0;
        for (OrderedQuantity oq : planOrderedQuantities) {
            totalOrderedQuotaForThisPlan += oq.getOrderedQuota();
        }

        
        if (totalOrderedQuotaForThisPlan == pkg.getQuota()) {
            plan.setStatus("Fulfilled");
        } else {
            plan.setStatus("Unfulfilled");
        }
        
        planRepository.save(plan);
    }
}
package apap.ti._5.tour_package_2306240111_be.restservice;

import apap.ti._5.tour_package_2306240111_be.model.Package;
import apap.ti._5.tour_package_2306240111_be.model.Plan;
import apap.ti._5.tour_package_2306240111_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306240111_be.repository.PackageRepository;
import apap.ti._5.tour_package_2306240111_be.repository.PlanRepository;
import apap.ti._5.tour_package_2306240111_be.repository.OrderedQuantityRepository;
import apap.ti._5.tour_package_2306240111_be.restdto.request.packagereq.CreatePackageRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.request.packagereq.UpdatePackageRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PackageRestServiceImpl implements PackageRestService {
    
    private final PackageRepository packageRepository;
    private final PlanRepository planRepository;
    private final OrderedQuantityRepository orderedQuantityRepository;
    
    public PackageRestServiceImpl(
            PackageRepository packageRepository,
            PlanRepository planRepository,
            OrderedQuantityRepository orderedQuantityRepository) {
        this.packageRepository = packageRepository;
        this.planRepository = planRepository;
        this.orderedQuantityRepository = orderedQuantityRepository;
    }
    
    @Override
    public Page<Package> getAllPackages(Pageable pageable) {
        return packageRepository.findAll(pageable);
    }
    
    @Override
    public Package getPackageById(String id) {
        return packageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + id));
    }
    
    @Override
    public Package createPackage(CreatePackageRequestDTO requestDTO) {
        if (requestDTO.getEndDate().isBefore(requestDTO.getStartDate()) || 
            requestDTO.getEndDate().isEqual(requestDTO.getStartDate())) {
            throw new RuntimeException("End date must be after start date");
        }
        
        String packageId = generatePackageId(requestDTO.getUserId());
        
        Package newPackage = Package.builder()
                .id(packageId)
                .userId(requestDTO.getUserId())
                .packageName(requestDTO.getPackageName())
                .quota(requestDTO.getQuota())
                .price(0L) 
                .status("Pending")
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .isDeleted(false)
                .build();
        
        return packageRepository.save(newPackage);
    }
    
    @Override
    public String generatePackageId(String userId) {
        long count = packageRepository.countByUserId(userId);
        return String.format("PACK-%s-%03d", userId, count + 1);
    }
    
    @Override
    public void deletePackage(String id) {
        Package pkg = packageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + id));
        
        // package status must be "Pending"
        if (!"Pending".equals(pkg.getStatus())) {
            throw new RuntimeException("Cannot delete package. Package status must be 'Pending'");
        }
        
        // delete all associated plans and their ordered quantities
        List<Plan> plans = planRepository.findByPackageId(id);
        for (Plan plan : plans) {
            List<OrderedQuantity> orderedQuantities = orderedQuantityRepository.findByPlanId(plan.getId());
            orderedQuantityRepository.deleteAll(orderedQuantities);
            
            planRepository.delete(plan);
        }
        
        pkg.setIsDeleted(true);
        packageRepository.save(pkg);
    }
    
    @Override
    public Package updatePackage(String id, UpdatePackageRequestDTO requestDTO) {
        Package pkg = packageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + id));
        
        // package status must be "Pending"
        if (!"Pending".equals(pkg.getStatus())) {
            throw new RuntimeException("Cannot update package. Package status must be 'Pending'");
        }
        
        // package must not have any plans
        List<Plan> plans = planRepository.findByPackageId(id);
        if (!plans.isEmpty()) {
            throw new RuntimeException("Cannot update package. Package has associated plans. Remove all plans first.");
        }
        
        if (requestDTO.getEndDate().isBefore(requestDTO.getStartDate()) || 
            requestDTO.getEndDate().isEqual(requestDTO.getStartDate())) {
            throw new RuntimeException("End date must be after start date");
        }
        
        // userId cannot be changed
        pkg.setPackageName(requestDTO.getPackageName());
        pkg.setQuota(requestDTO.getQuota());
        pkg.setStartDate(requestDTO.getStartDate());
        pkg.setEndDate(requestDTO.getEndDate());
        
        return packageRepository.save(pkg);
    }

    @Override
    public Package processPackage(String id) {
        Package pkg = packageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + id));
        
        List<Plan> plans = planRepository.findByPackageId(id);
        
        // must have status "Fulfilled"
        for (Plan plan : plans) {
            if (!"Fulfilled".equals(plan.getStatus())) {
                throw new RuntimeException("Cannot process package. All plans must have status 'Fulfilled'");
            }
        }
        
        pkg.setStatus("Processed");
        
        return packageRepository.save(pkg);
    }
}

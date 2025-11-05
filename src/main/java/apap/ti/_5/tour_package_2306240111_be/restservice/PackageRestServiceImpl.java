package apap.ti._5.tour_package_2306240111_be.restservice;

import apap.ti._5.tour_package_2306240111_be.model.Package;
import apap.ti._5.tour_package_2306240111_be.repository.PackageRepository;
import apap.ti._5.tour_package_2306240111_be.restdto.request.packagereq.CreatePackageRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PackageRestServiceImpl implements PackageRestService {
    
    @Autowired
    private PackageRepository packageRepository;
    
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
        // Count existing packages for this user
        long count = packageRepository.countByUserId(userId);
        
        // Format: PACK-{userId}-{3-digit sequence}
        return String.format("PACK-%s-%03d", userId, count + 1);
    }
}

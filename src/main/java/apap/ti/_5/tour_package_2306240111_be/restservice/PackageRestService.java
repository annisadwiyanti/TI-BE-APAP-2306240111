package apap.ti._5.tour_package_2306240111_be.restservice;

import apap.ti._5.tour_package_2306240111_be.model.Package;
import apap.ti._5.tour_package_2306240111_be.restdto.request.packagereq.CreatePackageRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PackageRestService {
    
    // Fitur 2: Get all packages with pagination
    Page<Package> getAllPackages(Pageable pageable);
    
    // Fitur 3: Get package detail by id
    Package getPackageById(String id);
    
    // Fitur 4: Create new package
    Package createPackage(CreatePackageRequestDTO requestDTO);
    
    // Helper method untuk generate package ID
    String generatePackageId(String userId);
}

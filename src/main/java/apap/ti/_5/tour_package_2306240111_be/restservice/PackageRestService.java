package apap.ti._5.tour_package_2306240111_be.restservice;

import apap.ti._5.tour_package_2306240111_be.model.Package;
import apap.ti._5.tour_package_2306240111_be.restdto.request.packagereq.CreatePackageRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.request.packagereq.UpdatePackageRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PackageRestService {
    
    Page<Package> getAllPackages(Pageable pageable);
    
    Package getPackageById(String id);
    
    Package createPackage(CreatePackageRequestDTO requestDTO);
    
    String generatePackageId(String userId);
    
    void deletePackage(String id);
    
    Package updatePackage(String id, UpdatePackageRequestDTO requestDTO);
    
    Package processPackage(String id);
}

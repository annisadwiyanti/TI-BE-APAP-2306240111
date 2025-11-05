package apap.ti._5.tour_package_2306240111_be.repository;

import apap.ti._5.tour_package_2306240111_be.model.Package;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Package, String> {
    Page<Package> findAll(Pageable pageable);
    Optional<Package> findById(String id);
    long countByUserId(String userId);
}

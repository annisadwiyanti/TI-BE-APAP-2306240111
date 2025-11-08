package apap.ti._5.tour_package_2306240111_be.repository;

import apap.ti._5.tour_package_2306240111_be.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> {
    @Query("SELECT p FROM Plan p WHERE p.packageId = :packageId AND (p.isDeleted IS NULL OR p.isDeleted = false)")
    List<Plan> findByPackageId(@Param("packageId") String packageId);
}

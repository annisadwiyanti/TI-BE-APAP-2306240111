package apap.ti._5.tour_package_2306240111_be.repository;

import apap.ti._5.tour_package_2306240111_be.model.OrderedQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderedQuantityRepository extends JpaRepository<OrderedQuantity, UUID> {
    List<OrderedQuantity> findByPlanId(UUID planId);

    @Query("SELECT oq FROM OrderedQuantity oq WHERE YEAR(oq.startDate) = :year AND (:month = 0 OR MONTH(oq.startDate) = :month)")
    List<OrderedQuantity> findByYearAndMonth(@Param("year") int year, @Param("month") int month);
}

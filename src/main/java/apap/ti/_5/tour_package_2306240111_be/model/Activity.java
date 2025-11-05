package apap.ti._5.tour_package_2306240111_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "activities")
public class Activity {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "activity_name", nullable = false)
    private String activityName;

    @Column(name = "activity_item")
    private String activityItem;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "activity_type", nullable = false)
    private String activityType; // Flight, Accommodation, Vehicle Rental

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "start_location")
    private String startLocation;

    @Column(name = "end_location")
    private String endLocation;
}
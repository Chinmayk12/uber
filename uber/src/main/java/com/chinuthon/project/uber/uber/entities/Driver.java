package com.chinuthon.project.uber.uber.entities;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Will help to query fast is someone is finding driver on vehicleId
@Table(indexes = {
    @Index(name = "idx_driver_vehicle_id",columnList = "vehicleId")
})
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Double rating;

    private Boolean available;

    private String vehicleId;

    // Indicates the current location earth (latitude and longitude)
    @Column(columnDefinition = "Geometry(Point, 4326)")
    private Point currentLocation;

}

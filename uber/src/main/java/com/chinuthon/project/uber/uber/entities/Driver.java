package com.chinuthon.project.uber.uber.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.geolatte.geom.Point;

@Entity
@Getter
@Setter
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

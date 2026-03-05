package com.chinuthon.project.uber.uber.entities;

import com.chinuthon.project.uber.uber.dto.DriverDto;
import jakarta.persistence.*;
import lombok.*;
import org.modelmapper.internal.bytebuddy.description.field.FieldDescription;

@Entity
@Table(indexes = {
        @Index(name = "idx_rating_rider",columnList = "rider_id"),
        @Index(name = "idx_rating_driver",columnList = "driver_id")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Ride ride;

    @ManyToOne
    private Rider rider;

    @ManyToOne
    private Driver driver;

    private Integer driverRating;
    private Integer riderRating;
}

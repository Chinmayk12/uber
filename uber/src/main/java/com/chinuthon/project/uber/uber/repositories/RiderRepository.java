package com.chinuthon.project.uber.uber.repositories;

import com.chinuthon.project.uber.uber.entities.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderRepository extends JpaRepository<Rider,Long> {
}

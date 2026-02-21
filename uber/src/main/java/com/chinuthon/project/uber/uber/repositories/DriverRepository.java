package com.chinuthon.project.uber.uber.repositories;

import com.chinuthon.project.uber.uber.entities.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver,Long> {
}

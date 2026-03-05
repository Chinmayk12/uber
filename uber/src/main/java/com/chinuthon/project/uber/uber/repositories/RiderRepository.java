package com.chinuthon.project.uber.uber.repositories;

import com.chinuthon.project.uber.uber.entities.Rider;
import com.chinuthon.project.uber.uber.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RiderRepository extends JpaRepository<Rider,Long> {
    Optional<Rider> findByUser(User user);
}

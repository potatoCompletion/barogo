package com.barogo.api.delivery.repository;

import com.barogo.api.delivery.domain.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    @Query("SELECT d FROM Delivery d JOIN FETCH d.user WHERE d.user.id = :userId AND d.orderDateTime BETWEEN :startDate AND :endDate")
    Page<Delivery> findByUserIdAndOrderDateTimeBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    Optional<Delivery> findByIdAndUserId(Long id, Long userId);
}

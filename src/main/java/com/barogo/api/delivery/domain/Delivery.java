package com.barogo.api.delivery.domain;

import com.barogo.api.delivery.enums.DeliveryStatus;
import com.barogo.api.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(nullable = false)
    private LocalDateTime orderDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Delivery(Long id, String destination, DeliveryStatus status, LocalDateTime orderDateTime, User user) {
        this.id = id;
        this.destination = destination;
        this.status = status;
        this.orderDateTime = orderDateTime;
        this.user = user;
    }

    @PrePersist
    protected void onCreate() {
        this.orderDateTime = LocalDateTime.now();
    }

    public boolean isUpdatable() {
        // 배달 상태가 보류, 배달 중 상태일 때만 업데이트 가능
        return status == DeliveryStatus.PENDING || status == DeliveryStatus.IN_PROGRESS;
    }

    public void updateDestination(String destination) {
        this.destination = destination;
    }
}

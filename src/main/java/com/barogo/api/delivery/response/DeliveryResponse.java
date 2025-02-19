package com.barogo.api.delivery.response;

import com.barogo.api.delivery.domain.Delivery;
import com.barogo.api.delivery.enums.DeliveryStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DeliveryResponse(Long id, String userName, String destination, DeliveryStatus status, LocalDateTime orderDateTime) {

    public static DeliveryResponse fromDelivery(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .userName(delivery.getUser().getName())
                .destination(delivery.getDestination())
                .status(delivery.getStatus())
                .orderDateTime(delivery.getOrderDateTime())
                .build();
    }
}

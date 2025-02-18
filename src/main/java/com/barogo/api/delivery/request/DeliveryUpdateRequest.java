package com.barogo.api.delivery.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DeliveryUpdateRequest {

    @NotBlank(message = "도착지 주소를 입력해주세요.")
    private String destination;

    @Builder
    public DeliveryUpdateRequest(String destination) {
        this.destination = destination;
    }
}

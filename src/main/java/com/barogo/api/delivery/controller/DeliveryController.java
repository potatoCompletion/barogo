package com.barogo.api.delivery.controller;

import com.barogo.api.delivery.request.DeliverySearchRequest;
import com.barogo.api.delivery.request.DeliveryUpdateRequest;
import com.barogo.api.delivery.response.DeliveryResponse;
import com.barogo.api.delivery.response.DeliveryUpdateResponse;
import com.barogo.api.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping
    public ResponseEntity<Page<DeliveryResponse>> getUserDeliveries(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute @Valid DeliverySearchRequest request) {

        Page<DeliveryResponse> deliveries = deliveryService.getUserDeliveries(userDetails.getUsername(), request);
        return ResponseEntity.ok(deliveries);
    }

    @PatchMapping("/{deliveryId}")
    public ResponseEntity<DeliveryUpdateResponse> updateDelivery(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long deliveryId,
            @RequestBody @Valid DeliveryUpdateRequest request) {

        deliveryService.updateDelivery(userDetails.getUsername(), deliveryId, request);
        return ResponseEntity.ok(new DeliveryUpdateResponse("도착지 주소가 성공적으로 변경되었습니다."));
    }
}

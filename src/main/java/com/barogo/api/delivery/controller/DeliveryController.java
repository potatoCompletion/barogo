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

    /**
     * 배달 조회 API
     * @param userDetails 로그인한 유저 정보
     * @param request 배달 조회 요청 정보
     * @return DeliveryResponse 페이지
     */
    @GetMapping
    public ResponseEntity<Page<DeliveryResponse>> getUserDeliveries(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute @Valid DeliverySearchRequest request) {

        Page<DeliveryResponse> deliveries = deliveryService.getUserDeliveries(userDetails.getUsername(), request);
        return ResponseEntity.ok(deliveries);
    }

    /**
     * 배달 주문 수정 API
     * @param userDetails 로그인한 유저 정보
     * @param deliveryId 수정할 배달 ID (PathVariable)
     * @param request 수정 요청 정보
     * @return 수정 성공 메세지
     */
    @PatchMapping("/{deliveryId}")
    public ResponseEntity<DeliveryUpdateResponse> updateDelivery(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long deliveryId,
            @RequestBody @Valid DeliveryUpdateRequest request) {

        deliveryService.updateDelivery(userDetails.getUsername(), deliveryId, request);
        return ResponseEntity.ok(new DeliveryUpdateResponse("도착지 주소가 성공적으로 변경되었습니다."));
    }
}

package com.barogo.api.delivery.service;

import com.barogo.api.common.exception.InvalidDateRangeException;
import com.barogo.api.common.exception.base.ErrorCode;
import com.barogo.api.delivery.domain.Delivery;
import com.barogo.api.delivery.exception.DeliveryNotFoundException;
import com.barogo.api.delivery.exception.InvalidDeliveryStateException;
import com.barogo.api.delivery.repository.DeliveryRepository;
import com.barogo.api.delivery.request.DeliverySearchRequest;
import com.barogo.api.delivery.request.DeliveryUpdateRequest;
import com.barogo.api.delivery.response.DeliveryResponse;
import com.barogo.api.user.domain.User;
import com.barogo.api.user.exception.UserNotFoundException;
import com.barogo.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<DeliveryResponse> getUserDeliveries(String userId, DeliverySearchRequest request) {
        // 유저 찾기
        User user = userRepository.findByUserId(userId)
                .orElseThrow(UserNotFoundException::new);

        // 조회 일자 검증 (최대 3일)
        if (ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) > 2) {
            throw new InvalidDateRangeException(ErrorCode.INVALID_DELIVERY_DATE);
        }

        // LocalDate -> LocalDateTime(startDate의 00:00:00부터 endDate의 23:59:59까지로 변환)
        LocalDateTime startDateTime = request.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = request.getEndDate().atTime(LocalTime.MAX);

        // 배달 조회
        Page<Delivery> deliveries = deliveryRepository.findByUserIdAndOrderDateTimeBetween(
                user.getId(),
                startDateTime,
                endDateTime,
                request.toPageable()
        );

        return deliveries.map(DeliveryResponse::fromDelivery);
    }

    @Transactional
    public void updateDelivery(String userId, Long deliveryId, DeliveryUpdateRequest request) {
        // 유저 찾기
        User user = userRepository.findByUserId(userId)
                .orElseThrow(UserNotFoundException::new);

        // 배달 내역 찾기(userId와 같이 찾아서 해당 유저의 주문이 맞는지 확인)
        Delivery delivery = deliveryRepository.findByIdAndUserId(deliveryId, user.getId())
                .orElseThrow(() -> new DeliveryNotFoundException());

        // 수정 가능한 배달인지 검증
        if (!delivery.isUpdatable()) {
            throw new InvalidDeliveryStateException();
        }

        delivery.updateDestination(request.getDestination());
    }
}

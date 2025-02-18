package com.barogo.api.delivery.service;

import com.barogo.api.common.exception.InvalidDateRangeException;
import com.barogo.api.delivery.domain.Delivery;
import com.barogo.api.delivery.enums.DeliveryStatus;
import com.barogo.api.delivery.exception.DeliveryNotFoundException;
import com.barogo.api.delivery.repository.DeliveryRepository;
import com.barogo.api.delivery.request.DeliverySearchRequest;
import com.barogo.api.delivery.request.DeliveryUpdateRequest;
import com.barogo.api.delivery.response.DeliveryResponse;
import com.barogo.api.user.domain.User;
import com.barogo.api.user.exception.UserNotFoundException;
import com.barogo.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class DeliveryServiceTest {

    private static final String TEST_ID = "testId";
    private static final String NON_EXISTENT_ID = "nonExistentId";

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    private User testUser;

    @BeforeEach
    void clean() {
        deliveryRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .userId(TEST_ID)
                .password("testPassword123!@#")
                .name("김완수")
                .build();
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("배달 조회 - 정상 요청")
    void 배달_조회_테스트() {
        // given
        List<Delivery> requestDeliveries = IntStream.range(1, 26)
                .mapToObj(i -> Delivery.builder()
                        .destination("서울특별시 강남구 " + i)
                        .status(DeliveryStatus.COMPLETED)
                        .orderDateTime(LocalDateTime.now())
                        .user(testUser)
                        .build())
                .collect(Collectors.toList());
        deliveryRepository.saveAll(requestDeliveries);

        DeliverySearchRequest request1 = DeliverySearchRequest.builder()
                .page(0)
                .size(10)
                .startDate(LocalDate.now().minusDays(3))
                .endDate(LocalDate.now())
                .build();

        DeliverySearchRequest request2 = DeliverySearchRequest.builder()
                .page(1)
                .size(10)
                .startDate(LocalDate.now().minusDays(3))
                .endDate(LocalDate.now())
                .build();

        DeliverySearchRequest request3 = DeliverySearchRequest.builder()
                .page(2)
                .size(10)
                .startDate(LocalDate.now().minusDays(3))
                .endDate(LocalDate.now())
                .build();

        // when
        Page<DeliveryResponse> deliveries1 = deliveryService.getUserDeliveries(testUser.getUserId(), request1);
        Page<DeliveryResponse> deliveries2 = deliveryService.getUserDeliveries(testUser.getUserId(), request2);
        Page<DeliveryResponse> deliveries3 = deliveryService.getUserDeliveries(testUser.getUserId(), request3);

        // then
        assertEquals(10, deliveries1.getContent().size());
        assertEquals(10, deliveries2.getContent().size());
        assertEquals(5, deliveries3.getContent().size());
    }

    @Test
    @DisplayName("배달 조회 - 존재하지 않는 유저(UserNotFoundException)")
    void 존재하지_않는_유저_배달_조회_테스트() {
        // given
        DeliverySearchRequest request = DeliverySearchRequest.builder()
                .page(0)
                .size(10)
                .startDate(LocalDate.now().minusDays(3))
                .endDate(LocalDate.now())
                .build();

        // expected
        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> {
            deliveryService.getUserDeliveries(NON_EXISTENT_ID, request);
        });

        assertEquals("해당 유저를 찾을 수 없습니다.", e.getErrorCode().getMessage());
    }

    @Test
    @DisplayName("배달 조회 - 3일 초과 기간(InvalidDateRangeException)")
    void 잘못된_기간_배달_조회_테스트() {
        // given
        List<Delivery> requestDeliveries = IntStream.range(1, 26)
                .mapToObj(i -> Delivery.builder()
                        .destination("서울특별시 강남구 " + i)
                        .status(DeliveryStatus.COMPLETED)
                        .orderDateTime(LocalDateTime.now())
                        .user(testUser)
                        .build())
                .collect(Collectors.toList());
        deliveryRepository.saveAll(requestDeliveries);

        DeliverySearchRequest request = DeliverySearchRequest.builder()
                .page(0)
                .size(10)
                .startDate(LocalDate.now().minusDays(4))    // 4일로 세팅
                .endDate(LocalDate.now())
                .build();

        // expected
        InvalidDateRangeException e = assertThrows(InvalidDateRangeException.class, () -> {
            deliveryService.getUserDeliveries(testUser.getUserId(), request);
        });

        assertEquals("조회 가능한 기간은 최대 3일까지 가능합니다.", e.getErrorCode().getMessage());
    }

    @Test
    @DisplayName("배달 주문 수정 - 정상 요청")
    void 배달_주문_수정_테스트() {
        // given
        Delivery delivery = Delivery.builder()
                .destination("서울특별시 강남구 1")
                .status(DeliveryStatus.IN_PROGRESS)
                .orderDateTime(LocalDateTime.now())
                .user(testUser)
                .build();
        Long deliveryId = deliveryRepository.save(delivery).getId();

        DeliveryUpdateRequest request = DeliveryUpdateRequest.builder()
                .destination("제주특별자치도 서귀포시 1")
                .build();

        // when
        deliveryService.updateDestination(TEST_ID, deliveryId, request);
        Delivery updatedDelivery = deliveryRepository.findById(deliveryId).orElseThrow();

        // then
        assertEquals("제주특별자치도 서귀포시 1", updatedDelivery.getDestination());
    }

    @Test
    @DisplayName("배달 주문 수정 - 존재하지 않는 유저(UserNotFoundException)")
    void 배달_주문_수정_존재하지_않는_유저_테스트() {
        // given
        Delivery delivery = Delivery.builder()
                .destination("서울특별시 강남구 1")
                .status(DeliveryStatus.IN_PROGRESS)
                .orderDateTime(LocalDateTime.now())
                .user(testUser)
                .build();
        Long deliveryId = deliveryRepository.save(delivery).getId();

        DeliveryUpdateRequest request = DeliveryUpdateRequest.builder()
                .destination("제주특별자치도 서귀포시 1")
                .build();

        // expected
        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> {
            deliveryService.updateDestination(NON_EXISTENT_ID, deliveryId, request);
        });

        assertEquals("해당 유저를 찾을 수 없습니다.", e.getErrorCode().getMessage());
    }

    @Test
    @DisplayName("배달 주문 수정 - 존재하지 않는 배달(DeliveryNotFoundException)")
    void 배달_주문_수정_존재하지_않는_배달_테스트() {
        // given
        Delivery delivery = Delivery.builder()
                .destination("서울특별시 강남구 1")
                .status(DeliveryStatus.IN_PROGRESS)
                .orderDateTime(LocalDateTime.now())
                .user(testUser)
                .build();
        Long wrongDeliveryId = deliveryRepository.save(delivery).getId() + 1L;

        DeliveryUpdateRequest request = DeliveryUpdateRequest.builder()
                .destination("제주특별자치도 서귀포시 1")
                .build();

        // expected
        DeliveryNotFoundException e = assertThrows(DeliveryNotFoundException.class, () -> {
            deliveryService.updateDestination(TEST_ID, wrongDeliveryId, request);
        });

        assertEquals("해당 배달 주문을 찾을 수 없습니다.", e.getErrorCode().getMessage());
    }

    @Test
    @DisplayName("배달 주문 수정 - userId 불일치(DeliveryNotFoundException)")
    void 배달_주문_수정_userId_불일치_테스트() {
        // given
        User anotherUser = User.builder()
                .userId("anotherId")
                .password("anotherPassword123!@#")
                .name("홍길동")
                .build();
        userRepository.save(anotherUser);

        Delivery delivery = Delivery.builder()
                .destination("서울특별시 강남구 1")
                .status(DeliveryStatus.IN_PROGRESS)
                .orderDateTime(LocalDateTime.now())
                .user(testUser)
                .build();
        Long deliveryId = deliveryRepository.save(delivery).getId();

        DeliveryUpdateRequest request = DeliveryUpdateRequest.builder()
                .destination("제주특별자치도 서귀포시 1")
                .build();

        // expected
        DeliveryNotFoundException e = assertThrows(DeliveryNotFoundException.class, () -> {
            deliveryService.updateDestination("anotherId", deliveryId, request);
        });

        assertEquals("해당 배달 주문을 찾을 수 없습니다.", e.getErrorCode().getMessage());
    }
}
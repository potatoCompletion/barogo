package com.barogo.api.delivery.controller;

import com.barogo.api.delivery.domain.Delivery;
import com.barogo.api.delivery.enums.DeliveryStatus;
import com.barogo.api.delivery.repository.DeliveryRepository;
import com.barogo.api.delivery.request.DeliveryUpdateRequest;
import com.barogo.api.user.domain.User;
import com.barogo.api.user.repository.UserRepository;
import com.barogo.api.auth.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String testToken;

    private User testUser;

    @BeforeEach
    void setUp() {
        deliveryRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .userId("testId")
                .password(passwordEncoder.encode("testPassword123!@#"))
                .name("김완수")
                .build();
        userRepository.save(testUser);

        testToken = jwtTokenProvider.generateToken("testId");

        List<Delivery> deliveries = IntStream.range(1, 26)
                .mapToObj(i -> Delivery.builder()
                        .user(testUser)
                        .status(DeliveryStatus.PENDING)
                        .destination("서울 강남구 " + i)
                        .orderDateTime(LocalDateTime.now().minusDays(i % 4))    // 0, 1, 2, 3 중 하나만큼 빼기
                        .build())
                .collect(Collectors.toList());

        deliveryRepository.saveAll(deliveries);
    }

    @Test
    @DisplayName("배달 조회 API - 정상 요청")
    void 배달_조회_테스트() throws Exception {
        // given
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = LocalDate.now();

        // expected
        mockMvc.perform(get("/api/v1/deliveries")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("page", "0")
                        .header("Authorization", "Bearer " + testToken)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("배달 조회 API - 페이지 기본값")
    void 배달_조회_페이지_기본값_테스트() throws Exception {
        // given
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = LocalDate.now();

        // expected
        mockMvc.perform(get("/api/v1/deliveries")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .header("Authorization", "Bearer " + testToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andDo(print());
    }

    @Test
    @DisplayName("배달 조회 API - 잘못된 날짜 요청 (startDate > endDate)")
    void 배달_조회_잘못된_날짜_요청_테스트() throws Exception {
        // given
        LocalDate startDate = LocalDate.now().minusDays(3);
        LocalDate endDate = LocalDate.now();

        // expected
        mockMvc.perform(get("/api/v1/deliveries")
                        .param("startDate", endDate.toString())         // 바꿔서 주입
                        .param("endDate", startDate.toString())
                        .header("Authorization", "Bearer " + testToken)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST_PARAM"))
                .andExpect(jsonPath("$.message").value("요청 검증 실패했습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("배달 조회 API - 빈 객체 요청")
    void 배달_조회_요청_검증_테스트() throws Exception {
        // given
        LocalDate startDate = LocalDate.now().minusDays(3);
        LocalDate endDate = LocalDate.now();

        // expected
        mockMvc.perform(get("/api/v1/deliveries")
                        .header("Authorization", "Bearer " + testToken)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST_PARAM"))
                .andExpect(jsonPath("$.message").value("요청 검증 실패했습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("배달 주문 수정 API - 정상 요청")
    void 배달_주문_수정_테스트() throws Exception {
        // given
        Delivery delivery = Delivery.builder()
                .user(testUser)
                .status(DeliveryStatus.IN_PROGRESS)
                .destination("서울 강남구")
                .orderDateTime(LocalDateTime.now())
                .build();
        Long targetId = deliveryRepository.save(delivery).getId();

        DeliveryUpdateRequest request = DeliveryUpdateRequest.builder()
                .destination("제주특별자치도 서귀포시 1")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(patch("/api/v1/deliveries/{targetId}", targetId)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("도착지 주소가 성공적으로 변경되었습니다."))
                .andDo(print());

        String updatedDestination = deliveryRepository.findById(targetId).orElseThrow().getDestination();
        assertEquals("제주특별자치도 서귀포시 1", updatedDestination);
    }

    @Test
    @DisplayName("배달 주문 수정 API - 빈 객체 요청")
    void 배달_주문_수정_빈_객체_테스트() throws Exception {
        // given
        Delivery delivery = Delivery.builder()
                .user(testUser)
                .status(DeliveryStatus.IN_PROGRESS)
                .destination("서울 강남구")
                .orderDateTime(LocalDateTime.now())
                .build();
        Long targetId = deliveryRepository.save(delivery).getId();

        DeliveryUpdateRequest request = DeliveryUpdateRequest.builder()
                .destination("")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(patch("/api/v1/deliveries/{targetId}", targetId)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST_PARAM"))
                .andExpect(jsonPath("$.message").value("요청 검증 실패했습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("배달 주문 수정 API - 업데이트 불가능 배달")
    void 배달_주문_수정_업데이트_불가_테스트() throws Exception {
        // given
        Delivery delivery = Delivery.builder()
                .user(testUser)
                .status(DeliveryStatus.COMPLETED)
                .destination("서울 강남구")
                .orderDateTime(LocalDateTime.now())
                .build();
        Long targetId = deliveryRepository.save(delivery).getId();

        DeliveryUpdateRequest request = DeliveryUpdateRequest.builder()
                .destination("제주특별자치도 서귀포시 1")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(patch("/api/v1/deliveries/{targetId}", targetId)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_DELIVERY_STATE"))
                .andExpect(jsonPath("$.message").value("이 배달은 수정이 불가능한 상태입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("배달 주문 수정 API - 존재하지 않는 배달")
    void 배달_주문_수정_존재하지_않는_배달_테스트() throws Exception {
        // given
        Long targetId = deliveryRepository.count() + 1L;

        DeliveryUpdateRequest request = DeliveryUpdateRequest.builder()
                .destination("제주특별자치도 서귀포시 1")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(patch("/api/v1/deliveries/{targetId}", targetId)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value("404"))
                .andExpect(jsonPath("$.errorCode").value("DELIVERY_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("해당 배달 주문을 찾을 수 없습니다."))
                .andDo(print());
    }
}
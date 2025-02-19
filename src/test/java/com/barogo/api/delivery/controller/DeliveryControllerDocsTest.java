package com.barogo.api.delivery.controller;

import com.barogo.api.auth.jwt.JwtTokenProvider;
import com.barogo.api.delivery.domain.Delivery;
import com.barogo.api.delivery.enums.DeliveryStatus;
import com.barogo.api.delivery.repository.DeliveryRepository;
import com.barogo.api.delivery.request.DeliveryUpdateRequest;
import com.barogo.api.user.domain.User;
import com.barogo.api.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
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

import static com.barogo.api.common.util.ApiDocumentUtils.getDocumentRequest;
import static com.barogo.api.common.util.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class DeliveryControllerDocsTest {

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
    @DisplayName("Spring Rest Docs - 배달 조회")
    void 문서생성_배달조회() throws Exception {
        // given
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = LocalDate.now();

        // expected
        mockMvc.perform(get("/api/v1/deliveries")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + testToken)
                )
                .andExpect(status().isOk())
                .andDo(document("delivery-search",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 액세스 토큰")
                        ),
                        queryParameters(
                                parameterWithName("startDate").description("조회 시작일 (YYYY-MM-dd)"),
                                parameterWithName("endDate").description("조회 종료일 (YYYY-MM-dd)"),
                                parameterWithName("page").description("조회할 페이지 번호 (0부터 시작)"),
                                parameterWithName("size").description("페이지당 데이터 개수 (최소 1)")
                        ),
                        responseFields(
                                fieldWithPath("content").description("배달 목록 데이터 배열"),
                                fieldWithPath("content[].id").description("배달 ID"),
                                fieldWithPath("content[].userName").description("주문 회원 이름"),
                                fieldWithPath("content[].destination").description("배달 도착지 주소"),
                                fieldWithPath("content[].status").description("배달 상태 (COMPLETED, PENDING 등)"),
                                fieldWithPath("content[].orderDateTime").description("배달 주문 날짜/시간"),

                                fieldWithPath("pageable").description("페이징 정보"),
                                fieldWithPath("pageable.sort").description("정렬 정보"),
                                fieldWithPath("pageable.sort.empty").description("정렬 데이터가 비어있는지 여부"),
                                fieldWithPath("pageable.sort.sorted").description("정렬 여부"),
                                fieldWithPath("pageable.sort.unsorted").description("비정렬 여부"),
                                fieldWithPath("pageable.offset").description("현재 페이지의 첫 번째 요소의 전체 데이터에서의 위치"),
                                fieldWithPath("pageable.pageNumber").description("현재 페이지 번호"),
                                fieldWithPath("pageable.pageSize").description("페이지당 데이터 개수"),
                                fieldWithPath("pageable.paged").description("페이징 여부"),
                                fieldWithPath("pageable.unpaged").description("비페이징 여부"),

                                fieldWithPath("totalPages").description("총 페이지 수"),
                                fieldWithPath("totalElements").description("총 데이터 개수"),
                                fieldWithPath("last").description("마지막 페이지 여부"),
                                fieldWithPath("size").description("요청한 페이지 크기"),
                                fieldWithPath("number").description("현재 페이지 번호"),
                                fieldWithPath("sort").description("정렬 정보"),
                                fieldWithPath("sort.empty").description("정렬 데이터가 비어있는지 여부"),
                                fieldWithPath("sort.sorted").description("정렬 여부"),
                                fieldWithPath("sort.unsorted").description("비정렬 여부"),
                                fieldWithPath("first").description("첫 번째 페이지 여부"),
                                fieldWithPath("numberOfElements").description("현재 페이지에서 반환된 데이터 개수"),
                                fieldWithPath("empty").description("데이터가 비어있는지 여부")
                        )
                ));
    }

    @Test
    @DisplayName("Spring Rest Docs - 배달 주문 수정")
    void 문서생성_배달주문수정() throws Exception {
        // given
        Delivery delivery = Delivery.builder()
                .user(testUser)
                .status(DeliveryStatus.IN_PROGRESS)
                .destination("서울 강남구")
                .orderDateTime(LocalDateTime.now())
                .build();
        Long deliveryId = deliveryRepository.save(delivery).getId();

        DeliveryUpdateRequest request = DeliveryUpdateRequest.builder()
                .destination("제주특별자치도 서귀포시 1")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(patch("/api/v1/deliveries/{deliveryId}", deliveryId)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                )
                .andExpect(status().isOk())
                .andDo(document("delivery-update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("deliveryId").description("수정할 배달 주문의 ID")
                        ),
                        requestFields(
                                fieldWithPath("destination").description("변경할 주소")
                        ),
                        responseFields(
                                fieldWithPath("message").description("배달 수정 성공 메세지")
                        )
                ));
    }
}

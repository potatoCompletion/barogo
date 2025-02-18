package com.barogo.api.delivery.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

@Getter
public class DeliverySearchRequest {

    // 정렬은 고정
    private static final String SORT_BY = "orderDateTime";
    private static final Sort.Direction DIRECTION = Sort.Direction.DESC;

    @NotNull(message = "조회 시작일을 입력해주세요.")
    private LocalDate startDate;

    @NotNull(message = "조회 종료일을 입력해주세요.")
    private LocalDate endDate;

    private Integer page = 0;

    @Min(value = 1, message = "size는 최소 1 이상이어야 합니다.")
    private Integer size = 10;

    @Builder
    public DeliverySearchRequest(LocalDate startDate, LocalDate endDate, Integer page, Integer size) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.page = (page != null && page >= 0) ? page : 0;
        this.size = (size != null) ? size : 10;
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size, Sort.by(DIRECTION, SORT_BY));
    }

    @AssertTrue(message = "조회 시작일은 조회 종료일보다 이전이어야 합니다.")
    public boolean isValidDate() {
        // 조회 시작일자가 종료일자 보다 빠른지 검증 (startDate, endDate가 null이 아닐 때만 검증)
        return startDate == null || endDate == null || !startDate.isAfter(endDate);
    }
}

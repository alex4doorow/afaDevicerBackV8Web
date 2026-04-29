package com.afa.devicer.web.services;

import com.afa.core.dto.orders.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final WebClient webClient;

    @Transactional(readOnly = true)
    public OrderPagedResponse fullFiltered(final OrderPagedFilter orderPagedFilter) {

        return webClient.post()
                .uri("/api/v8/orders/full-filtered")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(orderPagedFilter)
                .retrieve()
                .bodyToMono(OrderPagedResponse.class)
                .block();
    }

    @Transactional(readOnly = true)
    public OrderSingleResponse getOrderById(final Long orderId) {

        final String uri = "/api/v8/orders/%d".formatted(orderId);
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(OrderSingleResponse.class)
                .block();
    }

    @Transactional
    public OrderDto update(
            final Long orderId,
            final OrderSaveRequest request) {
        return null;
    }

    @Transactional
    public OrderDto changeStatusOrder(
            final Long orderId,
            final OrderChangeStatusSaveRequest request) {

        final String uri = "/api/v8/orders/%d/change-status".formatted(orderId);
        return webClient.patch()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OrderDto.class)
                .block();
    }
}

package com.afa.devicer.web.services;

import com.afa.core.dto.BaseResponse;
import com.afa.core.dto.orders.*;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.web.enums.WebDevicerErrors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final WebClient webClient;

    @Transactional(readOnly = true)
    public Long findNextOrderNum() {
        final String uri = "/api/v8/orders/next-order-num";
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Long.class)
                .block();
    }

    @Transactional(readOnly = true)
    public OrderPagedResponse fullFiltered(final OrderPagedFilter orderPagedFilter) {

        final OrderPagedResponse response = webClient.post()
                .uri("/api/v8/orders/full-filtered")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(orderPagedFilter)
                .retrieve()
                .bodyToMono(OrderPagedResponse.class)
                .block();
        if (response != null && response.getOrders() != null) {
            final List<OrderDto> orders = response.getOrders().stream()
                    .peek(dto -> dto.setPresentation(OrderPresentationStatusDto.createOrderPresentationStatusDto(dto)))
                    .toList();
            response.getOrders().clear();
            response.getOrders().addAll(orders);
        }
        return response;
    }

    @Transactional(readOnly = true)
    public OrderPagedResponse periodFiltered(final String periodName) {
        final String uri = "/api/v8/orders/period/%s".formatted(periodName);
        final OrderPagedResponse response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(OrderPagedResponse.class)
                .block();
        if (response != null && response.getOrders() != null) {
            final List<OrderDto> orders = response.getOrders().stream()
                    .peek(dto -> dto.setPresentation(OrderPresentationStatusDto.createOrderPresentationStatusDto(dto)))
                    .toList();
            response.getOrders().clear();
            response.getOrders().addAll(orders);
        }
        return response;
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
    public OrderDto create(final OrderSaveRequest request) {

        final String uri = "/api/v8/orders";
        final OrderSingleResponse response = webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OrderSingleResponse.class)
                .block();
        if (response != null && response.getResult() != null && StringUtils.equals(response.getResult(), "error")) {
            throw new DevicerException(DevicerErrors.UNKNOWN_VALIDATION_ERROR,
                    WebDevicerErrors.ORDER_SAVE_ERROR.getErrorMessage(),
                    response.getViolations());
        }
        return response == null ? null : response.getOrder();
    }

    @Transactional
    public OrderDto update(
            final Long orderId,
            final OrderSaveRequest request) {

        final String uri = "/api/v8/orders/%d".formatted(orderId);
        final OrderSingleResponse response = webClient.put()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OrderSingleResponse.class)
                .block();
        if (response != null && response.getResult() != null && StringUtils.equals(response.getResult(), "error")) {
            throw new DevicerException(DevicerErrors.UNKNOWN_VALIDATION_ERROR,
                    WebDevicerErrors.ORDER_SAVE_ERROR.getErrorMessage(),
                    response.getViolations());
        }
        return response == null ? null : response.getOrder();
    }

    @Transactional
    public OrderDto changeStatusOrder(
            final Long orderId,
            final OrderChangeStatusSaveRequest request) {

        final String uri = "/api/v8/orders/%d/change-status".formatted(orderId);
        final OrderSingleResponse response = webClient.patch()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OrderSingleResponse.class)
                .block();
        return response == null ? null : response.getOrder();
    }

    @Transactional
    public void delete(final Long orderId) {

        final String uri = "/api/v8/orders/%d".formatted(orderId);
        final BaseResponse response = webClient.delete()
                .uri(uri)
                .retrieve()
                .bodyToMono(BaseResponse.class)
                .block();
        if (response != null && response.getResult() != null && StringUtils.equals(response.getResult(), "error")) {
            throw new DevicerException(DevicerErrors.UNKNOWN_VALIDATION_ERROR,
                    WebDevicerErrors.ORDER_DELETE_ERROR.getErrorMessage(),
                    response.getViolations());
        }
    }
}

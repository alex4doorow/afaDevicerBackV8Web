package com.afa.devicer.web.services;

import com.afa.core.dto.customers.CustomerDto;
import com.afa.core.dto.customers.CustomerSaveRequest;
import com.afa.core.dto.customers.CustomerSingleResponse;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.web.enums.WebDevicerErrors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final WebClient webClient;

    @Transactional(readOnly = true)
    public CustomerSingleResponse getCustomerById(final Long customerId) {

        final String uri = "/api/v8/customers/%d".formatted(customerId);
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(CustomerSingleResponse.class)
                .block();
    }

    @Transactional
    public CustomerDto create(final CustomerSaveRequest request) {

        final String uri = "/api/v8/customers";
        final CustomerSingleResponse response = webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CustomerSingleResponse.class)
                .block();
        if (response != null && response.getResult() != null && StringUtils.equals(response.getResult(), "error")) {
            throw new DevicerException(DevicerErrors.UNKNOWN_VALIDATION_ERROR,
                    WebDevicerErrors.CUSTOMER_SAVE_ERROR.getErrorMessage(),
                    response.getViolations());
        }
        return response == null ? null : response.getCustomer();
    }

    @Transactional
    public CustomerDto update(
            final Long customerId,
            final CustomerSaveRequest request) {

        final String uri = "/api/v8/customers/%d".formatted(customerId);
        final CustomerSingleResponse response = webClient.put()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CustomerSingleResponse.class)
                .block();
        if (response != null && response.getResult() != null && StringUtils.equals(response.getResult(), "error")) {
            throw new DevicerException(DevicerErrors.UNKNOWN_VALIDATION_ERROR,
                    WebDevicerErrors.CUSTOMER_SAVE_ERROR.getErrorMessage(),
                    response.getViolations());
        }
        return response == null ? null : response.getCustomer();
    }
}



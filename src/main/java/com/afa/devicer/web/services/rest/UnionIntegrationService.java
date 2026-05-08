package com.afa.devicer.web.services.rest;

import com.afa.core.dto.customers.CustomerSearchPagedFilter;
import com.afa.core.dto.integrations.union.CustomerDataUnionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class UnionIntegrationService {

    private final WebClient webClient;

    @Transactional(readOnly = true)
    public CustomerDataUnionResponse getCustomerSuggest(final CustomerSearchPagedFilter filter) {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/v8/integrations/union/customers/suggest").build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(filter)
                .retrieve()
                .bodyToMono(CustomerDataUnionResponse.class)
                .block();
    }
}

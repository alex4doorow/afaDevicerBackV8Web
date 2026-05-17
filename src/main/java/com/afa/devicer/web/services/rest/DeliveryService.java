package com.afa.devicer.web.services.rest;

import com.afa.core.dto.delivery.DeliveryCalcParcelAmountsRequest;
import com.afa.core.dto.delivery.DeliveryCalcParcelAmountsResponse;
import com.afa.core.dto.delivery.DeliveryCalcParcelDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final WebClient webClient;

    public DeliveryCalcParcelAmountsResponse calcParcelDeliveryAmounts(final DeliveryCalcParcelAmountsRequest request) {

        final String uri = "/api/v8/delivery/calc/parcel-delivery-amounts";
        try {
            final DeliveryCalcParcelAmountsResponse response = webClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(DeliveryCalcParcelAmountsResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            return response == null
                    ? new DeliveryCalcParcelAmountsResponse(DeliveryCalcParcelDto.createEmpty())
                    : response;

        } catch (WebClientResponseException e) {
            log.warn("Delivery calc backend error status={} body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            return new DeliveryCalcParcelAmountsResponse(DeliveryCalcParcelDto.createEmpty());

        } catch (IllegalStateException  e) {
            log.warn("Delivery calc backend unavailable or timeout", e);
            return new DeliveryCalcParcelAmountsResponse(DeliveryCalcParcelDto.createEmpty());
        }
    }
}

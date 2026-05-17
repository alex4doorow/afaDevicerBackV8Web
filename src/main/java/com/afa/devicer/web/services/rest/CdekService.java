package com.afa.devicer.web.services.rest;

import com.afa.core.dto.integrations.cdek.CdekCityFilter;
import com.afa.core.dto.integrations.cdek.CdekCityResponse;
import com.afa.core.dto.integrations.cdek.CdekDeliveryPointFilter;
import com.afa.core.dto.integrations.cdek.CdekDeliveryPointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CdekService {

    private final WebClient webClient;

    @Transactional(readOnly = true)
    public CdekCityResponse getLocationCities(final CdekCityFilter filter) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v8/integrations/cdek/location/cities")
                        .queryParam("countryCode2", filter.getCountryCode2())
                        .queryParam("cityNameContext", filter.getCityNameContext())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CdekCityResponse>() {})
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    public CdekDeliveryPointResponse getDeliveryPoints(final CdekDeliveryPointFilter filter) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v8/integrations/cdek/deliveryPoints")
                        .queryParam("countryCode2", filter.getCountryCode2())
                        .queryParam("cityCode", filter.getCityCode())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CdekDeliveryPointResponse>() {})
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}

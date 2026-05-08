package com.afa.devicer.web.services;

import com.afa.core.dto.dictionaries.CountryDto;
import com.afa.core.dto.dictionaries.CountryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DictionaryService {

    private final WebClient webClient;

    @Transactional(readOnly = true)
    public List<CountryDto> getCountries() {

        final CountryResponse response = webClient.get()
                .uri("api/v8/dictionaries/countries")
                .retrieve()
                .bodyToMono(CountryResponse.class)
                .block();

        return response == null || response.getItems() == null
                ? Collections.emptyList()
                : response.getItems();
    }

    @Transactional(readOnly = true)
    public CountryDto getDefaultCountry() {
        return getCountries()
                .stream()
                .filter(c -> c.getIsoCode2().equals("RU"))
                .toList()
                .getFirst();
    }

}

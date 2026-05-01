package com.afa.devicer.web.services;

import com.afa.core.dto.BaseResponse;
import com.afa.core.dto.persons.PersonSettingsResponse;
import com.afa.core.dto.persons.PersonSettingsSaveRequest;
import com.afa.devicer.web.dto.persons.FormPersonSettingsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"PMD.LawOfDemeter"})
public class PersonSettingService {

    private final WebClient webClient;

    @Transactional(readOnly = true)
    public PersonSettingsResponse loadSettings() {
        return webClient.get()
                .uri("/api/v8/persons/settings")
                .retrieve()
                .bodyToMono(PersonSettingsResponse.class)
                .block();
    }

    @Transactional
    public void saveSettingsByOrderConditions(final FormPersonSettingsDto form) {
        final PersonSettingsSaveRequest request = createRequestByOrderConditions(form);
        saveSettings(request);
    }

    @Transactional
    public PersonSettingsSaveRequest createRequestByOrderConditions(final FormPersonSettingsDto form) {
        form.convertForm();
        final PersonSettingsSaveRequest request = PersonSettingsSaveRequest.builder()
                .settings(loadSettings().getSettings())
                .build();
        request.getSettings().setOrders(form.getOrders());
        return request;
    }

    private void saveSettings(final PersonSettingsSaveRequest request) {
        final BaseResponse response = webClient.post()
                .uri("/api/v8/persons/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(BaseResponse.class)
                .block();
        log.info("response: {}", response);
    }
}

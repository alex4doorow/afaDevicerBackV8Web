package com.afa.devicer.web.controllers.rest;

import com.afa.core.dto.BaseResponse;
import com.afa.core.dto.integrations.cdek.CdekCityFilter;
import com.afa.devicer.web.controllers.internal.ControllerConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.REST_INTEGRATIONS_CDEK)
public class CdekIntegrationRestController {

    // https://web/wiki/integrations/cdek/location/cities
    @GetMapping("/location/cities")
    public ResponseEntity<BaseResponse> get(@Valid @ModelAttribute final CdekCityFilter filter) {
        return ResponseEntity.ok(new BaseResponse());
    }
}

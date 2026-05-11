package com.afa.devicer.web.controllers.rest;

import com.afa.core.dto.integrations.cdek.CdekCityFilter;
import com.afa.core.dto.integrations.cdek.CdekCityResponse;
import com.afa.core.dto.integrations.cdek.CdekDeliveryPointFilter;
import com.afa.core.dto.integrations.cdek.CdekDeliveryPointResponse;
import com.afa.devicer.web.controllers.internal.ControllerConstants;
import com.afa.devicer.web.services.rest.CdekService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.REST_INTEGRATIONS_CDEK)
public class CdekIntegrationRestController {

    private final CdekService cdekService;

    // https://web/wiki/integrations/cdek/location/cities
    @GetMapping("/location/cities")
    public ResponseEntity<CdekCityResponse> get(@Valid @ModelAttribute final CdekCityFilter filter) {
        return ResponseEntity.ok(cdekService.getLocationCities(filter));
    }

    @GetMapping("/deliveryPoints")
    public ResponseEntity<CdekDeliveryPointResponse> getLocationCities(
            @Valid @ModelAttribute final CdekDeliveryPointFilter filter
    ) {
        return ResponseEntity.ok(cdekService.getDeliveryPoints(filter));
    }

    // https://web/wiki/integrations/cdek/widjet
    @PostMapping("/widget")
    public ResponseEntity<String> widget(@RequestBody String body) {
        return ResponseEntity.ok("{}");
    }
}

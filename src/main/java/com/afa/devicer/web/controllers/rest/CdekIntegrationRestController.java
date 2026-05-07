package com.afa.devicer.web.controllers.rest;

import com.afa.core.dto.BaseResponse;
import com.afa.core.dto.integrations.cdek.CdekCityFilter;
import com.afa.devicer.web.controllers.internal.ControllerConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.REST_INTEGRATIONS_CDEK)
public class CdekIntegrationRestController {

    // https://web/wiki/integrations/cdek/location/cities
    @GetMapping("/location/cities")
    public ResponseEntity<BaseResponse> get(@Valid @ModelAttribute final CdekCityFilter filter) {
        return ResponseEntity.ok(new BaseResponse());
    }

    // https://web/wiki/integrations/cdek/widjet
    @PostMapping("/widget")
    public ResponseEntity<String> widget(@RequestBody String body) {
        return ResponseEntity.ok("{}");
    }
}

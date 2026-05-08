package com.afa.devicer.web.controllers.rest;

import com.afa.core.dto.customers.CustomerSearchPagedFilter;
import com.afa.core.dto.integrations.union.CustomerDataUnionResponse;
import com.afa.devicer.web.controllers.internal.ControllerConstants;
import com.afa.devicer.web.services.rest.UnionIntegrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.REST_INTEGRATIONS_UNION)
public class UnionIntegrationRestController {

    private final UnionIntegrationService unionIntegrationService;

    @PostMapping("/customers/suggest")
    public ResponseEntity<CustomerDataUnionResponse> getCustomerSuggest(
            @Valid @RequestBody final CustomerSearchPagedFilter filter) {
        return ResponseEntity.ok(unionIntegrationService.getCustomerSuggest(filter));
    }
}

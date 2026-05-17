package com.afa.devicer.web.controllers.rest;

import com.afa.core.dto.delivery.DeliveryCalcParcelAmountsRequest;
import com.afa.core.dto.delivery.DeliveryCalcParcelAmountsResponse;
import com.afa.devicer.web.controllers.internal.ControllerConstants;
import com.afa.devicer.web.services.rest.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.REST_DELIVERY)
public class DeliveryRestController {

    private final DeliveryService deliveryService;

    @PostMapping("/calc/parcel-delivery-amounts")
    public ResponseEntity<DeliveryCalcParcelAmountsResponse> calcParcelDeliveryAmounts(
            @Valid @RequestBody final DeliveryCalcParcelAmountsRequest request) {

        return ResponseEntity.ok(deliveryService.calcParcelDeliveryAmounts(request));
    }
}

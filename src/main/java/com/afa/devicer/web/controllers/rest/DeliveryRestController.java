package com.afa.devicer.web.controllers.rest;

import com.afa.core.dto.BaseResponse;
import com.afa.devicer.web.controllers.internal.ControllerConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.REST_DELIVERY)
public class DeliveryRestController {

    // /ajax/orders/calc/parcel-delivery-amounts
    @PostMapping("/calc/parcel-delivery-amounts")
    public ResponseEntity<BaseResponse> calcParcelDeliveryAmounts(@Valid @ModelAttribute final Object filter) {
        return ResponseEntity.ok(
                new BaseResponse()
        );
    }
}

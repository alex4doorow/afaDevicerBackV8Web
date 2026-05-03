package com.afa.devicer.web.controllers.rest;

import com.afa.core.dto.products.ProductFilter;
import com.afa.core.dto.products.ProductResponse;
import com.afa.devicer.web.controllers.internal.ControllerConstants;
import com.afa.devicer.web.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.REST_PRODUCTS)
public class ProductRestController {

    private final ProductService productService;

    // https://web/wiki/products/suggest
    @GetMapping("/suggest")
    public ResponseEntity<ProductResponse> getProductsSuggest(@Valid @ModelAttribute final ProductFilter filter) {
        return ResponseEntity.ok(
                new ProductResponse(productService.getProductsSuggest(filter))
        );
    }
}

package com.afa.devicer.web.services;

import com.afa.core.dto.products.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final WebClient webClient;

    @Transactional(readOnly = true)
    public List<ProductCategoryDto> getProductCategories() {

        final ProductCategoryResponse response = webClient.get()
                .uri("api/v8/products/productCategories")
                .retrieve()
                .bodyToMono(ProductCategoryResponse.class)
                .block();
        return response != null ? response.getItems() : List.of();
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getProductsSuggest(final ProductFilter filter) {

        final ProductResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v8/products/suggest")
                        .queryParam("nameContext", filter.getNameContext())
                        .build())
                .retrieve()
                .bodyToMono(ProductResponse.class)
                .block();
        return response != null ? response.getItems() : List.of();
    }
}

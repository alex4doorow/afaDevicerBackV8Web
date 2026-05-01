package com.afa.devicer.web.controllers;

import com.afa.core.dto.dictionaries.OrderStatusTypeDto;
import com.afa.core.enums.*;
import com.afa.devicer.web.services.DictionaryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Getter
@RequiredArgsConstructor
public class BaseController {

    @Autowired
    protected MessageSource messageSource;
    @Autowired
    protected DictionaryService dictionaryService;

    protected void populateDefaultModel(final Model model) {

        final String brandSite = messageSource.getMessage("app.brand.site", null, LocaleContextHolder.getLocale());
        model.addAttribute("brandSite", brandSite);
        model.addAttribute("urlHome", "/web/index/");
        model.addAttribute("urlOrders", "/web/orders");
        model.addAttribute("urlLogout", "/login");
        model.addAttribute("msg", null);

        model.addAttribute("orderTypes", OrderTypes.values());
        model.addAttribute("orderSourceTypes", OrderSourceTypes.values());
        model.addAttribute("orderAdvertTypes", OrderAdvertTypes.values());
        model.addAttribute("orderPaymentTypes", OrderPaymentTypes.values());
        model.addAttribute("productCategories", dictionaryService.getProductCategories());
        model.addAttribute("orderStatuses", getOrderStatuses());
        setActiveMenu(model);
    }

    private List<OrderStatusTypeDto> getOrderStatuses() {
        return Arrays.stream(OrderStatusTypes.values())
                .filter(s -> s != OrderStatusTypes.UNKNOWN)
                .map(s -> OrderStatusTypeDto.builder()
                        .id(s.getId())
                        .code(s.getCode())
                        .annotation(s.getAnnotation())
                        .view(s.getView())
                        .build())
                .sorted(Comparator.comparing(OrderStatusTypeDto::getId))
                .toList();
    }

    protected void setActiveMenu(final Model model) {
        model.addAttribute("activeMenu", "");
    }
}

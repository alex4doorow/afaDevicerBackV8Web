package com.afa.devicer.web.controllers;

import com.afa.core.dto.dictionaries.OrderStatusTypeDto;
import com.afa.core.enums.OrderStatusTypes;
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
import java.util.stream.Collectors;

@Slf4j
@Getter
@RequiredArgsConstructor
public class BaseController {

    @Autowired
    protected MessageSource messageSource;

    protected void populateDefaultModel(final Model model) {

        final List<OrderStatusTypeDto> orderStatuses = Arrays.stream(OrderStatusTypes.values())
                .filter(s -> s == OrderStatusTypes.BID
                        || s == OrderStatusTypes.APPROVED
                        || s == OrderStatusTypes.DELIVERED
                        || s == OrderStatusTypes.CANCELED
                        || s == OrderStatusTypes.FINISHED)
                .map(s -> OrderStatusTypeDto.builder()
                        .id(s.getId())
                        .code(s.getCode())
                        .annotation(s.getAnnotation())
                        .view(s.getView())
                        .build())
                .sorted(Comparator.comparing(OrderStatusTypeDto::getId))
                .toList();
        model.addAttribute("orderStatuses", orderStatuses);

        final List<String> allViewStatuses = Arrays.stream(OrderStatusTypes.values())
                .map(OrderStatusTypes::getAnnotation)
                .collect(Collectors.toList());
        model.addAttribute("allViewStatusTypes", allViewStatuses);

        final String brandSite = messageSource.getMessage("app.brand.site", null, LocaleContextHolder.getLocale());
        model.addAttribute("brandSite", brandSite);
        model.addAttribute("urlHome", "/web/index/");
        model.addAttribute("urlOrders", "/web/orders");
        model.addAttribute("urlLogout", "/login");
        model.addAttribute("msg", null);
    }

}

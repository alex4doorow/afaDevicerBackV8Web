package com.afa.devicer.web.controllers;

import com.afa.core.dto.orders.*;
import com.afa.core.dto.persons.PersonSettingsResponse;
import com.afa.core.enums.AmountTypes;
import com.afa.core.enums.OrderStatusTypes;
import com.afa.devicer.web.controllers.internal.ControllerConstants;
import com.afa.devicer.web.dto.orders.FormOrderDto;
import com.afa.devicer.web.mappers.OrderDtoMapper;
import com.afa.devicer.web.services.OrderService;
import com.afa.devicer.web.services.PersonSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@CrossOrigin
@RequestMapping(ControllerConstants.ORDERS)
@Tag(name = "orders", description = "Orders controller")
@Controller
@RequiredArgsConstructor
@SuppressWarnings({"PMD"})
public class OrderController extends BaseController {

    private final PersonSettingService personSettingService;
    private final OrderService orderService;
    private final OrderDtoMapper orderDtoMapper;

    @GetMapping()
    public String list(final Model model) {

        final PersonSettingsResponse settings = personSettingService.loadSettings();
        final OrderPagedFilter orderPagedFilter = OrderPagedFilter.builder()
                .conditions(settings.getSettings().getOrders())
                .build();
        final OrderPagedResponse result = orderService.fullFiltered(orderPagedFilter);

        populateDefaultModel(model);
        model.addAttribute("orders", result.getOrders());
        model.addAttribute("totalAmounts", result.getTotalAmounts());
        model.addAttribute("amountConversionBid", result.getTotalAmounts().get(AmountTypes.CONVERSION_BID));
        model.addAttribute("amountConversionApproved", result.getTotalAmounts().get(AmountTypes.CONVERSION_APPROVED));

        return "orders/list.html";
    }

    @GetMapping("/{orderId}/show")
    @Operation(summary = "Order по идентификатору")
    public String getOrder4Show(
            @NotNull @Valid @PathVariable final Long orderId,
            final Model model) {

        final OrderSingleResponse response = orderService.getOrderById(orderId);
        populateDefaultModel(model);
        model.addAttribute("order", response.getOrder());
        model.addAttribute("listType", "typical");

        return "orders/show.html";
    }

    @GetMapping("/{orderId}/update")
    public String getOrder4Edit(
            @NotNull @Valid @PathVariable final Long orderId,
            final Model model) {

        final OrderSingleResponse response = orderService.getOrderById(orderId);
        final FormOrderDto form = orderDtoMapper.fromOrder(response.getOrder());

        populateDefaultModel(model);
        model.addAttribute("listType", "list");
        model.addAttribute("order", response.getOrder());
        model.addAttribute("formOrder", form);
        return "orders/orderForm.html";
    }

    @PostMapping("/{orderId}/update")
    public String saveOrder4Edit(
            @NotNull @Valid @PathVariable final Long orderId,
            @ModelAttribute("orderForm") @Validated FormOrderDto form,
            BindingResult bindingResult,
            Model model,
            final RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            final OrderSingleResponse response = orderService.getOrderById(orderId);
            model.addAttribute("listType", "list");
            model.addAttribute("order", response.getOrder());
            model.addAttribute("formOrder", form);
            return "orders/orderForm.html";
        }

        final OrderSaveRequest request = OrderSaveRequest.builder()
                .type(form.getType())
                .sourceType(form.getSourceType())
                .paymentType(form.getPaymentType())
                .productCategoryId(form.getFormProductCategoryId())
                .annotation(form.getAnnotation())
                .build();
        //final OrderDto result = orderService.update(orderId, request);
        //log.info("{}", result.getId());
        return "redirect:/web/orders";
    }

    @GetMapping("/{orderId}/change-status/{list-type}")
    public String getOrder4ChangeStatus(
            @NotNull @Valid @PathVariable final Long orderId,
            @PathVariable("list-type") String listType,
            final Model model) {

        final OrderSingleResponse response = orderService.getOrderById(orderId);
        final FormOrderDto form = orderDtoMapper.fromOrder(response.getOrder());

        populateDefaultModel(model);
        model.addAttribute("listType", listType);
        model.addAttribute("order", response.getOrder());
        model.addAttribute("formOrder", form);
        return "orders/orderStatusForm.html";
    }

    @PostMapping("/{orderId}/change-status/{list-type}")
    public String saveOrder4ChangeStatus(
            @NotNull @Valid @PathVariable final Long orderId,
            @PathVariable("list-type") String listType,
            @ModelAttribute("orderForm") @Validated FormOrderDto form,
            BindingResult bindingResult,
            Model model,
            final RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            final OrderSingleResponse response = orderService.getOrderById(orderId);
            model.addAttribute("listType", listType);
            model.addAttribute("order", response.getOrder());
            model.addAttribute("formOrder", form);
            return "orders/orderStatusForm.html";
        }

        final OrderChangeStatusSaveRequest request = OrderChangeStatusSaveRequest.builder()
                .type(form.getType())
                .sourceType(form.getSourceType())
                .paymentType(form.getPaymentType())
                .productCategoryId(form.getFormProductCategoryId())
                .status(OrderStatusTypes.valueOf(form.getFormStatusCode()))
                .annotation(form.getAnnotation())
                .trackCode(form.getDelivery().getTrackCode())
                .build();
        final OrderDto result = orderService.changeStatusOrder(orderId, request);
        log.info("{}", result.getId());
        return "redirect:/web/orders";
    }
}

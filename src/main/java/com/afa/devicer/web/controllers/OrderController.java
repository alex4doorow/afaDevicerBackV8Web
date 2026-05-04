package com.afa.devicer.web.controllers;

import com.afa.core.dto.dictionaries.AddressSaveRequest;
import com.afa.core.dto.orders.*;
import com.afa.core.dto.people.PersonSaveRequest;
import com.afa.core.dto.people.PersonSettingsResponse;
import com.afa.core.enums.*;
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

import java.math.BigDecimal;
import java.util.Set;

@Slf4j
@CrossOrigin
@RequestMapping(ControllerConstants.ORDERS)
@Tag(name = "orders", description = "Orders controller")
@Controller
@RequiredArgsConstructor
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.LawOfDemeter"})
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
        final FormOrderDto form = orderDtoMapper.fromOrderToForm(response.getOrder());

        populateDefaultModel(model);
        model.addAttribute("listType", "list");
        model.addAttribute("order", response.getOrder());
        model.addAttribute("formOrder", form);
        return "orders/orderForm.html";
    }

    @PostMapping("/{orderId}/update")
    public String saveOrder4Edit(
            @NotNull @Valid @PathVariable final Long orderId,
            @ModelAttribute("formOrder") @Validated final FormOrderDto form,
            final BindingResult bindingResult,
            final Model model,
            final RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            final OrderSingleResponse response = orderService.getOrderById(orderId);
            model.addAttribute("listType", "list");
            model.addAttribute("order", response.getOrder());
            model.addAttribute("formOrder", form);
            return "orders/orderForm.html";
        }
        form.convertForm();
        final OrderDto order = orderService.getOrderById(orderId).getOrder();
        final OrderSaveRequest request = OrderSaveRequest.builder()
                .orderNum(form.getOrderNum())
                .orderDate(form.getOrderDate())
                .type(form.getType())
                .sourceType(form.getSourceType())
                .advertType(form.getAdvertType())
                .store(form.getStore())
                .productCategoryId(form.getProductCategory().getId())
                .annotation(form.getAnnotation())
                .paymentType(form.getPaymentType())
                .customerId(form.getCustomer().getId())
                .delivery(OrderDeliverySaveRequest.builder()
                        .deliveryType(DeliveryTypes.valueOf(form.getDelivery().getDeliveryType().getCode()))
                        .deliveryPaymentType(form.getDelivery().getDeliveryPaymentType())
                        .deliveryPriceType(form.getDelivery().getDeliveryPriceType())
                        .price(form.getDelivery().getPrice())
                        .address(AddressSaveRequest.builder()
                                .countryId(form.getDelivery().getRecipient().getCountry().getId())
                                .type(AddressTypes.MAIN)
                                .addressLine(form.getDelivery().getAddress().getAddressLine())
                                .build())
                        .customerEqualsRecipient(form.isFormDeliveryCustomerEqualsRecipient())
                        .recipient(PersonSaveRequest.builder()
                                .firstName(form.getDelivery().getRecipient().getFirstName())
                                .middleName(form.getDelivery().getRecipient().getMiddleName())
                                .lastName(form.getDelivery().getRecipient().getLastName())
                                .phoneNumber(form.getDelivery().getRecipient().getPhoneNumber())
                                .build())
                        .deliveryDate(order.getDelivery().getDeliveryDate())
                        .build())
                .items(Set.of(OrderItemSaveRequest.builder()
                        .itemNum(1)
                        .productId(32L)
                        .price(BigDecimal.valueOf(4725))
                        .quantity(2)
                        .discountRate(BigDecimal.ZERO)
                        .build()))
                .build();

        final OrderDto result = orderService.update(orderId, request);
        log.info("{}", result.getId());
        return "redirect:/web/orders";
    }

    @GetMapping("/{orderId}/change-status/{list-type}")
    public String getOrder4ChangeStatus(
            @NotNull @Valid @PathVariable final Long orderId,
            @PathVariable("list-type") final String listType,
            final Model model) {

        final OrderSingleResponse response = orderService.getOrderById(orderId);
        final FormOrderDto form = orderDtoMapper.fromOrderToForm(response.getOrder());

        populateDefaultModel(model);
        model.addAttribute("listType", listType);
        model.addAttribute("order", response.getOrder());
        model.addAttribute("formOrder", form);
        return "orders/orderStatusForm.html";
    }

    @PostMapping("/{orderId}/change-status/{list-type}")
    public String saveOrder4ChangeStatus(
            @NotNull @Valid @PathVariable final Long orderId,
            @PathVariable("list-type") final String listType,
            @ModelAttribute("formOrder") @Validated final FormOrderDto form,
            final BindingResult bindingResult,
            final Model model,
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

    @Override
    protected void setActiveMenu(final Model model) {
        model.addAttribute("activeMenu", "orders");
    }

}

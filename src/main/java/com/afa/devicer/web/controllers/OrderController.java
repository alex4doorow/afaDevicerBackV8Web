package com.afa.devicer.web.controllers;

import com.afa.core.dto.companies.CompanySaveRequest;
import com.afa.core.dto.customers.CustomerAddressSaveRequest;
import com.afa.core.dto.customers.CustomerContactSaveRequest;
import com.afa.core.dto.customers.CustomerDto;
import com.afa.core.dto.customers.CustomerSaveRequest;
import com.afa.core.dto.dictionaries.AddressDto;
import com.afa.core.dto.dictionaries.AddressSaveRequest;
import com.afa.core.dto.dictionaries.DeliveryTypeDto;
import com.afa.core.dto.dictionaries.OrderStatusTypeDto;
import com.afa.core.dto.orders.*;
import com.afa.core.dto.people.PersonFullDto;
import com.afa.core.dto.people.PersonSaveRequest;
import com.afa.core.dto.people.PersonSettingsResponse;
import com.afa.core.dto.products.ProductCategoryDto;
import com.afa.core.enums.*;
import com.afa.core.utils.TextHelper;
import com.afa.devicer.web.controllers.internal.ControllerConstants;
import com.afa.devicer.web.dto.orders.FormOrderDto;
import com.afa.devicer.web.mappers.OrderDtoMapper;
import com.afa.devicer.web.services.CustomerService;
import com.afa.devicer.web.services.OrderService;
import com.afa.devicer.web.services.PersonSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin
@RequestMapping(ControllerConstants.ORDERS)
@Tag(name = "orders", description = "Orders controller")
@Controller
@RequiredArgsConstructor
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.LawOfDemeter", "PMD.ExcessiveImports"})
public class OrderController extends BaseController {

    private final PersonSettingService personSettingService;
    private final CustomerService customerService;
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

    @GetMapping("/create")
    public String getOrder4Create(final Model model) {

        final OrderDto order = OrderDto.builder()
                .id(0L)
                .orderNum(orderService.findNextOrderNum())
                .orderDate(LocalDate.now())
                .type(OrderTypes.ORDER)
                .store(StoreTypes.PM)
                .sourceType(OrderSourceTypes.CALL)
                .advertType(OrderAdvertTypes.ADVERT)
                .productCategory(getProductService().getProductCategories()
                        .stream()
                        .filter(pc -> pc.getId() > 0)
                        .sorted(Comparator.comparing(ProductCategoryDto::getId))
                        .toList()
                        .getFirst())
                .status(OrderStatusTypeDto.builder()
                        .code(OrderStatusTypes.BID.getCode())
                        .build())
                .amounts(Collections.emptyMap())
                .customer(CustomerDto.builder()
                        .id(0L)
                        .type(CustomerTypes.PERSON)
                        .person(PersonFullDto.builder()
                                .id(0L)
                                .country(dictionaryService.getDefaultCountry())
                                .build())
                        .build())
                .delivery(OrderDeliveryDto.builder()
                        .deliveryType(DeliveryTypeDto.builder()
                                .code(DeliveryTypes.CDEK_PVZ_TYPICAL.getCode())
                                .build())
                        .deliveryPaymentType(DeliveryPaymentTypes.CUSTOMER)
                        .address(AddressDto.builder()
                                .type(AddressTypes.MAIN)
                                .country(dictionaryService.getDefaultCountry())
                                .build())
                        .build())
                .build();
        final FormOrderDto form = orderDtoMapper.fromOrderToForm(order);

        populateDefaultModel(model);
        model.addAttribute("listType", "list");
        model.addAttribute("order", order);
        model.addAttribute("formOrder", form);
        return "orders/orderForm.html";
    }

    @PostMapping("/create")
    public String saveOrder4Create(
            @ModelAttribute("formOrder") @Validated final FormOrderDto form,
            final BindingResult bindingResult,
            final Model model) {

        if (bindingResult.hasErrors()) {

            populateDefaultModel(model);
            model.addAttribute("listType", "list");
            form.convertForm();
            model.addAttribute("order", form);
            model.addAttribute("formOrder", form);
            return "orders/orderForm.html";
        }
        form.convertForm();
        final CustomerSaveRequest customerRequest = createCustomerRequestByForm(form);

        final OrderDto result;
        final CustomerDto customer;
        if (form.getCustomer().getId() != null && form.getCustomer().getId() > 0) {

            customer = customerService.update(form.getCustomer().getId(), customerRequest);
        } else {

            customer = customerService.create(customerRequest);
            if (customer != null && customer.getId() > 0) {
                form.getCustomer().setId(customer.getId());
            }
        }
        if (customer == null) {
            log.error("{}", "customer didn't create");
            return "redirect:/web/orders";
        }
        result = orderService.create(createOrderRequestByForm(form));

        log.info("{}", result.getId());
        return "redirect:/web/orders";
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

            model.addAttribute("listType", "list");
            if (orderId > 0) {
                final OrderSingleResponse response = orderService.getOrderById(orderId);
                model.addAttribute("order", response.getOrder());
                model.addAttribute("formOrder", form);
            } else {
                form.convertForm();
                model.addAttribute("order", form);
                model.addAttribute("formOrder", form);
            }
            return "orders/orderForm.html";
        }
        form.convertForm();
        final CustomerSaveRequest customerRequest = createCustomerRequestByForm(form);
        final OrderDto result;
        customerService.update(form.getCustomer().getId(), customerRequest);
        result = orderService.update(orderId, createOrderRequestByForm(form));

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        orderService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Override
    protected void setActiveMenu(final Model model) {
        model.addAttribute("activeMenu", "orders");
    }

    private CustomerSaveRequest createCustomerRequestByForm(final FormOrderDto form) {
        final CompanySaveRequest companySaveRequest;
        final PersonSaveRequest personSaveRequest;
        final String addressLine;
        if (form.getCustomer().getType() == CustomerTypes.COMPANY) {
            companySaveRequest = CompanySaveRequest.builder()
                    .inn(form.getFormCustomerInn())
                    .shortName(form.getFormCustomerShortName())
                    .longName(form.getFormCustomerLongName())
                    .build();
            personSaveRequest = null;
            addressLine = form.getCustomer().getMainAddress().getAddress().getAddressLine();

        } else {
            companySaveRequest = null;
            personSaveRequest = PersonSaveRequest.builder()
                    .firstName(form.getFormCustomerContactPersonFirstName())
                    .middleName(form.getFormCustomerContactPersonMiddleName())
                    .lastName(form.getFormCustomerContactPersonLastName())
                    .phoneNumber(TextHelper.formatPhoneNumber(form.getFormCustomerContactPersonPhoneNumber()))
                    .email("")
                    .build();
            addressLine = form.getDelivery().getAddress().getAddressLine();
        }

        return CustomerSaveRequest.builder()
                .type(form.getCustomer().getType())
                .company(companySaveRequest)
                .person(personSaveRequest)
                .countryId(form.getFormCustomerCountryId())
                .contacts(Set.of(CustomerContactSaveRequest.builder()
                        .type(ContactTypes.MAIN)
                        .person(personSaveRequest)
                        .build()))
                .addresses(Set.of(CustomerAddressSaveRequest.builder()
                        .address(AddressSaveRequest.builder()
                                .countryId(form.getFormCustomerCountryId())
                                .type(AddressTypes.MAIN)
                                .addressLine(addressLine)
                                .build())
                        .build()))
                .build();
    }

    private OrderSaveRequest createOrderRequestByForm(final FormOrderDto form) {

        final AtomicInteger counter = new AtomicInteger(1);
        return OrderSaveRequest.builder()
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
                                .type(AddressTypes.MAIN)
                                .countryId(form.getDelivery().getRecipient().getCountry().getId())
                                .cityCode(form.getDelivery().getAddress().getCityCode())
                                .city(form.getDelivery().getAddress().getCity())
                                .deliveryPointCode(form.getDelivery().getAddress().getDeliveryPointCode())
                                .addressLine(form.getDelivery().getAddress().getAddressLine())
                                .build())
                        .customerEqualsRecipient(form.isFormDeliveryCustomerEqualsRecipient())
                        .recipient(OrderDeliveryRecipientSaveRequest.builder()
                                .firstName(form.getDelivery().getRecipient().getFirstName())
                                .middleName(form.getDelivery().getRecipient().getMiddleName())
                                .lastName(form.getDelivery().getRecipient().getLastName())
                                .phoneNumber(form.getDelivery().getRecipient().getPhoneNumber())
                                .build())
                        .deliveryDate(form.getDelivery().getDeliveryDate())
                        .build())
                .items(form.getItems()
                        .stream()
                        .map(item -> OrderItemSaveRequest.builder()
                                .itemNum(counter.getAndIncrement())
                                .productId(item.getProduct().getId())
                                .price(item.getPrice())
                                .quantity(item.getQuantity())
                                .discountRate(item.getDiscountRate())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }
}

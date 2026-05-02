package com.afa.devicer.web.mappers;

import com.afa.core.dto.orders.OrderDto;
import com.afa.core.enums.CustomerTypes;
import com.afa.devicer.web.dto.orders.FormOrderDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
@SuppressWarnings({"PMD.LawOfDemeter"})
public interface OrderDtoMapper {

    @Mapping(target = "formOrderNum", expression = "java(getFormOrderNum(dto))")
    @Mapping(target = "formStatusCode", expression = "java(getFormStatusCode(dto))")
    @Mapping(target = "formProductCategoryId", expression = "java(getFormProductCategoryId(dto))")
    @Mapping(target = "formCustomerType", expression = "java(getFormCustomerType(dto))")
    @Mapping(target = "formCustomerId", expression = "java(getFormCustomerId(dto))")
    @Mapping(target = "formCustomerCompanyId", expression = "java(getFormCustomerCompanyId(dto))")
    @Mapping(target = "formCustomerPersonId", expression = "java(getFormCustomerPersonId(dto))")
    @Mapping(target = "formCustomerCountryId", expression = "java(getFormCustomerCountryId(dto))")

    @Mapping(target = "formCustomerInn", expression = "java(getFormCustomerInn(dto))")
    @Mapping(target = "formCustomerLongName", expression = "java(getFormCustomerLongName(dto))")
    @Mapping(target = "formCustomerShortName", expression = "java(getFormCustomerShortName(dto))")
    FormOrderDto fromOrderToForm(OrderDto dto);

    default Long getFormOrderNum(final OrderDto dto) {
        return dto.getOrderNum();
    }

    default String getFormStatusCode(final OrderDto dto) {
        return dto.getStatus().getCode();
    }

    default Long getFormProductCategoryId(final OrderDto dto) {
        return dto.getProductCategory().getId();
    }

    default CustomerTypes getFormCustomerType(final OrderDto dto) {
        return dto.getCustomer().getType();
    }

    default Long getFormCustomerId(final OrderDto dto) {
        return dto.getCustomer().getId();
    }

    default Long getFormCustomerPersonId(final OrderDto dto) {
        return dto.getCustomer().getPerson() == null ? null : dto.getCustomer().getPerson().getId();
    }

    default Long getFormCustomerCompanyId(final OrderDto dto) {
        return dto.getCustomer().getCompany() == null ? null : dto.getCustomer().getCompany().getId();
    }

    default UUID getFormCustomerCountryId(final OrderDto dto) {
        return dto.getCustomer().getCountry().getId();
    }

    default String getFormCustomerInn(final OrderDto dto) {
        return dto.getCustomer().getInn() == null ? null : dto.getCustomer().getInn();
    }

    default String getFormCustomerLongName(final OrderDto dto) {
        return dto.getCustomer().getCompany() == null ? null : dto.getCustomer().getCompany().getLongName();
    }

    default String getFormCustomerShortName(final OrderDto dto) {
        return dto.getCustomer().getCompany() == null ? null : dto.getCustomer().getCompany().getShortName();
    }
}
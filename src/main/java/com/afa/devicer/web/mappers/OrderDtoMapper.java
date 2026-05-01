package com.afa.devicer.web.mappers;

import com.afa.core.dto.orders.OrderDto;
import com.afa.devicer.web.dto.orders.FormOrderDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
@SuppressWarnings({"PMD.LawOfDemeter"})
public interface OrderDtoMapper {

    @Mapping(target = "formOrderNum", expression = "java(getFormOrderNum(dto))")
    @Mapping(target = "formStatusCode", expression = "java(getFormStatusCode(dto))")
    @Mapping(target = "formProductCategoryId", expression = "java(getFormProductCategoryId(dto))")
    FormOrderDto fromOrderToForm(OrderDto dto);

    //OrderSaveRequest fromOrderToSaveRequest(OrderDto dto);

    default Long getFormOrderNum(final OrderDto dto) {
        return dto.getOrderNum();
    }

    default String getFormStatusCode(final OrderDto dto) {
        return dto.getStatus().getCode();
    }

    default Long getFormProductCategoryId(final OrderDto dto) {
        return dto.getProductCategory().getId();
    }

}
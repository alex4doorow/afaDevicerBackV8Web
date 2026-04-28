package com.afa.devicer.web.mappers;

import com.afa.core.dto.orders.OrderDto;
import com.afa.devicer.web.dto.FormOrderDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
@SuppressWarnings({"PMD.LawOfDemeter"})
public interface OrderDtoMapper {

    @Mapping(target = "formStatusCode", expression = "java(getFormStatusCode(dto))")
    FormOrderDto fromOrder(OrderDto dto);

    default String getFormStatusCode(final OrderDto dto) {
        return dto.getStatus().getCode();
    }
}
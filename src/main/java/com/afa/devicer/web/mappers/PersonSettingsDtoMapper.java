package com.afa.devicer.web.mappers;

import com.afa.core.dto.people.PersonSettingsDto;
import com.afa.core.enums.OrderStatusTypes;
import com.afa.devicer.web.dto.people.FormPersonSettingsDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
@SuppressWarnings({"PMD.LawOfDemeter"})
public interface PersonSettingsDtoMapper {

    @Mapping(target = "formStartDate", expression = "java(getStartDate(dto))")
    @Mapping(target = "formEndDate", expression = "java(getEndDate(dto))")
    @Mapping(target = "formOrderStatuses", expression = "java(getOrderStatuses(dto))")
    FormPersonSettingsDto fromPersonSettings(PersonSettingsDto dto);

    default LocalDate getStartDate(final PersonSettingsDto dto) {
        return dto.getOrders().getPeriod().getFirst();
    }

    default LocalDate getEndDate(final PersonSettingsDto dto) {
        return dto.getOrders().getPeriod().getSecond();
    }

    default List<String> getOrderStatuses(final PersonSettingsDto dto) {
        if (dto.getOrders() == null || dto.getOrders().getStatuses() == null) {
            return Collections.emptyList();
        }
        return dto.getOrders().getStatuses().stream().map(OrderStatusTypes::getCode).toList();
    }
}

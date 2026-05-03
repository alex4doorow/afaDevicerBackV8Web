package com.afa.devicer.web.dto.people;

import com.afa.core.dto.customers.CustomerConditionsDto;
import com.afa.core.dto.people.PersonSettingsDto;
import com.afa.core.dto.products.ProductConditionsDto;
import com.afa.core.enums.OrderStatusTypes;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormPersonSettingsDto extends PersonSettingsDto {

    @NotNull(message = "{order.conditions.form.fields.invalidFeedback.period.start}")
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate formStartDate;

    @NotNull(message = "{order.conditions.form.fields.invalidFeedback.period.end}")
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate formEndDate;

    private List<String> formOrderStatuses = new ArrayList<>();

    public void convertForm() {
        if (getOrders().getProductConditions() == null) {
            getOrders().setProductConditions(new ProductConditionsDto());
        }
        if (getOrders().getCustomerConditions() == null) {
            getOrders().setCustomerConditions(new CustomerConditionsDto());
        }
        // orders
        if (formStartDate != null && formEndDate != null) {
            getOrders().setPeriod(Pair.of(this.formStartDate, this.formEndDate));
        } else {
            getOrders().setPeriod(null);
        }
        final Set<OrderStatusTypes> orderStatuses = formOrderStatuses.stream()
                .map(OrderStatusTypes::valueOf)
                .collect(Collectors.toSet());
        getOrders().setStatuses(orderStatuses);
    }
}

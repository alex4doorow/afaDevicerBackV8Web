package com.afa.devicer.web.dto.orders;

import com.afa.core.dto.companies.CompanyDto;
import com.afa.core.dto.customers.CustomerDto;
import com.afa.core.dto.dictionaries.CountryDto;
import com.afa.core.dto.dictionaries.OrderStatusTypeDto;
import com.afa.core.dto.orders.OrderDto;
import com.afa.core.dto.people.PersonFullDto;
import com.afa.core.dto.products.ProductCategoryDto;
import com.afa.core.enums.CustomerTypes;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormOrderDto extends OrderDto {

    @NotNull(message = "{order.form.fields.invalidFeedback.no}")
    private Long formOrderNum;

    // order
    private String formStatusCode;
    private Long formProductCategoryId;

    // customer
    private Long formCustomerId;
    private Long formCustomerPersonId;
    private Long formCustomerCompanyId;
    private CustomerTypes formCustomerType;
    private UUID formCustomerCountryId;
    private String formCustomerInn;
    private String formCustomerShortName;
    private String formCustomerLongName;

    // delivery
    private boolean formDeliveryCustomerEqualsRecipient = true;

    public void convertForm() {
        // customer
        if (getCustomer() == null) {
            this.setCustomer(CustomerDto.builder()
                    .id(formCustomerId)
                    .type(formCustomerType)
                    .build());
        }
        if (formCustomerType == CustomerTypes.COMPANY) {
            this.getCustomer().setCompany(CompanyDto.builder()
                    .id(formCustomerCompanyId)
                    .longName(formCustomerLongName)
                    .shortName(formCustomerShortName)
                    .country(CountryDto.builder()
                            .id(formCustomerCountryId)
                            .build())
                    .build());
        } else if (formCustomerType == CustomerTypes.PERSON) {
            this.getCustomer().setPerson(PersonFullDto.builder()
                    .id(formCustomerPersonId)
                    .country(CountryDto.builder()
                            .id(formCustomerCountryId)
                            .build())
                    .build());
        }
        // order
        this.setOrderNum(formOrderNum);
        this.setStatus(OrderStatusTypeDto.builder()
                .code(formStatusCode)
                .build());
        this.setProductCategory(ProductCategoryDto.builder()
                .id(formProductCategoryId)
                .build());
        // delivery
        if (getDelivery().getRecipient().getFirstName() == null) {
            getDelivery().getRecipient().setFirstName(".");
        }
        if (getDelivery().getRecipient().getPhoneNumber() == null) {
            getDelivery().getRecipient().setPhoneNumber(".");
        }
    }
}

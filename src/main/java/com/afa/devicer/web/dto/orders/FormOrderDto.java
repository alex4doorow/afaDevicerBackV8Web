package com.afa.devicer.web.dto.orders;

import com.afa.core.dto.companies.CompanyDto;
import com.afa.core.dto.customers.CustomerContactDto;
import com.afa.core.dto.customers.CustomerDto;
import com.afa.core.dto.dictionaries.CountryDto;
import com.afa.core.dto.dictionaries.OrderStatusTypeDto;
import com.afa.core.dto.orders.OrderDto;
import com.afa.core.dto.people.PersonFullDto;
import com.afa.core.dto.products.ProductCategoryDto;
import com.afa.core.enums.AmountTypes;
import com.afa.core.enums.ContactTypes;
import com.afa.core.enums.CustomerTypes;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormOrderDto extends OrderDto {

    // customer
    private Long formCustomerId;
    private Long formCustomerPersonId;
    private Long formCustomerCompanyId;
    private CustomerTypes formCustomerType;
    private UUID formCustomerCountryId;
    private String formCustomerInn;
    private String formCustomerShortName;
    private String formCustomerLongName;

    // customer contact
    private String formCustomerContactPersonFirstName;
    private String formCustomerContactPersonMiddleName;
    private String formCustomerContactPersonLastName;
    private String formCustomerContactPersonPhoneNumber;
    private String formCustomerContactPersonEmail;

    @NotNull(message = "{order.form.fields.invalidFeedback.no}")
    private Long formOrderNum;

    // order
    private String formStatusCode;
    private Long formProductCategoryId;

    // amounts
    private BigDecimal formPostpayAmount;

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
            final PersonFullDto person = PersonFullDto.builder()
                    .firstName(formCustomerContactPersonFirstName)
                    .middleName(formCustomerContactPersonMiddleName)
                    .lastName(formCustomerContactPersonLastName)
                    .phoneNumber(formCustomerContactPersonPhoneNumber)
                    .email(formCustomerContactPersonEmail)
                    .build();
            final CustomerContactDto customerContactDto = CustomerContactDto.builder()
                    .type(ContactTypes.MAIN)
                    .person(person)
                    .build();
            this.getCustomer().setCompany(CompanyDto.builder()
                    .id(formCustomerCompanyId)
                    .longName(formCustomerLongName)
                    .shortName(formCustomerShortName)
                    .country(CountryDto.builder()
                            .id(formCustomerCountryId)
                            .build())
                    .build());
            this.getCustomer().setContacts(Set.of(customerContactDto));

        } else if (formCustomerType == CustomerTypes.PERSON) {
            final PersonFullDto person = PersonFullDto.builder()
                    .id(formCustomerPersonId)
                    .country(CountryDto.builder()
                            .id(formCustomerCountryId)
                            .build())
                    .build();
            this.getCustomer().setPerson(person);
            final CustomerContactDto customerContactDto = CustomerContactDto.builder()
                    .type(ContactTypes.MAIN)
                    .person(person)
                    .build();
            this.getCustomer().setContacts(Set.of(customerContactDto));
        }
        // order
        this.setOrderNum(formOrderNum);
        this.setStatus(OrderStatusTypeDto.builder()
                .code(formStatusCode)
                .build());
        this.setProductCategory(ProductCategoryDto.builder()
                .id(formProductCategoryId)
                .build());

        // amounts
        //getAmounts().put(AmountTypes.POSTPAY, formPostpayAmount);
    }
}

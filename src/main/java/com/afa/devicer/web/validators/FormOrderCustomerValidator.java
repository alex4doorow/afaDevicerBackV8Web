package com.afa.devicer.web.validators;

import com.afa.core.enums.CustomerTypes;
import com.afa.devicer.web.dto.orders.FormOrderDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.LawOfDemeter", "PMD.AvoidDuplicateLiterals"})
public class FormOrderCustomerValidator implements ConstraintValidator<ValidFormOrderCustomer, FormOrderDto> {

    @Override
    public boolean isValid(final FormOrderDto form, final ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }

        if (form.getFormCustomerType() != CustomerTypes.PERSON
                && form.getFormCustomerType() != CustomerTypes.FOREIGNER_PERSON) {
            return true;
        }

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        if (isBlank(form.getFormCustomerContactPersonFirstName())) {
            addViolation(context, "formCustomerContactPersonFirstName", "не должно быть пустым");
            valid = false;
        } else if (form.getFormCustomerContactPersonFirstName().length() < 2
                || form.getFormCustomerContactPersonFirstName().length() > 64) {
            addViolation(context, "formCustomerContactPersonFirstName", "размер должен находиться в диапазоне от 2 до 64");
            valid = false;
        }

        if (isBlank(form.getFormCustomerContactPersonLastName())) {
            addViolation(context, "formCustomerContactPersonLastName", "не должно быть пустым");
            valid = false;
        } else if (form.getFormCustomerContactPersonLastName().length() < 2
                || form.getFormCustomerContactPersonLastName().length() > 64) {
            addViolation(context, "formCustomerContactPersonLastName", "размер должен находиться в диапазоне от 2 до 64");
            valid = false;
        }

        if (isBlank(form.getFormCustomerContactPersonPhoneNumber())) {
            addViolation(context, "formCustomerContactPersonPhoneNumber", "не должно быть пустым");
            valid = false;
        }

        if (form.getFormCustomerCountryId() == null) {
            addViolation(context, "formCustomerCountryId", "не должно быть пустым");
            valid = false;
        }

        return valid;
    }

    private static boolean isBlank(final String value) {
        return value == null || value.trim().isEmpty();
    }

    private static void addViolation(
            final ConstraintValidatorContext context,
            final String field,
            final String message
    ) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }
}

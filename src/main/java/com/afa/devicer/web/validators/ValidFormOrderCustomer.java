package com.afa.devicer.web.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FormOrderCustomerValidator.class)
public @interface ValidFormOrderCustomer {

    String message() default "Ошибка валидации покупателя";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package com.afa.devicer.web.enums;

import lombok.Getter;

@Getter
@SuppressWarnings("PMD.ExcessivePublicCount")
public enum WebDevicerErrors {
    CUSTOMER_SAVE_ERROR("Ошибка при сохранении покупателя"),
    ORDER_SAVE_ERROR("Ошибка при сохранении заказа"),
    ORDER_DELETE_ERROR("Ошибка при удалении заказа"),
    DELIVERY_CALC_ERROR("Ошибка расчета стоимости доставки"),
    ;

    private final String errorMessage;

    WebDevicerErrors(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCode() {
        return name();
    }
}

package com.afa.devicer.web.enums;

import lombok.Getter;

@Getter
@SuppressWarnings("PMD.ExcessivePublicCount")
public enum WebDevicerErrors {
    ORDER_SAVE_ERROR("Ошибка при сохранении заказа")
    ;

    private final String errorMessage;

    WebDevicerErrors(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCode() {
        return name();
    }
}

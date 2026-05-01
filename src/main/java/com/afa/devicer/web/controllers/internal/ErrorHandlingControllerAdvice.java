package com.afa.devicer.web.controllers.internal;

import com.afa.core.exceptions.DevicerException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
@NoArgsConstructor
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(DevicerException.class)
    public String handleDevicerException(final DevicerException e, final Model model) {
        log.error("Devicer error: {}", e.getErrorCode(), e);
        model.addAttribute("status", 500);
        model.addAttribute("title", "Ошибка обработки запроса");
        model.addAttribute("message", e.getErrorMessage());
        model.addAttribute("errorCode", e.getErrorCode());
        model.addAttribute("violations", e.getViolations());
        return "error/500";
    }
}

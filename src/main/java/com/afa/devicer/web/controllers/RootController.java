package com.afa.devicer.web.controllers;

import com.afa.devicer.web.controllers.internal.ControllerConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.ROOT)
@Tag(name = "root", description = "Root controller")
@Controller
public class RootController extends BaseController{

    @GetMapping()
    public String root(final Model model) {

        populateDefaultModel(model);
        model.addAttribute("customPageTitle", getMessageSource().getMessage("index.header", null,
                LocaleContextHolder.getLocale()));
        return "index.html";
    }

    @Override
    protected void setActiveMenu(final Model model) {
        model.addAttribute("activeMenu", "home");
    }
}

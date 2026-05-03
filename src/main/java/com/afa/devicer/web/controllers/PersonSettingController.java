package com.afa.devicer.web.controllers;

import com.afa.core.dto.people.PersonSettingsResponse;
import com.afa.devicer.web.controllers.internal.ControllerConstants;
import com.afa.devicer.web.dto.people.FormPersonSettingsDto;
import com.afa.devicer.web.mappers.PersonSettingsDtoMapper;
import com.afa.devicer.web.services.PersonSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RequestMapping(ControllerConstants.PERSON_SETTINGS)
@Controller
@RequiredArgsConstructor
@SuppressWarnings({"PMD"})
public class PersonSettingController extends BaseController {

    private final PersonSettingsDtoMapper personSettingsDtoMapper;
    private final PersonSettingService personSettingService;

    @GetMapping("/conditions/order-list/filter")
    public String getConditions4OrderList(final Model model) {

        final PersonSettingsResponse settings = personSettingService.loadSettings();
        final FormPersonSettingsDto form = personSettingsDtoMapper.fromPersonSettings(settings.getSettings());

        populateDefaultModel(model);
        model.addAttribute("activeMenu", "orders");
        model.addAttribute("formSettings", form);
        return "people/orderConditionForm.html";
    }

    @PostMapping("/conditions/order-list/filter")
    public String saveConditions4OrderList(
            @ModelAttribute("formSettings") @Validated FormPersonSettingsDto form,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            populateDefaultModel(model);
            model.addAttribute("activeMenu", "orders");
            model.addAttribute("formSettings", form);
            return "people/orderConditionForm.html";
        }
        personSettingService.saveSettingsByOrderConditions(form);
        return "redirect:/web/orders";
    }
}

package com.afa.devicer.web.controllers;

import com.afa.devicer.web.controllers.internal.ControllerConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.INDEX)
@Tag(name = "home", description = "Home controller")
@Controller
public class IndexController {

    @GetMapping("/")
    public String index(final Model model) {

        model.addAttribute("customPageTitle", "Главная");
        return "index.html";
    }
}

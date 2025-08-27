package com.afa.devicer.web.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Slf4j
@CrossOrigin
@RequiredArgsConstructor
@Controller
@SuppressWarnings({"PMD.LawOfDemeter"})
public class LoginController {

    @Value("${keycloak.login.url}")
    private String keycloakLoginUrl;

    @Value("${keycloak.logout.url}")
    private String keycloakLogoutUrl;

    @GetMapping("/login")
    public void login(final HttpServletResponse response) throws IOException {
        response.sendRedirect(keycloakLoginUrl);
    }

    @GetMapping("/logout")
    public void logout(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        request.getSession().invalidate();
        response.sendRedirect(keycloakLogoutUrl);
    }
}
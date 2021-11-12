package com.sephrael.issuetrackingsystem.controller;

import com.sephrael.issuetrackingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Controller
public class WebController {
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/index.html", method = RequestMethod.GET)
    public String index(Model model, Principal principal) {
        model.addAttribute("currentUserProjects", userRepository.findByEmail(principal.getName()).getProjects());

        return "old-index";
    }

    @RequestMapping(value = "/auth-password-social.html", method = RequestMethod.GET)
    public String authPasswordSocial() {return "reset-password";}

    @RequestMapping(value = "/auth-redirect.html", method = RequestMethod.GET)
    public String authRedirect() {return "auth-redirect";}

    @RequestMapping(value = "/multi-tenant-add-users.html", method = RequestMethod.GET)
    public String multiTenantAddUsers() {return "multi-tenant-add-users";}

    @RequestMapping(value = "/user-management-add-user.html", method = RequestMethod.GET)
    public String userManagementAddUser(Model model, Principal principal) {
        model.addAttribute("currentUserProjects", userRepository.findByEmail(principal.getName()).getProjects());

        return "user-management-add-user";
    }
}

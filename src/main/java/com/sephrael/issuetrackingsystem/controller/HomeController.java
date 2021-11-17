package com.sephrael.issuetrackingsystem.controller;

import com.sephrael.issuetrackingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Controller
public class HomeController {
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("currentUser", userRepository.findByEmail(principal.getName()));
        model.addAttribute("currentUserProjects", userRepository.findByEmail(principal.getName()).getProjects());

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
        return "index";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {return "landing1";}
}

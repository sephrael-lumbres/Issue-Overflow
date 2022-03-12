package com.sephrael.issueoverflow.controller;

import com.sephrael.issueoverflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class HelpController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/getting-started")
    public String showGettingStartedCategory(Model model, Principal principal) {
        if(principal != null)
            model.addAttribute("currentUser", userRepository.findByEmail(principal.getName()));

        return "/knowledge-base/getting-started/getting-started-category";
    }

    @RequestMapping("/getting-started/project-manager")
    public String showGettingStartedPageForProjectManager(Model model, Principal principal) {
        if(principal != null)
            model.addAttribute("currentUser", userRepository.findByEmail(principal.getName()));

        return "/knowledge-base/getting-started/articles/project-manager-guide";
    }

    @RequestMapping("/getting-started/user")
    public String showGettingStartedForUserPage(Model model, Principal principal) {
        if(principal != null)
            model.addAttribute("currentUser", userRepository.findByEmail(principal.getName()));

        return "/knowledge-base/getting-started/articles/user-guide";
    }

    @RequestMapping("/contact-us")
    public String showContactUsPage(Model model, Principal principal) {
        if(principal != null)
            model.addAttribute("currentUser", userRepository.findByEmail(principal.getName()));

        return "/help/contact-us";
    }
}

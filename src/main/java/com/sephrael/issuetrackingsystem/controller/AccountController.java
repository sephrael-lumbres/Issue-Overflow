package com.sephrael.issuetrackingsystem.controller;

import com.sephrael.issuetrackingsystem.entity.File;
import com.sephrael.issuetrackingsystem.entity.User;
import com.sephrael.issuetrackingsystem.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/account")
public class AccountController {
    private final UserRepository userRepository;

    public AccountController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping("/profile/{id}")
    public String showAccountProfilePage(@PathVariable("id") long id, Principal principal, Model model) {
        User currentUser = userRepository.findByEmail(principal.getName());

        model.addAttribute("user", userRepository.getById(id));
        model.addAttribute("file", new File());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUserProjects", currentUser.getProjects());


        if(userRepository.findByEmail(principal.getName()).getOrganization() == null)
            return "/organization/select-organization";
        if(currentUser != userRepository.findUserById(id))
            return "/error/403";

        return "/account-settings/account-profile";
    }

    @PostMapping("/profile/{id}/save")
    public String saveAccountProfileChanges(@PathVariable("id") long id, User user) {

        User userToBeUpdated = userRepository.findUserById(id);

        userToBeUpdated.setFirstName(user.getFirstName());
        userToBeUpdated.setLastName(user.getLastName());
        userToBeUpdated.setEmail(user.getEmail());

        userRepository.save(userToBeUpdated);

        return "redirect:/account/profile/" + id;
    }

    @RequestMapping("/security/{id}")
    public String showAccountSecurityPage(@PathVariable("id") long id, Principal principal, Model model) {
        User currentUser = userRepository.findByEmail(principal.getName());

        model.addAttribute("user", userRepository.getById(id));
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUserProjects", currentUser.getProjects());

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null)
            return "/organization/select-organization";
        if(currentUser != userRepository.findUserById(id))
            return "/error/403";

        return "/account-settings/account-security";
    }

    @RequestMapping("/notifications/{id}")
    public String showAccountNotificationsPage(@PathVariable("id") long id, Principal principal, Model model) {
        User currentUser = userRepository.findByEmail(principal.getName());

        model.addAttribute("user", userRepository.getById(id));
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUserProjects", currentUser.getProjects());

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null)
            return "/organization/select-organization";
        if(currentUser != userRepository.findUserById(id))
            return "/error/403";

        return "/account-settings/account-notifications";
    }
}

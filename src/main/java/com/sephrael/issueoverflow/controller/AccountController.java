package com.sephrael.issueoverflow.controller;

import com.sephrael.issueoverflow.entity.File;
import com.sephrael.issueoverflow.entity.Organization;
import com.sephrael.issueoverflow.entity.Project;
import com.sephrael.issueoverflow.entity.User;
import com.sephrael.issueoverflow.repository.OrganizationRepository;
import com.sephrael.issueoverflow.repository.UserRepository;
import com.sephrael.issueoverflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Objects;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;

    @RequestMapping("/profile/{id}")
    public String showAccountProfilePage(@PathVariable("id") long id, Principal principal, Model model) {
        User currentUser = userRepository.findByEmail(principal.getName());

        if(currentUser.getOrganization() == null)
            return "organization/select-organization";

        // checks if the the requested Account Profile page matches the Current User
        if(currentUser.getId() != id)
            return "error/404";

        model.addAttribute("user", userRepository.getById(id));
        model.addAttribute("file", new File());
        model.addAttribute("newProject", new Project());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUserProjects", currentUser.getProjects());

        return "account-settings/account-profile";
    }

    @PostMapping("/profile/{id}/save")
    public String saveAccountProfileChanges(@PathVariable("id") long id, User user, Principal principal,
                                            RedirectAttributes redirectAttributes) {
        User currentUser = userRepository.findByEmail(principal.getName());

        if(currentUser.getOrganization() == null)
            return "organization/select-organization";

        if(currentUser.getId() != id)
            return "error/404";

        try {
            User userToBeUpdated = userRepository.findUserById(id);
            String previousEmail = userToBeUpdated.getEmail();

            userToBeUpdated.setFirstName(user.getFirstName());
            userToBeUpdated.setLastName(user.getLastName());
            userToBeUpdated.setEmail(user.getEmail());

            userRepository.save(userToBeUpdated);

            if(!Objects.equals(previousEmail, user.getEmail())) {
                redirectAttributes.addFlashAttribute("emailChangeSuccess", "Email address has been successfully updated. Please log in with your new email address.");
                SecurityContextHolder.getContext().setAuthentication(null);
                return "redirect:/login";
            }
            redirectAttributes.addFlashAttribute("profileChangeSuccess", "Account Profile changes have been saved");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("profileChangeError", "An error has occurred while attempting to save Account Profile Changes");;
        }

        return "redirect:/account/profile/" + id;
    }

    @RequestMapping("/security/{id}")
    public String showAccountSecurityPage(@PathVariable("id") long id, Principal principal, Model model) {
        User currentUser = userRepository.findByEmail(principal.getName());

        if(currentUser.getOrganization() == null)
            return "organization/select-organization";

        if(currentUser.getId() != id)
            return "error/404";

        model.addAttribute("newProject", new Project());
        model.addAttribute("user", userRepository.getById(id));
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUserProjects", currentUser.getProjects());

        return "account-settings/account-security";
    }

    @PostMapping("/security/{id}/save")
    public String saveAccountSecurityChanges(@PathVariable("id") long id, RedirectAttributes redirectAttributes,
                                             @RequestParam("currentPassword") String currentPassword,
                                             @RequestParam("newPassword") String newPassword,
                                             @RequestParam("confirmPassword") String confirmPassword, Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName());

        if(currentUser.getOrganization() == null)
            return "organization/select-organization";

        if(currentUser.getId() != id)
            return "error/404";

        User userToBeUpdated = userRepository.findUserById(id);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // checks if the Current Password actually matches the User's current password and if the new passwords match
        if(passwordEncoder.matches(currentPassword, userToBeUpdated.getPassword()) && Objects.equals(newPassword, confirmPassword)) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            userToBeUpdated.setPassword(encodedPassword);

            userRepository.save(userToBeUpdated);
            // popup alerts are displayed accordingly
            redirectAttributes.addFlashAttribute("passwordChangeSuccess", "Your password has been successfully changed!");
        } else if(!passwordEncoder.matches(currentPassword, userToBeUpdated.getPassword())) {
            redirectAttributes.addFlashAttribute("incorrectOldPassword", "Your current password does not match!");
        } else if(!Objects.equals(newPassword, confirmPassword)) {
            redirectAttributes.addFlashAttribute("passwordsDoNotMatch", "Passwords do not match. Please try again.");
        } else {
            redirectAttributes.addFlashAttribute("error", "An error has occurred. Please try again.");
        }

        return "redirect:/account/security/" + id;
    }

    @PostMapping("/security/{id}/delete/{organizationId}")
    public String deleteAccount(@PathVariable(name = "id") long id, Principal principal, RedirectAttributes redirectAttributes,
                                @RequestParam(value = "password", required = false) String password,
                                @PathVariable("organizationId") long organizationId) {
        User currentUser = userRepository.findByEmail(principal.getName());
        Organization currentOrganization = organizationRepository.findOrganizationById(organizationId);

        if(currentOrganization == null)
            return "organization/select-organization";

        if(currentUser.getId() != id)
            return "error/404";

        User userToBeDeleted = userRepository.findUserById(id);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // checks if the Password actually matches the User's current password
        if(passwordEncoder.matches(password, userToBeDeleted.getPassword())) {
            // unassigns a User from all Issues that they were previously assigned to
            userService.unassignAllIssuesBeforeUserDeletion(userToBeDeleted);

            // if a User is involved with any Projects, this removes them from all those Projects before deleting the User
            if(userToBeDeleted.getProjects() != null) {
                userService.removeUserFromAllProjects(userToBeDeleted);
            }

            // removes User from Organization
            currentOrganization.removeUser(currentUser);

            // deletes organization if there are no more members
            if(currentOrganization.getUsers().size() == 0)
                organizationRepository.deleteById(currentOrganization.getId());

            userRepository.deleteById(id);
            // popup alerts are displayed accordingly
            redirectAttributes.addFlashAttribute("accountDeletionSuccess", "Your account has been successfully deleted!");
        } else {
            redirectAttributes.addFlashAttribute("accountDeletionFailed", "Your password does not match is on file!");
            return "redirect:/account/security/" + id;
        }

        return "redirect:/login";
    }

    // feature to be added in the future
//    @RequestMapping("/notifications/{id}")
//    public String showAccountNotificationsPage(@PathVariable("id") long id, Principal principal, Model model) {
//        User currentUser = userRepository.findByEmail(principal.getName());
//
//        if(currentUser.getOrganization() == null)
//            return "organization/select-organization";
//
//        if(currentUser.getId() != id)
//            return "error/404";
//
//        model.addAttribute("newProject", new Project());
//        model.addAttribute("user", userRepository.getById(id));
//        model.addAttribute("currentUser", currentUser);
//        model.addAttribute("currentUserProjects", currentUser.getProjects());
//
//        return "account-settings/account-notifications";
//    }
}

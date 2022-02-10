// file from https://www.codejava.net/frameworks/spring-boot/user-registration-and-login-tutorial

package com.sephrael.issuetrackingsystem.controller;

import com.sephrael.issuetrackingsystem.entity.Project;
import com.sephrael.issuetrackingsystem.entity.User;
import com.sephrael.issuetrackingsystem.repository.ProjectRepository;
import com.sephrael.issuetrackingsystem.repository.RoleRepository;
import com.sephrael.issuetrackingsystem.repository.UserRepository;
import com.sephrael.issuetrackingsystem.service.UserService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        return "register";
    }

    @PostMapping("/process_register")
    public String processRegister(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userService.registerDefaultUser(user);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");

        return modelAndView;
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm() {
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("email") String email, HttpServletRequest request, Model model) {
        String token = RandomString.make(30);

        try {
            if(userRepository.findByEmail(email) != null) {
                String siteURL = request.getRequestURL().toString();
                String resetPasswordLink = siteURL.replace(request.getServletPath(), "") + "/change-password?token=" + token;

                userService.updateResetPasswordToken(token, email);
                userService.sendPasswordResetEmail(email, resetPasswordLink);

                // creates a popup alert to let the User know that a registered account has been found and that an Email
                // has been sent that includes a link to Reset their Password
                model.addAttribute("isEmailSent", true);
            } else {
                // creates a popup alert to let the User know that the Email Address they entered is not associated
                // with any registered account
                model.addAttribute("isFound", false);
            }
        } catch (UnsupportedEncodingException | MessagingException exception) {
            model.addAttribute("error", "Error while sending email");
        }

        return "reset-password";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(@Param(value = "token") String token, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.getUserByResetPasswordToken(token);
        model.addAttribute("token", token);

        if(user == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid Token!");
            return "redirect:/login";
        }

        return "change-password";
    }

    @PostMapping("/change-password")
    public String processChangePassword(@RequestParam("password") String password, @RequestParam("confirmPassword") String confirmPassword,
                                        HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        String token = request.getParameter("token");

        User user = userService.getUserByResetPasswordToken(token);
        model.addAttribute("title", "Reset your password");

        if(user == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid Token!");
        } else if(!Objects.equals(password, confirmPassword)) {
            redirectAttributes.addFlashAttribute("passwordsDoNotMatch", "Passwords do not match. Please try again.");
            return "redirect:/change-password?token=" + token;
        } else {
            userService.updatePassword(user, password);
            redirectAttributes.addFlashAttribute("success", "You have successfully changed your password!");
        }

        return "redirect:/login";
    }

    // change this to only display 'Users' within the 'Organization'
    @GetMapping("/users")
    public String listUsers(Model model, Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName());

        if(currentUser.getOrganization() == null)
            return "/organization/select-organization";

        List<User> listUsers = userRepository.findAll();
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("newProject", new Project());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUserProjects", currentUser.getProjects());

        return "/users/users-by-organization";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserPage(@PathVariable("id") long id, Model model, Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName());

        if(currentUser.getOrganization() == null)
            return "/organization/select-organization";

        User user = userRepository.getById(id);

        // if the Current User is NOT in the same Organization as the Requested User, redirect to 404 page
        if(!currentUser.getOrganization().getUsers().contains(user)) {
            return "/error/404";
        }

        model.addAttribute("user", user);
        model.addAttribute("listRoles", userService.listRoles());
        model.addAttribute("currentUserProjects", currentUser.getProjects());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("editUser", userRepository.findUserById(id));
        model.addAttribute("allProjectsInOrganization", projectRepository.findAllProjectsByOrganizationId(userRepository.findUserById(id).getOrganization().getId()));

        return "/users/edit-user";
    }

    // Allows for updating a User's Role and Projects(only Project Managers are allowed to make these changes)
    @PostMapping(value = "/users/update/{id}")
    public String saveUserChanges(@PathVariable("id") Long id, @RequestParam("role") long roleId, Principal principal,
                                  @RequestParam(value = "projects", required = false) List<Project> projects,
                                  User userBeforeUpdate, RedirectAttributes redirectAttributes) {
        User currentUser = userRepository.findByEmail(principal.getName());

        if(currentUser.getOrganization() == null)
            return "/organization/select-organization";

        User userToBeUpdated = userRepository.findUserById(id);

        // if the Current User is NOT in the same Organization as the Requested User, redirect to 404 page
        if(!currentUser.getOrganization().getUsers().contains(userToBeUpdated)) {
            return "/error/404";
        }

        // changes a User's Role
        roleRepository.findRoleById(roleId).addToUser(userToBeUpdated);

        if(projects != null) {
            // decides whether or not to add or remove a User from a Project
            userBeforeUpdate.setOrganization(userToBeUpdated.getOrganization());
            userService.manageUserProjects(userToBeUpdated, userBeforeUpdate, projects);
        } else {
            // if all checkboxes for Projects are unchecked
            userService.removeUserFromAllProjects(userToBeUpdated);
        }

        userRepository.save(userToBeUpdated);
        redirectAttributes.addFlashAttribute("userUpdateSuccess", "Changes have been saved");

        return "redirect:/users";
    }

    @RequestMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") long id, Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName());

        if(currentUser.getOrganization() == null)
            return "/organization/select-organization";

        User userBeforeDeletion = userRepository.findUserById(id);

        // if the Current User is NOT in the same Organization as the Requested User, redirect to 404 page
        if(!currentUser.getOrganization().getUsers().contains(userBeforeDeletion)) {
            return "/error/404";
        }

        // unassigns a User from all Issues that they were previously assigned to
        userService.unassignAllIssuesBeforeUserDeletion(userBeforeDeletion);

        // if a User is involved with any Projects, this removes them from all those Projects before deleting the User
        if(userBeforeDeletion.getProjects() != null) {
            userService.removeUserFromAllProjects(userBeforeDeletion);
        }

        userRepository.deleteById(id);

        if(currentUser.getId() == id)
            return "redirect:/register";

        return "redirect:/users";
    }

    // this returns the json of all the users
    @GetMapping(path = "/users/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}

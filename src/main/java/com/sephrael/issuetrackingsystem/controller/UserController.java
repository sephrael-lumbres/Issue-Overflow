// file from https://www.codejava.net/frameworks/spring-boot/user-registration-and-login-tutorial

package com.sephrael.issuetrackingsystem.controller;

import com.sephrael.issuetrackingsystem.entity.User;
import com.sephrael.issuetrackingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

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

        userRepository.save(user);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");

        return modelAndView;
    }

    @GetMapping("/users")
    public String listUsers(Model model, Principal principal) {
        List<User> listUsers = userRepository.findAll();
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("user", userRepository.findByEmail(principal.getName()));

        return "users";
    }

    // DOES NOT WORK DUE TO USER PASSWORD
    @GetMapping("/edit/user/{id}")
    public String showEditUserPage(@PathVariable("id") long id, Model model) {
        User user = userRepository.getById(id);
        model.addAttribute("user", user);

        return "user-management-edit-user";
    }

    // DOES NOT WORK DUE TO USER PASSWORD
//    @RequestMapping(value = "/save_user_changes", method = RequestMethod.POST)
//    public String saveUserChanges(@ModelAttribute("user") User user) {
//        userRepository.save(user);
//
//        return "redirect:/users";
//    }

    @RequestMapping("/delete/user/{id}")
    public String deleteUser(@PathVariable(name = "id") long id) {
        userRepository.deleteById(id);

        return "redirect:/users";
    }

    // this returns the json of all the users
    @GetMapping(path = "/allUsers")
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}

package com.sephrael.issueoverflow.controller;

import com.sephrael.issueoverflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.Principal;

@Controller
public class HelpController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender;

    @RequestMapping("/getting-started")
    public String showGettingStartedCategory(Model model, Principal principal) {
        if(principal != null)
            model.addAttribute("currentUser", userRepository.findByEmail(principal.getName()));

        return "knowledge-base/getting-started/getting-started-category";
    }

    @RequestMapping("/getting-started/project-manager")
    public String showGettingStartedPageForProjectManager(Model model, Principal principal) {
        if(principal != null)
            model.addAttribute("currentUser", userRepository.findByEmail(principal.getName()));

        return "knowledge-base/getting-started/articles/project-manager-guide";
    }

    @RequestMapping("/getting-started/user")
    public String showGettingStartedForUserPage(Model model, Principal principal) {
        if(principal != null)
            model.addAttribute("currentUser", userRepository.findByEmail(principal.getName()));

        return "knowledge-base/getting-started/articles/user-guide";
    }

    @RequestMapping("/contact-us")
    public String showContactUsPage(Model model, Principal principal) {
        if(principal != null)
            model.addAttribute("currentUser", userRepository.findByEmail(principal.getName()));

        return "help/contact-us";
    }

    @PostMapping("/contact-us/send-message")
    public String sendMessage(@RequestParam("name") String name, @RequestParam("email") String email,
                              @RequestParam("message") String message) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        helper.setFrom("support@issueoverflow.app", "Issue Overflow");
        helper.setTo("support@issueoverflow.app");

        String subject = "Contact Us - Message from: " + name;

        helper.setSubject(subject);

        message = message.replaceAll("(\r\n|\n)", "<br>");
        message = message.concat("<br><br>From: " + name + " <br>Email address: " + email);

        helper.setText(message, true);

        mailSender.send(mimeMessage);

        return "redirect:/contact-us";
    }
}

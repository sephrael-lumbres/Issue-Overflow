package com.sephrael.issuetrackingsystem.controller;

import com.sephrael.issuetrackingsystem.entity.Organization;
import com.sephrael.issuetrackingsystem.entity.User;
import com.sephrael.issuetrackingsystem.repository.OrganizationRepository;
import com.sephrael.issuetrackingsystem.repository.RoleRepository;
import com.sephrael.issuetrackingsystem.repository.UserRepository;
import com.sephrael.issuetrackingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.Random;

@Controller
@RequestMapping("/organization")
public class OrganizationController {

    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;

    @RequestMapping("")
    public ModelAndView viewOrganizationDetails(Model model, Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/organization/organization");

        User currentUser = userRepository.findByEmail(principal.getName());
        String organizationName = currentUser.getOrganization().getName();

        // this allowed me to print the Organization that the currently logged-in user belongs to
        model.addAttribute("currentUser", currentUser);

        // this allowed me to access the Users of an Organization by User Roles
        model.addAttribute("projectManagers", userRepository.findByRoleNameAndOrganizationName("Project Manager", organizationName));
        model.addAttribute("admins", userRepository.findByRoleNameAndOrganizationName("Admin", organizationName));
        model.addAttribute("developers", userRepository.findByRoleNameAndOrganizationName("Developer", organizationName));
        model.addAttribute("members", userRepository.findByRoleNameAndOrganizationName("Member", organizationName));

        model.addAttribute("currentUserProjects", currentUser.getProjects());

        if(currentUser.getOrganization() == null) {
            modelAndView.setViewName("/organization/select-organization");
        }
        return modelAndView;
    }

    @RequestMapping("/select")
    public String showCreateOrJoinOrganizationPage() {
        return ("/organization/select-organization");
    }

    @RequestMapping("/new")
    public String showCreateOrganizationPage(Model model) {
        model.addAttribute("organization", new Organization());
        model.addAttribute("users", userService.listAll());

        return("/organization/create-organization");
    }

    public static String generateRandomAccessKey(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            stringBuilder.append(chars.charAt(random.nextInt(chars.length())));
        return stringBuilder.toString();
    }

    @PostMapping("/save")
    public String createOrganization(Organization organization, Principal principal) {
        String accessKey = generateRandomAccessKey(10);
        organization.setAccessKey(accessKey);

        // this connects the current user to the newly created Organization
        organization.addToUser(userRepository.findByEmail(principal.getName()));
        organizationRepository.save(organization);

        User currentUser = userRepository.findByEmail(principal.getName());

        roleRepository.findByName("Project Manager").addToUser(currentUser);
        userRepository.save(currentUser);

        return "redirect:/organization";
    }

    @PostMapping("/joining")
    public String joinOrganization(@RequestParam(value = "accessKey") String accessKey, Principal principal) {
        // this gets the access key input by the user from the "Join Organization Form" and uses the access key to find
        // the associated Organization and adds the current user to the desired Organization
        organizationRepository.findByAccessKey(accessKey).addToUser(userRepository.findByEmail(principal.getName()));

        // after adding the current user to the desired Organization, this saves the Organization
        organizationRepository.save(organizationRepository.findByAccessKey(accessKey));

        return "redirect:/organization";
    }

    @RequestMapping("/join")
    public String joinOrganization(Model model) {
        model.addAttribute("organization", organizationRepository.findAll());

        return("/organization/join-organization");
    }

    // this returns the json of all the organizations
    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    // feature request: be able to add users by email, to an organization
    // try to get "multi-tenant-add-users.html" to work
}

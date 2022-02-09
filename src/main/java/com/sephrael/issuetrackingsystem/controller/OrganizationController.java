package com.sephrael.issuetrackingsystem.controller;

import com.sephrael.issuetrackingsystem.entity.Organization;
import com.sephrael.issuetrackingsystem.entity.Project;
import com.sephrael.issuetrackingsystem.entity.User;
import com.sephrael.issuetrackingsystem.repository.OrganizationRepository;
import com.sephrael.issuetrackingsystem.repository.RoleRepository;
import com.sephrael.issuetrackingsystem.repository.UserRepository;
import com.sephrael.issuetrackingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String viewOrganizationDetails(Model model, Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName());
        Organization currentOrganization = currentUser.getOrganization();

        if(currentOrganization == null) {
            return "/organization/select-organization";
        }

        model.addAttribute("newProject", new Project());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUserProjects", currentUser.getProjects());

        // gets the Users of an Organization by each Role
        model.addAttribute("projectManagers", userRepository.findByOrganizationAndRole(currentOrganization, roleRepository.findByName("Project Manager")));
        model.addAttribute("admins", userRepository.findByOrganizationAndRole(currentOrganization, roleRepository.findByName("Admin")));
        model.addAttribute("developers", userRepository.findByOrganizationAndRole(currentOrganization, roleRepository.findByName("Developer")));
        model.addAttribute("guests", userRepository.findByOrganizationAndRole(currentOrganization, roleRepository.findByName("Guest")));

        return "/organization/organization-details";
    }

    @RequestMapping("/select")
    public String showCreateOrJoinOrganizationPage(Principal principal) {

        if(userRepository.findByEmail(principal.getName()).getOrganization() != null)
            return("redirect:/organization");
        else
            return ("/organization/select-organization");
    }

    @RequestMapping("/new")
    public String showCreateOrganizationPage(Model model, Principal principal) {
        if(userRepository.findByEmail(principal.getName()).getOrganization() != null)
            return("redirect:/organization");

        model.addAttribute("organization", new Organization());

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
        User currentUser = userRepository.findByEmail(principal.getName());
        String accessKey = generateRandomAccessKey(10);
        organization.setAccessKey(accessKey);

        // this connects the current user to the newly created Organization
        organization.addToUser(currentUser);
        organizationRepository.save(organization);

        roleRepository.findByName("Project Manager").addToUser(currentUser);
        userRepository.save(currentUser);

        // logs out the Current User to allow the Authorization of 'Project Manager'
        SecurityContextHolder.getContext().setAuthentication(null);

        return "redirect:/dashboard";
    }

    @PostMapping("/joining")
    public String joinOrganization(@RequestParam(value = "accessKey") String accessKey, Principal principal) {
        // this gets the access key input by the user from the "Join Organization Form" and uses the access key to find
        // the associated Organization and adds the current user to the desired Organization
        organizationRepository.findByAccessKey(accessKey).addToUser(userRepository.findByEmail(principal.getName()));

        // after adding the current user to the desired Organization, this saves the Organization
        organizationRepository.save(organizationRepository.findByAccessKey(accessKey));

        return "redirect:/dashboard";
    }

    @RequestMapping("/join")
    public String joinOrganization(Principal principal) {
        if(userRepository.findByEmail(principal.getName()).getOrganization() != null)
            return("redirect:/organization");

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

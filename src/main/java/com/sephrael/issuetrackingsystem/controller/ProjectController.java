package com.sephrael.issuetrackingsystem.controller;

import com.sephrael.issuetrackingsystem.entity.Organization;
import com.sephrael.issuetrackingsystem.entity.Project;
import com.sephrael.issuetrackingsystem.entity.User;
import com.sephrael.issuetrackingsystem.repository.OrganizationRepository;
import com.sephrael.issuetrackingsystem.repository.ProjectRepository;
import com.sephrael.issuetrackingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    @RequestMapping("")
    public String viewProjects(Model model, Principal principal) {
        model.addAttribute("project", new Project());
        model.addAttribute("currentUser", userRepository.findByEmail(principal.getName()));
        model.addAttribute("currentUserProjects", userRepository.findByEmail(principal.getName()).getProjects());

        return "/projects/projects";
    }

    @PostMapping("/new")
    public String createProject(Project project, Principal principal, Model model) {
        model.addAttribute("project", new Project());
        project.addUser(userRepository.findByEmail(principal.getName()));

//        userRepository.findByEmail(principal.getName()).addProject(project);

        // this adds the newly created project to the current user's Organization
        userRepository.findByEmail(principal.getName()).getOrganization().addToProject(project);

        projectRepository.save(project);

        return "redirect:/projects";
    }

    @PostMapping("/join")
    public String joinProject(@RequestParam(value = "accessKey") String accessKey, Principal principal) {
        Project currentProject = projectRepository.findByAccessKey(accessKey);
        User currentUser = userRepository.findByEmail(principal.getName());



        // this adds the project to the current user's Organization only if the current user is in the Organization that
        // the Project was created in
        Organization currentUserOrganization = currentUser.getOrganization();
        if(Objects.equals(currentUserOrganization.getId(), currentProject.getOrganization().getId())) {
            currentUserOrganization.addToProject(currentProject);

            // this gets the access key input by the user from the "Join Organization Form" and uses the access key to find
            // the associated Organization and adds the current user to the desired Project
            currentProject.addUser(currentUser);
        } else {
            System.out.println("Project Not Found in this Organization");
        }

        // OLD this adds the project to the current user's Organization without CHECKING
        //userRepository.findByEmail(principal.getName()).getOrganization().addToProject(projectRepository.findByAccessKey(accessKey));


        // after adding the current user to the desired Organization, this saves the Organization
        projectRepository.save(projectRepository.findByAccessKey(accessKey));

        return "redirect:/projects";
    }

    // this returns the json of all the projects
    @GetMapping(path = "/all")
    public @ResponseBody
    List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
}

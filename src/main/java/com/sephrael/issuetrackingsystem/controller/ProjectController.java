package com.sephrael.issuetrackingsystem.controller;

import com.sephrael.issuetrackingsystem.entity.Issue;
import com.sephrael.issuetrackingsystem.entity.Organization;
import com.sephrael.issuetrackingsystem.entity.Project;
import com.sephrael.issuetrackingsystem.entity.User;
import com.sephrael.issuetrackingsystem.repository.IssueRepository;
import com.sephrael.issuetrackingsystem.repository.ProjectRepository;
import com.sephrael.issuetrackingsystem.repository.UserRepository;
import com.sephrael.issuetrackingsystem.service.IssueService;
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
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private IssueService issueService;

    @RequestMapping("/all")
    public String viewProjects(Model model, Principal principal) {
        model.addAttribute("newProject", new Project());
        model.addAttribute("currentUser", userRepository.findByEmail(principal.getName()));
        model.addAttribute("currentUserProjects", userRepository.findByEmail(principal.getName()).getProjects());

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
        return "/projects/projects";
    }

    @RequestMapping("/{identifier}")
    public String projectHome(@PathVariable("identifier") String identifier, Model model, Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName());
        Project currentProject = projectRepository.findByIdentifier(identifier);

        List<Issue> sortedIssues = issueService.getIssuesSortedByRecentActivity(
                issueRepository.findByProject(currentProject),
                issueRepository.findByProjectOrderByDateCreatedDesc(currentProject),
                issueRepository.findByProjectOrderByDateUpdatedDesc(currentProject)
        );

        model.addAttribute("issues", sortedIssues);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("newProject", new Project());
        model.addAttribute("issueService", issueService);
        model.addAttribute("currentProject", currentProject);
        model.addAttribute("issueRepository", issueRepository);
        model.addAttribute("currentUserProjects", currentUser.getProjects());
        model.addAttribute("numberOfOpenIssues", issueService.getNumberOfIssuesByProjectAndStatus("Open", identifier));
        model.addAttribute("numberOfClosedIssues", issueService.getNumberOfIssuesByProjectAndStatus("Closed", identifier));
        model.addAttribute("numberOfResolvedIssues", issueService.getNumberOfIssuesByProjectAndStatus("Resolved", identifier));
        model.addAttribute("numberOfInProgressIssues", issueService.getNumberOfIssuesByProjectAndStatus("In-Progress", identifier));

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
        return "/projects/project-home";
    }

    @PostMapping("/new")
    public String createProject(Project project, Principal principal, Model model) {
        model.addAttribute("project", new Project());
        project.addUser(userRepository.findByEmail(principal.getName()));

//        userRepository.findByEmail(principal.getName()).addProject(project);

        // this adds the newly created project to the current user's Organization
        userRepository.findByEmail(principal.getName()).getOrganization().addToProject(project);

        projectRepository.save(project);

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
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
        //userRepository.findByEmail(principal.getName()).getOrganization().addToProject(projectRepository.findByIdentifier(identifier));


        // after adding the current user to the desired Organization, this saves the Organization
        projectRepository.save(projectRepository.findByAccessKey(accessKey));

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
        return "redirect:/projects";
    }

    @PostMapping("/save")
    public String editProject(@RequestParam(value = "id") Long id, Project project, Principal principal) {

        Project currentProject = projectRepository.findProjectById(id);

        // fixes bug that when updating a Project removed all the Users in an updated Project except for the User that
        // performed the update
        // had to use 'currentProject' because I needed to access a Project's Users before the update was performed
        for (User user : currentProject.getUsers()) {
            project.addUser(user);
        }

        projectRepository.save(project);

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
        return "redirect:/projects";
    }

    @RequestMapping("/delete/{id}")
    public String deleteProject(@PathVariable(name = "id") Long id, Principal principal) {
        projectRepository.deleteById(id);

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
        return "redirect:/projects";
    }

    // this returns the json of all the projects
    @GetMapping(path = "/json")
    public @ResponseBody
    List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
}

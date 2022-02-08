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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.temporal.ChronoUnit;
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
        User currentUser = userRepository.findByEmail(principal.getName());

        if(currentUser.getOrganization() == null)
            return "/organization/select-organization";

        model.addAttribute("newProject", new Project());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUserProjects", currentUser.getProjects());

        return "/projects/projects-list";
    }

    @RequestMapping("/{identifier}")
    public String projectHome(@PathVariable("identifier") String identifier, Model model, Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName());
        Organization currentOrganization = currentUser.getOrganization();

        if(currentOrganization == null)
            return "/organization/select-organization";

        Project currentProject = projectRepository.findByIdentifierAndOrganization(identifier, currentOrganization);

        List<Issue> sortedIssues = issueService.getIssuesSortedByRecentActivity(
                issueRepository.findByProject(currentProject),
                issueRepository.findByProjectOrderByDateCreatedDesc(currentProject),
                issueRepository.findByProjectOrderByDateUpdatedDesc(currentProject)
        );

        model.addAttribute("issues", sortedIssues);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("newProject", new Project());
        model.addAttribute("issueService", issueService);
        model.addAttribute("seconds", ChronoUnit.SECONDS);
        model.addAttribute("currentProject", currentProject);
        model.addAttribute("issueRepository", issueRepository);
        model.addAttribute("currentUserProjects", currentUser.getProjects());
        model.addAttribute("numberOfOpenIssues", issueService.getNumberOfIssuesByProjectAndStatus("Open", identifier, currentOrganization));
        model.addAttribute("numberOfClosedIssues", issueService.getNumberOfIssuesByProjectAndStatus("Closed", identifier, currentOrganization));
        model.addAttribute("numberOfResolvedIssues", issueService.getNumberOfIssuesByProjectAndStatus("Resolved", identifier, currentOrganization));
        model.addAttribute("numberOfInProgressIssues", issueService.getNumberOfIssuesByProjectAndStatus("In-Progress", identifier, currentOrganization));

        return "/projects/project-home";
    }

    @PostMapping("/new")
    public String createProject(Project newProject, Principal principal, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = userRepository.findByEmail(principal.getName());
        Organization currentUserOrganization = currentUser.getOrganization();

        if(currentUserOrganization == null)
            return "/organization/select-organization";

        model.addAttribute("project", new Project());

        // if the new Project's Identifier or Access Key already exists within the current User's Organization's Projects,
        // display an ERROR message
        for(Project currentProject : currentUserOrganization.getProjects()) {
            if(Objects.equals(newProject.getIdentifier(), currentProject.getIdentifier())) {
                // popup alerts are displayed accordingly
                redirectAttributes.addFlashAttribute("idNotUniqueToOrganization", "Error creating project! Project 'ID' already exists within Organization!");

                return "redirect:/projects/all";
            } else if(Objects.equals(newProject.getAccessKey(), currentProject.getAccessKey())) {
                redirectAttributes.addFlashAttribute("accessKeyNotUniqueToOrganization", "Error creating project! Project 'Access Key' already exists within Organization!");

                return "redirect:/projects/all";
            }
        }

        newProject.addUser(currentUser);

        // this adds the newly created project to the current user's Organization
        currentUserOrganization.addToProject(newProject);

        projectRepository.save(newProject);
        redirectAttributes.addFlashAttribute("projectCreationSuccess", "Project '" + newProject.getName() + "'" + " has been successfully created!");

        return "redirect:/projects/all";
    }

    @PostMapping("/join")
    public String joinProject(@RequestParam(value = "accessKey") String accessKey, Principal principal, RedirectAttributes redirectAttributes) {
        User currentUser = userRepository.findByEmail(principal.getName());
        Organization currentUserOrganization = currentUser.getOrganization();

        if(currentUserOrganization == null)
            return "/organization/select-organization";

        Project currentProject = projectRepository.findByAccessKeyAndOrganization(accessKey, currentUserOrganization);

        if(currentProject == null) {
            redirectAttributes.addFlashAttribute("projectNotFound", "Project Not Found!");
            return "redirect:/projects/all";
        }

        // this adds the project to the current user's Organization only if the current user is in the Organization that
        // the Project was created in
        if(Objects.equals(currentUserOrganization.getId(), currentProject.getOrganization().getId())) {
            currentUserOrganization.addToProject(currentProject);

            // this gets the access key input by the user from the "Join Organization Form" and uses the access key to find
            // the associated Organization and adds the current user to the desired Project
            currentProject.addUser(currentUser);
        } else {
            return "/error/500";
        }

        // OLD CODE: this added the project to the current user's Organization WITHOUT CHECKING
        //userRepository.findByEmail(principal.getName()).getOrganization().addToProject(projectRepository.findByIdentifier(identifier));

        // after adding the current user to the desired Organization, this saves the Organization
        projectRepository.save(projectRepository.findByAccessKeyAndOrganization(accessKey, currentUserOrganization));

        return "redirect:/projects/" + currentProject.getIdentifier();
    }

    @PostMapping("/save")
    public String editProject(@RequestParam(value = "id") Long id, Project project, Principal principal) {
        if(userRepository.findByEmail(principal.getName()).getOrganization() == null)
            return "/organization/select-organization";

        Project currentProject = projectRepository.findProjectById(id);

        // fixes bug that when updating a Project removed all the Users in an updated Project except for the User that
        // performed the update
        // had to use 'currentProject' because I needed to access a Project's Users before the update was performed
        for (User user : currentProject.getUsers()) {
            project.addUser(user);
        }

        projectRepository.save(project);

        return "redirect:/projects/all";
    }

    @RequestMapping("/delete/{id}")
    public String deleteProject(@PathVariable(name = "id") Long id, Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName());

        if(currentUser.getOrganization() == null)
            return "/organization/select-organization";

        // checks if the Current User is involved with the Requested Project
        if(!currentUser.getProjects().contains(projectRepository.findProjectById(id))) {
            return "/error/404";
        }

        projectRepository.deleteById(id);

        return "redirect:/projects/all";
    }

    // this returns the json of all the projects
    @GetMapping(path = "/json")
    public @ResponseBody
    List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
}

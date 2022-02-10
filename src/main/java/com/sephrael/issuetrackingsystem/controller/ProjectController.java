package com.sephrael.issuetrackingsystem.controller;

import com.sephrael.issuetrackingsystem.entity.Issue;
import com.sephrael.issuetrackingsystem.entity.Organization;
import com.sephrael.issuetrackingsystem.entity.Project;
import com.sephrael.issuetrackingsystem.entity.User;
import com.sephrael.issuetrackingsystem.repository.IssueRepository;
import com.sephrael.issuetrackingsystem.repository.ProjectRepository;
import com.sephrael.issuetrackingsystem.repository.UserRepository;
import com.sephrael.issuetrackingsystem.service.IssueService;
import com.sephrael.issuetrackingsystem.service.ProjectService;
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
    private ProjectService projectService;
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

        // verifies that the Project requested for Creation has unique fields and redirects to '/projects/all' with an
        // alert message based on the result
        return projectService.isProjectIdentifierAndAccessKeyUniqueWithinOrganization(newProject, currentUser,
                true, redirectAttributes);
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
    public String editProject(@RequestParam(value = "id") Long id, Project requestedProjectUpdate, Principal principal,
                              RedirectAttributes redirectAttributes) {
        User currentUser = userRepository.findByEmail(principal.getName());

        if(currentUser.getOrganization() == null)
            return "/organization/select-organization";

        Project projectBeforeUpdate = projectRepository.findProjectById(id);

        // fixes bug that when updating a Project removed all the Users in an updated Project except for the User that
        // performed the update
        // had to use 'projectBeforeUpdate' because I needed to access a Project's Users before the update was performed
        for (User user : projectBeforeUpdate.getUsers()) {
            requestedProjectUpdate.addUser(user);
        }

        // verifies that the Project requested for an Update has unique fields and redirects to '/projects/all' with an
        // alert message based on the result
        return projectService.isProjectIdentifierAndAccessKeyUniqueWithinOrganization(requestedProjectUpdate, currentUser,
                false, redirectAttributes);
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

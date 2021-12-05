package com.sephrael.issuetrackingsystem.controller;

import com.sephrael.issuetrackingsystem.entity.Comment;
import com.sephrael.issuetrackingsystem.entity.Issue;
import com.sephrael.issuetrackingsystem.entity.Project;
import com.sephrael.issuetrackingsystem.entity.User;
import com.sephrael.issuetrackingsystem.repository.CommentRepository;
import com.sephrael.issuetrackingsystem.repository.IssueRepository;
import com.sephrael.issuetrackingsystem.repository.ProjectRepository;
import com.sephrael.issuetrackingsystem.repository.UserRepository;
import com.sephrael.issuetrackingsystem.service.IssueService;
import com.sephrael.issuetrackingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/issues")
public class IssueController {

    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private IssueService issueService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/{identifier}")
    public String showIssuesByProject(@PathVariable("identifier") String identifier, Model model, Principal principal) {
        List<Issue> issuesByProject = issueService.findProjectById(projectRepository.findByIdentifier(identifier).getId());
        model.addAttribute("issues", issuesByProject);
        model.addAttribute("currentUser", userRepository.findByEmail(principal.getName()));
        model.addAttribute("currentProject", projectRepository.findByIdentifier(identifier));
        model.addAttribute("currentUserProjects", userRepository.findByEmail(principal.getName()).getProjects());

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
        return("/issues/issues");
    }

    @GetMapping("/{identifier}/results")
    public String filterIssues(@RequestParam(value = "type", required = false) String type,
                               @RequestParam(value = "status", required = false) String status,
                               @RequestParam(value = "priority", required = false) String priority,
                               @RequestParam(value = "assignedTo", required = false) String assignedTo,
                               @PathVariable("identifier") String identifier, Model model, Principal principal) {

        // this allows filter fields to be empty
        if(type == "") { type = null; }
        if(status == "") { status = null; }
        if(priority == "") { priority = null; }
        if(assignedTo == "") { assignedTo = null; }

        Project currentProject = projectRepository.findByIdentifier(identifier);
        List<Issue> filteredIssues = issueRepository.findByProjectAndStatusAndPriorityAndTypeAndAssignedTo(currentProject,
                status, priority, type, userRepository.findByEmail(assignedTo));

        model.addAttribute("issues", filteredIssues);
        model.addAttribute("currentProject", currentProject);
        model.addAttribute("currentUser", userRepository.findByEmail(principal.getName()));
        model.addAttribute("currentUserProjects", userRepository.findByEmail(principal.getName()).getProjects());

        return ("/issues/issues");
    }

    @RequestMapping("/{identifier}/new")
    public String showNewIssuePage(@PathVariable(name = "identifier") String identifier, Model model, Principal principal) {
        Issue issue = new Issue();
        model.addAttribute("issue", issue);
        model.addAttribute("currentUser", userRepository.findByEmail(principal.getName()));
        model.addAttribute("users", userService.listAll());
        model.addAttribute("currentProject", projectRepository.findByIdentifier(identifier));
        model.addAttribute("currentUserProjects", userRepository.findByEmail(principal.getName()).getProjects());

        // THIS ALLOWS ME TO ASSIGN EMAIL TO ISSUE TABLE (create-issue.html)
//        issue.setUserEmail(principal.getName());

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
        return("/issues/create-issue");
    }

    @PostMapping(value = "/save")
    public String saveIssue(@RequestParam("project") Long id, @RequestParam(value = "assignedTo", required = false)User assignedTo, @ModelAttribute("issue") Issue issue, Principal principal) {
        // this connects the newly created Issue to the current User that created the Issue
        userRepository.findByEmail(principal.getName()).addToIssue(issue);

        issue.setAssignedTo(assignedTo);
        String identifier = projectRepository.findProjectById(id).getIdentifier();

        issueService.save(issue);

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
        return ("redirect:/issues/" + identifier + "/view/" + issue.getId());
    }

    @RequestMapping("/{identifier}/view/{id}")
    public String showViewIssuePage(@PathVariable("id") long id, @PathVariable(name = "identifier") String identifier, Model model, Principal principal) {
        Issue issue = issueService.find(id);
        model.addAttribute("comment", new Comment());
        model.addAttribute("comments", issue.getComments());
        model.addAttribute("issue", issue);
        model.addAttribute("currentUser", userRepository.findByEmail(principal.getName()));
        model.addAttribute("currentProject", projectRepository.findByIdentifier(identifier));
        model.addAttribute("currentUserProjects", userRepository.findByEmail(principal.getName()).getProjects());

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
        return "/issues/view-issue";
    }

    @GetMapping("/{identifier}/edit/{id}")
    public String showEditIssuePage(@PathVariable("id") long id, @PathVariable(name = "identifier") String identifier, Model model, Principal principal) {
        Issue issue = issueService.find(id);
        model.addAttribute("issue", issue);
        model.addAttribute("currentUser", userRepository.findByEmail(principal.getName()));
        model.addAttribute("currentProject", projectRepository.findByIdentifier(identifier));
        model.addAttribute("currentUserProjects", userRepository.findByEmail(principal.getName()).getProjects());

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
        return "/issues/edit-issue";
    }

    @RequestMapping("/{identifier}/delete/{id}")
    public String deleteIssue(@PathVariable(name = "id") int id, @PathVariable(name = "identifier") String identifier, Principal principal) {
        issueService.delete(id);

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
        return "redirect:/issues/" + identifier;
    }

    // this shows the json format of all the Issues
    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Issue> getAllIssues() {
        return issueService.listAll();
    }

    // this shows the json format of all the Issues by Project
    @GetMapping(path = "/{identifier}/all")
    public @ResponseBody Iterable<Issue> getAllIssuesByProject(@PathVariable("identifier") String identifier) {
        return issueService.findProjectByIdentifier(identifier);
    }

    // this shows the json format of all the Comments
    @GetMapping(path = "/comments/all")
    public @ResponseBody Iterable<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}
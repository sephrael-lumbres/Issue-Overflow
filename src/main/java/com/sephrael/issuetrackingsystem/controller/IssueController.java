package com.sephrael.issuetrackingsystem.controller;

import com.sephrael.issuetrackingsystem.entity.Comment;
import com.sephrael.issuetrackingsystem.entity.Issue;
import com.sephrael.issuetrackingsystem.repository.CommentRepository;
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
    private IssueService issueService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/{accessKey}")
    public String showIssuesByProject(@PathVariable("accessKey") String accessKey, Model model, Principal principal) {
        List<Issue> issuesByProject = issueService.findProjectByAccessKey(accessKey);
        model.addAttribute("issuesByProject", issuesByProject);
        model.addAttribute("currentProject", projectRepository.findByAccessKey(accessKey));
        model.addAttribute("currentUserProjects", userRepository.findByEmail(principal.getName()).getProjects());

        return("/issues/issues");
    }

    @RequestMapping("/{accessKey}/new")
    public String showNewIssuePage(@PathVariable(name = "accessKey") String accessKey, Model model, Principal principal) {
        Issue issue = new Issue();
        model.addAttribute("issue", issue);
        model.addAttribute("users", userService.listAll());
        model.addAttribute("currentProject", projectRepository.findByAccessKey(accessKey));
        model.addAttribute("currentUserProjects", userRepository.findByEmail(principal.getName()).getProjects());

        // THIS ALLOWS ME TO ASSIGN EMAIL TO ISSUE TABLE (create-issue.html)
//        issue.setUserEmail(principal.getName());

        return("/issues/create-issue");
    }

    @PostMapping(value = "/save")
    public String saveIssue(@RequestParam("project") Long id, @ModelAttribute("issue") Issue issue, Principal principal) {
        // this connects the newly created Issue to the current User that created the Issue
        userRepository.findByEmail(principal.getName()).addToIssue(issue);
        String accessKey = projectRepository.findProjectById(id).getAccessKey();

        issueService.save(issue);

        return "redirect:/issues/" + accessKey;
    }

    @RequestMapping("/{accessKey}/view/{id}")
    public String showViewIssuePage(@PathVariable("id") long id, @PathVariable(name = "accessKey") String accessKey, Model model, Principal principal) {
        Issue issue = issueService.find(id);
        model.addAttribute("comment", new Comment());
        model.addAttribute("comments", issue.getComments());
        model.addAttribute("issue", issue);
        model.addAttribute("user", userRepository.findByEmail(principal.getName()));
        model.addAttribute("currentProject", projectRepository.findByAccessKey(accessKey));
        model.addAttribute("currentUserProjects", userRepository.findByEmail(principal.getName()).getProjects());

        return "/issues/view-issue";
    }

    @GetMapping("/{accessKey}/edit/{id}")
    public String showEditIssuePage(@PathVariable("id") long id, @PathVariable(name = "accessKey") String accessKey, Model model, Principal principal) {
        Issue issue = issueService.find(id);
        model.addAttribute("issue", issue);
        model.addAttribute("currentProject", projectRepository.findByAccessKey(accessKey));
        model.addAttribute("currentUserProjects", userRepository.findByEmail(principal.getName()).getProjects());

        return "/issues/edit-issue";
    }

    @RequestMapping("/{accessKey}/delete/{id}")
    public String deleteIssue(@PathVariable(name = "id") int id, @PathVariable(name = "accessKey") String accessKey) {
        issueService.delete(id);

        return "redirect:/issues/" + accessKey;
    }

    // this shows the json format of all the Issues
    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Issue> getAllIssues() {
        return issueService.listAll();
    }

    // this shows the json format of all the Issues by Project
    @GetMapping(path = "/{accessKey}/all")
    public @ResponseBody Iterable<Issue> getAllIssuesByProject(@PathVariable("accessKey") String accessKey) {
        return issueService.findProjectByAccessKey(accessKey);
    }

    // this shows the json format of all the Comments
    @GetMapping(path = "/comments/all")
    public @ResponseBody Iterable<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}
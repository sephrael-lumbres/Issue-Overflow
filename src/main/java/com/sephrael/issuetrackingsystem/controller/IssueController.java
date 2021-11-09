package com.sephrael.issuetrackingsystem.controller;

import com.sephrael.issuetrackingsystem.entity.Comment;
import com.sephrael.issuetrackingsystem.entity.Issue;
import com.sephrael.issuetrackingsystem.repository.CommentRepository;
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

    @RequestMapping("")
    public String viewIssues(Model model) {
        List<Issue> listIssues = issueService.listAll();
        model.addAttribute("listIssues", listIssues);

        return("issues");
    }

    @RequestMapping("/new")
    public String showNewIssuePage(Model model, Principal principal) {
        Issue issue = new Issue();
        model.addAttribute("issue", issue);
        model.addAttribute("users", userService.listAll());
        model.addAttribute("currentUserProjects", userRepository.findByEmail(principal.getName()).getProjects());

        // THIS ALLOWS ME TO ASSIGN EMAIL TO ISSUE TABLE (create-issue.html)
//        issue.setUserEmail(principal.getName());

        return("create-issue");
    }

    @PostMapping(value = "/save")
    public String saveIssue(@ModelAttribute("issue") Issue issue, Principal principal) {
        // this connects the newly created Issue to the current User that created the Issue
        userRepository.findByEmail(principal.getName()).addToIssue(issue);
        issueService.save(issue);

        return "redirect:/issues";
    }

    @RequestMapping("/view/{id}")
    public String showViewIssuePage(@PathVariable("id") long id, Model model, Principal principal) {
        Issue issue = issueService.find(id);
        model.addAttribute("comment", new Comment());
        model.addAttribute("comments", issue.getComments());
        model.addAttribute("issue", issue);
        model.addAttribute("user", userRepository.findByEmail(principal.getName()));

        return "view-issue";
    }

    @GetMapping("/edit/{id}")
    public String showEditIssuePage(@PathVariable("id") long id, Model model, Principal principal) {
        Issue issue = issueService.find(id);
        model.addAttribute("issue", issue);
        model.addAttribute("currentUserProjects", userRepository.findByEmail(principal.getName()).getProjects());

        return "edit-issue";
    }

    @RequestMapping("/delete/{id}")
    public String deleteIssue(@PathVariable(name = "id") int id) {
        issueService.delete(id);

        return "redirect:/issues";
    }

    // this shows the json format of all the Issues
    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Issue> getAllIssues() {
        return issueService.listAll();
    }

    // this shows the json format of all the Comments
    @GetMapping(path = "/comment/all")
    public @ResponseBody Iterable<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}
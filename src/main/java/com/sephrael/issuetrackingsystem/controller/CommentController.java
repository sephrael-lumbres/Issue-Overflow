package com.sephrael.issuetrackingsystem.controller;

import com.sephrael.issuetrackingsystem.entity.Comment;
import com.sephrael.issuetrackingsystem.entity.Issue;
import com.sephrael.issuetrackingsystem.entity.User;
import com.sephrael.issuetrackingsystem.repository.CommentRepository;
import com.sephrael.issuetrackingsystem.repository.IssueRepository;
import com.sephrael.issuetrackingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/issues/{identifier}/view/{issueKey}/comment")
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/new")
    public String addComment(@PathVariable(value = "issueKey") String issueKey, Comment comment, Principal principal,
                             @PathVariable String identifier) {
        userRepository.findByEmail(principal.getName()).addToComment(comment);
        issueRepository.findByIssueKey(issueKey).addToComment(comment);

        comment.setIsEdited(false);
        commentRepository.save(comment);

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
        return "redirect:/issues/{identifier}/view/" + issueKey;
    }

    @PostMapping("/save")
    public String editComment(@RequestParam("id") Long id, @RequestParam("user") User user, @RequestParam("issue") Issue issue,
                              @RequestParam("isEdited") boolean isEdited, @RequestParam("message") String message,
                              @PathVariable(value = "issueKey") String issueKey,
                              @PathVariable String identifier, Principal principal) {

        Comment updatedComment = commentRepository.findCommentById(id);

        updatedComment.setUser(user);
        updatedComment.setIssue(issue);
        // sets Comment isEdited to true if comment has not yet been edited
        if(!isEdited) { updatedComment.setIsEdited(true); }
        updatedComment.setMessage(message);

        commentRepository.save(updatedComment);

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
        return "redirect:/issues/{identifier}/view/" + issueKey;
    }

    @RequestMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable(value = "issueKey") String issueKey,
                                @PathVariable(value = "commentId") Long commentId,
                                @PathVariable String identifier, Principal principal) {
        commentRepository.deleteById(commentId);

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
        return "redirect:/issues/{identifier}/view/" + issueKey;
    }

    // this shows the json format of all the Comments of an Issue
    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Comment> getAllCommentsByIssueId(@PathVariable(value = "issueKey") String issueKey, @PathVariable(name = "identifier") String identifier) {
        return issueRepository.findByIssueKey(issueKey).getComments();
    }
}

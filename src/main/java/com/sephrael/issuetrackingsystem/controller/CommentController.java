package com.sephrael.issuetrackingsystem.controller;

import com.sephrael.issuetrackingsystem.entity.Comment;
import com.sephrael.issuetrackingsystem.entity.Issue;
import com.sephrael.issuetrackingsystem.entity.User;
import com.sephrael.issuetrackingsystem.repository.CommentRepository;
import com.sephrael.issuetrackingsystem.repository.UserRepository;
import com.sephrael.issuetrackingsystem.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/issues/{identifier}/view/{identifier}-{issueId}/comment")
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private IssueService issueService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/new")
    public String addComment(@PathVariable(value = "issueId") Long issueId, @PathVariable(name = "identifier") String identifier, Comment comment, Principal principal) {
        userRepository.findByEmail(principal.getName()).addToComment(comment);
        issueService.find(issueId).addToComment(comment);

        comment.setIsEdited(false);
        commentRepository.save(comment);

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
        return "redirect:/issues/{identifier}/view/" + identifier + '-' + issueId;
    }

    @PostMapping("/save")
    public String editComment(@RequestParam("id") Long id, @RequestParam("user") User user, @RequestParam("issue") Issue issue,
                              @RequestParam("isEdited") boolean isEdited, @RequestParam("message") String message,
                              @PathVariable(value = "issueId") Long issueId, @PathVariable(name = "identifier") String identifier, Principal principal) {

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
        return "redirect:/issues/{identifier}/view/" + identifier + '-' + issueId;
    }

    @RequestMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable(value = "issueId") Long issueId, @PathVariable(value = "commentId") Long commentId,
                                @PathVariable(name = "identifier") String identifier, Principal principal) {
        commentRepository.deleteById(commentId);

        if(userRepository.findByEmail(principal.getName()).getOrganization() == null) {
            return "/organization/select-organization";
        }
        return "redirect:/issues/{identifier}/view/" + identifier + '-' + issueId;
    }

    // this shows the json format of all the Comments of an Issue
    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Comment> getAllCommentsByIssueId(@PathVariable(value = "issueId") Long issueId, @PathVariable(name = "identifier") String identifier) {
        return issueService.find(issueId).getComments();
    }
}

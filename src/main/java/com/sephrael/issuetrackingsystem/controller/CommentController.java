package com.sephrael.issuetrackingsystem.controller;

import com.sephrael.issuetrackingsystem.entity.Comment;
import com.sephrael.issuetrackingsystem.entity.Issue;
import com.sephrael.issuetrackingsystem.repository.CommentRepository;
import com.sephrael.issuetrackingsystem.repository.UserRepository;
import com.sephrael.issuetrackingsystem.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/issues/view/{issueId}/comment")
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private IssueService issueService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/save")
    public String addComment(@PathVariable(value = "issueId") Long issueId, Comment comment, Principal principal) {
        userRepository.findByEmail(principal.getName()).addToComment(comment);
        issueService.find(issueId).addToComment(comment);

        commentRepository.save(comment);

        return "redirect:/issues/view/" + issueId;
    }

    @RequestMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable(value = "issueId") Long issueId, @PathVariable(value = "commentId") Long commentId) {
        commentRepository.deleteById(commentId);

        return "redirect:/issues/view/" + issueId;
    }

    // this shows the json format of all the Comments of an Issue
    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Comment> getAllCommentsByIssueId(@PathVariable(value = "issueId") Long issueId) {
        return commentRepository.findByIssueId(issueId);
    }
}

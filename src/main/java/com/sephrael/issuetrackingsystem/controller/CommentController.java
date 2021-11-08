package com.sephrael.issuetrackingsystem.controller;

import com.sephrael.issuetrackingsystem.entity.Comment;
import com.sephrael.issuetrackingsystem.entity.Issue;
import com.sephrael.issuetrackingsystem.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/issues/view")
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;

    @PostMapping("/{issueId}/comment/save")
    public String addComment(@PathVariable(value = "issueId") Long issueId, Comment comment, Issue issue, Principal principal) {
        //userRepository.findByEmail(principal.getName().addToComment(comment));
        //issueService.find(issue.getId()).addToComment(comment);

        commentRepository.save(comment);

        return "redirect:/issues/view/" + issueId;
    }

    @RequestMapping("/{issueId}/comment/{commentId}/delete")
    public String deleteComment(@PathVariable(value = "issueId") Long issueId, @PathVariable(value = "commentId") Long commentId) {
        commentRepository.deleteById(commentId);

        return "redirect:/issues/view/" + issueId;
    }
}

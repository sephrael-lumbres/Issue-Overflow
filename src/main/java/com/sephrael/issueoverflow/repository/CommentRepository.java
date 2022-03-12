package com.sephrael.issueoverflow.repository;

import com.sephrael.issueoverflow.entity.Comment;
import com.sephrael.issueoverflow.entity.Issue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findByIssue(Issue issue);
    Comment findCommentByMessage(String message);
    Comment findCommentById(Long id);
}

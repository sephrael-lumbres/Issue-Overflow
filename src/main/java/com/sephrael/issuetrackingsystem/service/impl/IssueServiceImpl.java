//package com.sephrael.bugtracker.service.impl;
//
//import com.sephrael.bugtracker.entity.Issue;
//import com.sephrael.bugtracker.entity.User;
//import com.sephrael.bugtracker.repository.IssueRepository;
//import com.sephrael.bugtracker.repository.UserRepository;
//import com.sephrael.bugtracker.service.IssueService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class IssueServiceImpl implements IssueService {
//
//    @Autowired
//    private IssueRepository issueRepository;
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public List<Issue> listAll() {
//        return (List<Issue>) issueRepository.findAll();
//    }
//
//    public Issue save(Issue issue) {
//        issue.setIssueId(issue.getIssueId());
//        issue.setIssueTitle(issue.getIssueTitle());
//        issue.setIssueDescription(issue.getIssueDescription());
//        issue.setIssueType(issue.getIssueType());
//        issue.setIssuePriority(issue.getIssuePriority());
//        issue.setIssueStatus(issue.getIssueStatus());
//        issue.setIssueCreated(issue.getIssueCreated());
////        issue.setUser(userRepository.getOne(issue.getAuthor()));
//        issue.setUserEmail(issue.getUserEmail());
//        return issueRepository.save(issue);
//    }
//
//    @Override
//    public Issue find(Long id) {
//        return null;
//    }
//
//    public Issue find(long id) {
//        if(!issueRepository.existsById(id)) {
//            throw new RuntimeException("ISSUE_NOT_FOUND");
//        }
//
//        return issueRepository.findById(id).get();
//    }
//
//    @Override
//    public boolean delete(long id) {
//        if(!issueRepository.existsById(id)) {
//            throw new RuntimeException("ISSUE_NOT_FOUND");
//        } else {
//            issueRepository.deleteById(id);
//        }
//
//        return true;
//    }
//
//    public List<Issue> findByUser(Long id) {
//        User user = userRepository.findById(id).get();
//
//        return issueRepository.findByUser(user);
//    }
//}

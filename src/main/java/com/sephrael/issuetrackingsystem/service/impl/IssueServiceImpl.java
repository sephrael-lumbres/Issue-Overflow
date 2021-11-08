//package com.sephrael.issuetrackingsystem.service.impl;
//
//import com.sephrael.issuetrackingsystem.entity.Issue;
//import com.sephrael.issuetrackingsystem.entity.User;
//import com.sephrael.issuetrackingsystem.repository.IssueRepository;
//import com.sephrael.issuetrackingsystem.repository.UserRepository;
//import com.sephrael.issuetrackingsystem.service.IssueService;
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
//        issue.setId(issue.getId());
//        issue.setTitle(issue.getTitle());
//        issue.setDescription(issue.getDescription());
//        issue.setType(issue.getType());
//        issue.setPriority(issue.getPriority());
//        issue.setStatus(issue.getStatus());
//        issue.setDateCreated(issue.getDateCreated());
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

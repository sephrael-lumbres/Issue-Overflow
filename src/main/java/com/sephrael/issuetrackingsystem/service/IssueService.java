package com.sephrael.issuetrackingsystem.service;

import com.sephrael.issuetrackingsystem.entity.Issue;
import com.sephrael.issuetrackingsystem.entity.Project;
import com.sephrael.issuetrackingsystem.entity.User;
import com.sephrael.issuetrackingsystem.repository.IssueRepository;
import com.sephrael.issuetrackingsystem.repository.ProjectRepository;
import com.sephrael.issuetrackingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class IssueService {
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public List<Issue> listAll() {
        return (List<Issue>) issueRepository.findAll();
    }

    public List<Issue> findByUser(Long id) {
        User user = userRepository.findById(id).get();

        return issueRepository.findByUser(user);
    }

    public List<Issue> findProjectByAccessKey(String accessKey) {
        Project project = projectRepository.findByAccessKey(accessKey);

        return issueRepository.findByProject(project);
    }

    public void save(Issue issue) {
        issueRepository.save(issue);
    }

    public Issue find(long id) {
        return issueRepository.findById(id).get();
    }

    public void delete(long id) {
        issueRepository.deleteById(id);
    }
}

package com.sephrael.issuetrackingsystem.service;

import com.sephrael.issuetrackingsystem.entity.Issue;
import com.sephrael.issuetrackingsystem.entity.IssueKeySequence;
import com.sephrael.issuetrackingsystem.entity.Project;
import com.sephrael.issuetrackingsystem.entity.User;
import com.sephrael.issuetrackingsystem.repository.IssueKeySequenceRepository;
import com.sephrael.issuetrackingsystem.repository.IssueRepository;
import com.sephrael.issuetrackingsystem.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class IssueService {
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private IssueKeySequenceRepository issueKeySequenceRepository;

    public List<Issue> listAll() {
        return (List<Issue>) issueRepository.findAll();
    }

    public List<Issue> findProjectByIdentifier(String identifier) {
        Project project = projectRepository.findByAccessKey(identifier);

        return issueRepository.findByProject(project);
    }

    public List<Issue> findProjectById(Long id) {
        Project project = projectRepository.findProjectById(id);

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

    public List<Issue> getIssuesSortedByRecentActivity(List<Issue> sortedIssues, List<Issue> issuesSortedByDateCreated, List<Issue> issuesSortedByDateUpdated) {
        for(int i=0; i < issuesSortedByDateCreated.size(); i++) {
            if(issuesSortedByDateCreated.get(i).getDateCreated().isEqual(issuesSortedByDateUpdated.get(i).getDateUpdated())) {
                sortedIssues.set(i, issuesSortedByDateCreated.get(i));
            } else if(issuesSortedByDateCreated.get(i).getDateCreated().isBefore(issuesSortedByDateUpdated.get(i).getDateUpdated())) {
                sortedIssues.set(i, issuesSortedByDateUpdated.get(i));
            }
        }

        return sortedIssues;
    }

    public String getIssueDateTimeString(Issue issue) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime issueDateTimeString;

        if(issue.getDateCreated() == issue.getDateUpdated()) {
            issueDateTimeString = issue.getDateCreated();
        } else {
            issueDateTimeString = issue.getDateUpdated();
        }

        if(issueDateTimeString.until(currentDateTime, ChronoUnit.HOURS) < 1) {
            return issueDateTimeString.until(currentDateTime, ChronoUnit.MINUTES) + " min";
        } else if(issueDateTimeString.until(currentDateTime, ChronoUnit.HOURS) == 1) {
            return issueDateTimeString.until(currentDateTime, ChronoUnit.HOURS) + " hr";
        } else if(issueDateTimeString.until(currentDateTime, ChronoUnit.HOURS) < 24) {
            return issueDateTimeString.until(currentDateTime, ChronoUnit.HOURS) + " hrs";
        } else if(issueDateTimeString.until(currentDateTime, ChronoUnit.HOURS) < 48) {
            return issueDateTimeString.until(currentDateTime, ChronoUnit.DAYS) + " day";
        } else {
            return issueDateTimeString.until(currentDateTime, ChronoUnit.DAYS) + " days";
        }
    }

    public int getNumberOfIssuesByProjectAndStatus(String status, String projectIdentifier) {
        return issueRepository.findByProjectAndStatus(projectRepository.findByIdentifier(projectIdentifier), status).size();
    }

    public int getNumberOfIssuesByOrganizationAndStatus(String status, User currentUser) {
        int numberOfIssuesByOrganizationAndStatus = 0;
        List<Project> currentProjects = currentUser.getProjects();

        for(Project project : currentProjects) {
            numberOfIssuesByOrganizationAndStatus += issueRepository.findByProjectAndStatus(project, status).size();
        }

        return numberOfIssuesByOrganizationAndStatus;
    }

    // this allows filter fields to be empty
    public String setEmptyFilterFieldToNull(String filterField) {
        if(filterField == "") { filterField = null; }

        return filterField;
    }

    // this sets the Issue Key
    public String setIssueKey(String projectIdentifier) {
        Project project = projectRepository.findByIdentifier(projectIdentifier);
        String issueKey = "";

        if(project.getIssues().size() == 0) {
            IssueKeySequence issueKeySequence = new IssueKeySequence();
            issueKeySequence.setProjectIdentifier(projectIdentifier);
            issueKeySequence.setIssueKeyCounter(1);
            issueKeySequenceRepository.save(issueKeySequence);
            issueKey = projectIdentifier + "-" + issueKeySequence.getIssueKeyCounter();
        } else {
            IssueKeySequence currentSequence = issueKeySequenceRepository.findByProjectIdentifier(projectIdentifier);
            currentSequence.setIssueKeyCounter(currentSequence.getIssueKeyCounter() + 1);

            issueKey = projectIdentifier + "-" + currentSequence.getIssueKeyCounter();
        }

        return issueKey;
    }
}

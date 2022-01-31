package com.sephrael.issuetrackingsystem.service;

import com.sephrael.issuetrackingsystem.entity.Issue;
import com.sephrael.issuetrackingsystem.entity.IssueKeySequence;
import com.sephrael.issuetrackingsystem.entity.Project;
import com.sephrael.issuetrackingsystem.entity.User;
import com.sephrael.issuetrackingsystem.repository.IssueKeySequenceRepository;
import com.sephrael.issuetrackingsystem.repository.IssueRepository;
import com.sephrael.issuetrackingsystem.repository.ProjectRepository;
import com.sephrael.issuetrackingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
public class IssueService {
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private UserRepository userRepository;
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

    public List<Issue> setIssueChangeHistoryValues(Issue issue1, Issue issue2, List<Issue> issues) {
        Issue newIssue;
        User updatedBy;

        if(issue1.getUpdatedBy() != null && issue1.getChangeVersion().getRevisionType() == RevisionMetadata.RevisionType.UPDATE) {
            updatedBy = userRepository.findUserById(issue1.getUpdatedBy().getId());
        } else {
            updatedBy = null;
        }

        if(!Objects.equals(issue1.getTitle(), issue2.getTitle())) {
            newIssue = new Issue();
            newIssue.setProperty("Title");
            newIssue.setOldValue(issue2.getTitle());
            newIssue.setNewValue(issue1.getTitle());
            newIssue.setChangeVersion(issue2.getChangeVersion());
            newIssue.setUpdatedBy(updatedBy);
            issues.add(newIssue);
        }
        if(!Objects.equals(issue1.getDescription(), issue2.getDescription())) {
            newIssue = new Issue();
            newIssue.setProperty("Description");
            newIssue.setOldValue(issue2.getDescription());
            newIssue.setNewValue(issue1.getDescription());
            newIssue.setChangeVersion(issue2.getChangeVersion());
            newIssue.setUpdatedBy(updatedBy);
            issues.add(newIssue);
        }
        if(issue2.getAssignedTo() == null && issue1.getAssignedTo() != null ||
                issue1.getAssignedTo() == null && issue2.getAssignedTo() != null ||
                issue2.getAssignedTo() != null && !Objects.equals(issue1.getAssignedTo().getId(), issue2.getAssignedTo().getId())) {

            newIssue = new Issue();
            newIssue.setProperty("Assigned To");
            // if the Issue's 'Assigned To' is empty or does not exist
            if(issue2.getAssignedTo() == null || userRepository.findUserById(issue2.getAssignedTo().getId()) == null)
                newIssue.setOldValue(null);
            else {
                User issue2User = userRepository.findUserById(issue2.getAssignedTo().getId());
                newIssue.setOldValue(issue2User.getFirstName() + " " + issue2User.getLastName());
            }
            // if the Issue's 'Assigned To' is empty or does not exist
            if(issue1.getAssignedTo() == null || userRepository.findUserById(issue1.getAssignedTo().getId()) == null)
                newIssue.setNewValue(null);
            else {
                System.out.println(issue1.getAssignedTo());
                User issue1User = userRepository.findUserById(issue1.getAssignedTo().getId());
                newIssue.setNewValue(issue1User.getFirstName() + " " + issue1User.getLastName());
            }
            newIssue.setChangeVersion(issue2.getChangeVersion());
            newIssue.setUpdatedBy(updatedBy);
            issues.add(newIssue);
        }
        if(!Objects.equals(issue1.getProject().getId(), issue2.getProject().getId())) {
            newIssue = new Issue();
            newIssue.setProperty("Project");
            newIssue.setOldValue(projectRepository.findProjectById(issue2.getProject().getId()).getName());
            newIssue.setNewValue(projectRepository.findProjectById(issue1.getProject().getId()).getName());
            newIssue.setChangeVersion(issue2.getChangeVersion());
            newIssue.setUpdatedBy(updatedBy);
            issues.add(newIssue);
        }
        if(!Objects.equals(issue1.getType(), issue2.getType())) {
            newIssue = new Issue();
            newIssue.setProperty("Type");
            newIssue.setOldValue(issue2.getType());
            newIssue.setNewValue(issue1.getType());
            newIssue.setChangeVersion(issue2.getChangeVersion());
            newIssue.setUpdatedBy(updatedBy);
            issues.add(newIssue);
        }
        if(!Objects.equals(issue1.getPriority(), issue2.getPriority())) {
            newIssue = new Issue();
            newIssue.setProperty("Priority");
            newIssue.setOldValue(issue2.getPriority());
            newIssue.setNewValue(issue1.getPriority());
            newIssue.setChangeVersion(issue2.getChangeVersion());
            newIssue.setUpdatedBy(updatedBy);
            issues.add(newIssue);
        }
        if(!Objects.equals(issue1.getStatus(), issue2.getStatus())) {
            newIssue = new Issue();
            newIssue.setProperty("Status");
            newIssue.setOldValue(issue2.getStatus());
            newIssue.setNewValue(issue1.getStatus());
            newIssue.setChangeVersion(issue2.getChangeVersion());
            newIssue.setUpdatedBy(updatedBy);
            issues.add(newIssue);
        }

        return issues;
    }

    // gets a list of issues with only 'Property', 'Old Value', and 'New Value' fields
    public List<Issue> getIssueChangeHistoryList(Long issueId) {
        // list of issues that contains all changes within an 'Issue'
        List<Issue> revisedIssues = new ArrayList<>();
        issueRepository.findRevisions(issueId).get().forEach(issue -> {
            issue.getEntity().setChangeVersion(issue.getMetadata());
            revisedIssues.add(issue.getEntity());
        });

        // list of issues that will contain the old and new values of an 'Issue'
        List<Issue> issues = new ArrayList<>();

        for(int i = 0; i < revisedIssues.size(); i++) {
            Issue issue = revisedIssues.get(i);

            if(i + 1 < revisedIssues.size()) {
                Issue newValueIssue = revisedIssues.get(i + 1);
                issues = setIssueChangeHistoryValues(newValueIssue, issue, issues);
            }
        }

        return issues;
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
    public void setIssueKey(Issue issue, Project nextProject) {
        Project previousProject = issue.getProject();
        String issueKey = "";

        // if issue does not have an issue key or if the issue is being moved to a different project
        if(issue.getIssueKey() == null || !Objects.equals(previousProject.getId(), nextProject.getId())) {
            if(nextProject.getIssues().size() == 0) {
                IssueKeySequence issueKeySequence = new IssueKeySequence();
                issueKeySequence.setProjectIdentifier(nextProject.getIdentifier());
                issueKeySequence.setIssueKeyCounter(1);
                issueKeySequenceRepository.save(issueKeySequence);
                issueKey = nextProject.getIdentifier() + "-" + issueKeySequence.getIssueKeyCounter();
            } else {
                IssueKeySequence currentSequence = issueKeySequenceRepository.findByProjectIdentifier(nextProject.getIdentifier());
                currentSequence.setIssueKeyCounter(currentSequence.getIssueKeyCounter() + 1);

                issueKey = nextProject.getIdentifier() + "-" + currentSequence.getIssueKeyCounter();
            }

            // if the previous project will no longer have any issues after switching its only issue to a different project
            if(previousProject != null && previousProject != nextProject && previousProject.getIssues().size() <= 1) {
                issueKeySequenceRepository.delete(issueKeySequenceRepository.findByProjectIdentifier(previousProject.getIdentifier()));
            }

            issue.setIssueKey(issueKey);
        }
    }
}

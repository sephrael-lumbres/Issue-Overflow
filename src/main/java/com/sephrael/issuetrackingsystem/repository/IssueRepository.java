package com.sephrael.issuetrackingsystem.repository;

import com.sephrael.issuetrackingsystem.entity.Issue;
import com.sephrael.issuetrackingsystem.entity.Organization;
import com.sephrael.issuetrackingsystem.entity.Project;
import com.sephrael.issuetrackingsystem.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends CrudRepository<Issue, Long> {

    List<Issue> findByUser(User user);
    List<Issue> findByOrganization(Organization organization);
    List<Issue> findByProject(Project project);
    Issue findIssueByTitle(String title);

    // used to sort Issues of a Project for Recent Activity
    List<Issue> findByProjectOrderByDateCreatedDesc(Project project);
    List<Issue> findByProjectOrderByDateUpdatedDesc(Project project);

    // used to sort Issues of an Organization for Recent Activity
    List<Issue> findByOrganizationOrderByDateCreatedDesc(Organization organization);
    List<Issue> findByOrganizationOrderByDateUpdatedDesc(Organization organization);

    // used to calculate % progress in dashboard
    List<Issue> findByProjectAndStatus(Project project, String status);

    @Query("SELECT i FROM Issue i WHERE (:project is null  or i.project = :project) and " +
            "(:status is null or i.status = :status) and (:priority is null or i.priority = :priority) and " +
            "(:type is null or i.type = :type) and (:assignedTo is null or i.assignedTo = :assignedTo) and (:user is null or i.user = :user)")
    List<Issue> findByProjectAndStatusAndPriorityAndTypeAndAssignedToAndUser(Project project, String status, String priority, String type, User assignedTo, User user);

    @Query("SELECT i FROM Issue i WHERE (:project is null  or i.project = :project) and " +
            "(:status is null or i.status = :status) and (:priority is null or i.priority = :priority) and " +
            "(:type is null or i.type = :type) and (:user is null or i.user = :user) and (i.assignedTo is null or i.assignedTo = :assignedTo)")
    List<Issue> findByProjectAndStatusAndPriorityAndTypeAndUserAndAssignedTo(Project project, String status, String priority, String type, User user, User assignedTo);
//    @Query("select a from Issue a where a.issueId = ?1")
//    public Issue findIssue(Long issueId);

//    @Modifying
//    @Query("UPDATE Issue i SET i.user = User.id WHERE i.userEmail = User.email")
//    Long joinIssueAndUserByUserId(@Param(value = "id") long id, @Param(value = "email") String email);
}

package com.sephrael.issuetrackingsystem.repository;

import com.sephrael.issuetrackingsystem.entity.Issue;
import com.sephrael.issuetrackingsystem.entity.Project;
import com.sephrael.issuetrackingsystem.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends CrudRepository<Issue, Long> {

    List<Issue> findByUser(User user);
    List<Issue> findByProject(Project project);

    @Query("SELECT i FROM Issue i WHERE (:project is null  or i.project = :project) and " +
            "(:status is null or i.status = :status) and (:priority is null or i.priority = :priority) and " +
            "(:type is null or i.type = :type) and (:assignedTo is null or i.assignedTo = :assignedTo)")
    List<Issue> findByProjectAndStatusAndPriorityAndTypeAndAssignedTo(Project project, String status, String priority, String type, User assignedTo);
    Issue findIssueByTitle(String title);
//    @Query("select a from Issue a where a.issueId = ?1")
//    public Issue findIssue(Long issueId);

//    @Modifying
//    @Query("UPDATE Issue i SET i.user = User.id WHERE i.userEmail = User.email")
//    Long joinIssueAndUserByUserId(@Param(value = "id") long id, @Param(value = "email") String email);
}

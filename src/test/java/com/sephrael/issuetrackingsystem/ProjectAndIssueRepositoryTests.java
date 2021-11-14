package com.sephrael.issuetrackingsystem;

import com.sephrael.issuetrackingsystem.entity.Comment;
import com.sephrael.issuetrackingsystem.entity.Issue;
import com.sephrael.issuetrackingsystem.entity.Project;
import com.sephrael.issuetrackingsystem.repository.CommentRepository;
import com.sephrael.issuetrackingsystem.repository.IssueRepository;
import com.sephrael.issuetrackingsystem.repository.ProjectRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectAndIssueRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @Order(1)
    public void testCreateTwoProjects() {
        Project project1 = new Project();
        project1.setName("NBA 2K");
        project1.setAccessKey("NBA");
        Project savedProject1 = projectRepository.save(project1);
        Project existingProject1 = entityManager.find(Project.class, savedProject1.getId());

        Project project2 = new Project();
        project2.setName("WWE 2K");
        project2.setAccessKey("WWE");
        Project savedProject2 = projectRepository.save(project2);
        Project existingProject2 = entityManager.find(Project.class, savedProject2.getId());

        assertThat(project1.getName()).isEqualTo(existingProject1.getName());
        assertThat(project2.getName()).isEqualTo(existingProject2.getName());
    }

    @Test
    @Order(2)
    public void testFindProjectsByAccessKey() {
        Project project1 = projectRepository.findByAccessKey("NBA");
        assertThat(project1.getAccessKey()).isEqualTo("NBA");

        Project project2 = projectRepository.findByAccessKey("WWE");
        assertThat(project2.getAccessKey()).isEqualTo("WWE");
    }

    @Test
    @Order(3)
    public void testListProjects() {
        List<Project> projects = (List<Project>) projectRepository.findAll();
        assertThat(projects).size().isGreaterThan(0);
    }

    @Test
    @Order(4)
    public void testUpdateProjects() {
        Project project1 = projectRepository.findByAccessKey("NBA");
        project1.setName("NBA 2K22");
        projectRepository.save(project1);
        Project updatedProject1 = projectRepository.findByAccessKey("NBA");

        Project project2 = projectRepository.findByAccessKey("WWE");
        project2.setName("WWE 2K22");
        projectRepository.save(project2);
        Project updatedProject2 = projectRepository.findByAccessKey("WWE");

        assertThat(updatedProject1.getName()).isEqualTo("NBA 2K22");
        assertThat(updatedProject2.getName()).isEqualTo("WWE 2K22");
    }

    @Test
    @Order(5)
    public void testCreateIssue() {
        Issue issue = new Issue();
        issue.setTitle("Wonky Dunk Animations");
        issue.setDescription("When a player performs a dunk, the character sprite is upside down");
        issue.setType("Bug");
        issue.setPriority("High");
        issue.setStatus("Open");

        Issue savedIssue = issueRepository.save(issue);

        Issue existingIssue = entityManager.find(Issue.class, savedIssue.getId());

        assertThat(issue.getTitle()).isEqualTo(existingIssue.getTitle());
    }

    @Test
    @Order(6)
    public void testCreateComment() {
        Comment comment = new Comment();
        comment.setMessage("This bug is very hard to recreate");

        Comment savedComment = commentRepository.save(comment);

        Comment existingComment = entityManager.find(Comment.class, savedComment.getId());

        assertThat(comment.getMessage()).isEqualTo(existingComment.getMessage());
    }

    @Test
    @Order(7)
    public void testAddCommentToIssue() {
        Issue issue = issueRepository.findIssueByTitle("Wonky Dunk Animations");
        Comment comment = commentRepository.findCommentByMessage("This bug is very hard to recreate");

        comment.setIssue(issue);
        commentRepository.save(comment);

        Comment commentWithIssue = commentRepository.findCommentByMessage("This bug is very hard to recreate");

        assertThat(commentWithIssue.getIssue().getTitle()).isEqualTo("Wonky Dunk Animations");
    }

    @Test
    @Order(8)
    public void testAddIssueToProject() {
        Issue issue = issueRepository.findIssueByTitle("Wonky Dunk Animations");
        issue.setProject(projectRepository.findByAccessKey("NBA"));

        issueRepository.save(issue);

        Issue issueWithProject = issueRepository.findIssueByTitle("Wonky Dunk Animations");

        assertThat(issueWithProject.getTitle()).isEqualTo("Wonky Dunk Animations");
    }

    @Test
    @Order(12)
    public void testSwitchIssueWithCommentToDifferentProject() {
        Issue issue = issueRepository.findIssueByTitle("Wonky Dunk Animations");
        issue.setProject(projectRepository.findByAccessKey("WWE"));
        Comment comment = commentRepository.findCommentByMessage("This bug is very hard to recreate");

        issueRepository.save(issue);

        Issue issueWithDifferentProject = issueRepository.findIssueByTitle("Wonky Dunk Animations");

        assertThat(issueWithDifferentProject.getProject().getName()).isEqualTo("WWE 2K22");
        assertThat(comment.getIssue().getTitle()).isEqualTo("Wonky Dunk Animations");
    }

    @Test
    @Order(13)
    public void testUpdateIssueAfterProjectSwitch() {
        Issue issue = issueRepository.findIssueByTitle("Wonky Dunk Animations");
        Comment comment = commentRepository.findCommentByMessage("This bug is very hard to recreate");
        issue.setStatus("Closed");
        issueRepository.save(issue);

        Issue updatedIssue = issueRepository.findIssueByTitle("Wonky Dunk Animations");

        assertThat(updatedIssue.getStatus()).isEqualTo("Closed");
        assertThat(updatedIssue.getProject().getName()).isEqualTo("WWE 2K22");
        assertThat(comment.getIssue().getTitle()).isEqualTo("Wonky Dunk Animations");
    }

    @Test
    @Order(14)
    public void testDeleteProjects() {
        Project project1 = projectRepository.findByAccessKey("WWE");
        Project project2 = projectRepository.findByAccessKey("NBA");

        projectRepository.deleteById(project1.getId());
        projectRepository.deleteById(project2.getId());

        Project deletedProject1 = projectRepository.findByAccessKey("WWE");
        Project deletedProject2 = projectRepository.findByAccessKey("NBA");

        assertThat(deletedProject1).isNull();
        assertThat(deletedProject2).isNull();
    }

    @Test
    @Order(15)
    public void testIssueAndCommentAreDeletedWhenParentProjectHasBeenDeleted() {
        Issue issue = issueRepository.findIssueByTitle("Wonky Dunk Animations");
        Comment comment = commentRepository.findCommentByMessage("This bug is very hard to recreate");

        assertThat(issue).isNull();
        assertThat(comment).isNull();
    }

    @Test
    @Order(16)
    public void testCreateProjectIssueAndCommentWithoutSwitching() {

        // creates a new project
        Project project = new Project();
        project.setName("MLB 2K");
        project.setAccessKey("MLB");
        projectRepository.save(project);

        // creates a new issue
        Issue issue = new Issue();
        issue.setTitle("Incorrect Baseball Bat Sprite");
        issue.setDescription("Baseball bat is showing as a dildo");
        issue.setType("Bug");
        issue.setPriority("High");
        issue.setStatus("Open");
        issueRepository.save(issue);

        // creates a new comment
        Comment comment = new Comment();
        comment.setMessage("I wonder who drew the dildo");
        commentRepository.save(comment);

        // adds comment to issue
        comment.setIssue(issue);
        commentRepository.save(comment);

        // adds issue to project
        issue.setProject(projectRepository.findByAccessKey("MLB"));
        issueRepository.save(issue);

        assertThat(issue.getProject()).isEqualTo(projectRepository.findByAccessKey("MLB"));
    }

    @Test
    @Order(17)
    public void testDeleteProjectWithoutSwitchingIssueToDifferentProject() {
        Project project = projectRepository.findByAccessKey("MLB");

        // deletes the project with the issue and comment
        projectRepository.deleteById(project.getId());
        Project deletedProject = projectRepository.findByAccessKey("MLB");
        Issue deletedIssue = issueRepository.findIssueByTitle("Incorrect Baseball Bat Sprite");
        Comment deletedComment = commentRepository.findCommentByMessage("I wonder who drew the dildo");

        assertThat(deletedProject).isNull();
        assertThat(deletedIssue).isNull();
        assertThat(deletedComment).isNull();
    }
}

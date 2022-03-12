// file from https://www.codejava.net/frameworks/spring-boot/user-registration-and-login-tutorial

package com.sephrael.issueoverflow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Audited
public class User {
    // variables to add: userRole, dateCreated, dateModified, assignedProject, assignedOrganization

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull(message = "Email is required")
    @NotEmpty(message = "Email may not be empty")
    private String email;

    @Column
    @JsonIgnore
    @NotNull(message = "Password is required")
    @NotEmpty(message = "Password may not be empty")
    private String password;

    @Column
    @NotNull(message = "First name is required")
    @NotEmpty(message = "First name may not be empty")
    private String firstName;

    @Column
    @NotNull(message = "Last name is required")
    @NotEmpty(message = "Last name may not be empty")
    private String lastName;

    @Column(unique = true)
    private String resetPasswordToken;

    private boolean hasProfilePicture;

    @NotAudited
    private boolean enabled;

    @ManyToOne
    @NotAudited
    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @ManyToMany(mappedBy = "users")
    @NotAudited
    private List<Project> projects = new ArrayList<>();

    public List<Project> getProjects() {
        // sorts the Projects alphabetically by their Names
        projects.sort(Comparator.comparing(Project::getName));

        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    @ManyToOne
    @NotAudited
    private Organization organization;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @NotAudited
    private List<Issue> issues;

    public void addToIssue(Issue issue) {
        issue.setUser(this);
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @NotAudited
    private List<Comment> comments;

    public void addToComment(Comment comment) {
        comment.setUser(this);
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @NotAudited
    private List<File> files;

    public void addToFiles(File file) {
        file.setUser(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public boolean hasProfilePicture() {
        return hasProfilePicture;
    }

    public void setHasProfilePicture(boolean hasProfilePicture) {
        this.hasProfilePicture = hasProfilePicture;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

package com.sephrael.issuetrackingsystem.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // insert dateOrganizationJoined variable here (a somewhat working Date variable in Issue.java)

    @OneToMany(mappedBy = "organization")
    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void addToUser(User user) {
        user.setOrganization(this);
//        this.users.add(user);
    }

    @OneToMany(mappedBy = "organization")
    private List<Project> projects;

    public void addToProject(Project project) {
        project.setOrganization(this);
    }

    @Column(unique = true, updatable = false)
    private String accessKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }
}

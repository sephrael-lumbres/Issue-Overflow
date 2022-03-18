package com.sephrael.issueoverflow.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class IssueKeySequence {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long projectId;

    private String projectIdentifier;

    private int issueKeyCounter;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectIdentifier() {
        return projectIdentifier;
    }

    public void setProjectIdentifier(String projectIdentifier) {
        this.projectIdentifier = projectIdentifier;
    }

    public int getIssueKeyCounter() {
        return issueKeyCounter;
    }

    public void setIssueKeyCounter(int issueKeyCounter) {
        this.issueKeyCounter = issueKeyCounter;
    }
}

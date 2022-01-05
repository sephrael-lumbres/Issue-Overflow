package com.sephrael.issuetrackingsystem.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class IssueKeySequence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectIdentifier;

    private int issueKeyCounter;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

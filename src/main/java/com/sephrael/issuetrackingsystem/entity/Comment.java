package com.sephrael.issuetrackingsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private boolean isEdited;

    // change the format of the date and include time
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDate dateCreated;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalTime timeCreated;

    // add variable for author
    @JsonIgnore
    @ManyToOne
    @NotNull
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonIgnore
    @ManyToOne
    @NotNull
    private Issue issue;

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    // maybe add variable for userToBeNotified

    // maybe fileAttachment


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalTime getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(LocalTime timeCreated) {
        this.timeCreated = timeCreated;
    }
}

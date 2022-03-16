package com.sephrael.issueoverflow.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type;

    private Long size;

    @CreationTimestamp
    @JsonFormat(pattern = "dd MMM yyyy HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime dateCreated;

    private boolean isProfilePicture;

    @Lob
    @JsonIgnore
//    @Column(columnDefinition = "MEDIUMBLOB")
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] data;

    @ManyToOne
    @JsonIgnore
    private User user;

    @ManyToOne
    @JsonIgnore
    private Issue issue;

    public File() {
    }

    public File(String name, String type, Long size, boolean isProfilePicture, byte[] data) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.isProfilePicture = isProfilePicture;
        this.data = data;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isProfilePicture() {
        return isProfilePicture;
    }

    public void setProfilePicture(boolean isProfilePicture) {
        this.isProfilePicture = isProfilePicture;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }
}

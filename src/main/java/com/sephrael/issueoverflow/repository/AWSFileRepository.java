package com.sephrael.issueoverflow.repository;

import com.sephrael.issueoverflow.entity.AWSFile;
import com.sephrael.issueoverflow.entity.Issue;
import com.sephrael.issueoverflow.entity.Project;
import com.sephrael.issueoverflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AWSFileRepository extends JpaRepository<AWSFile, Long> {
    AWSFile findByUserAndIsProfilePictureTrue(User user);
    AWSFile findByFileKey(String fileKey);
    List<AWSFile> findByUser(User user);
    List<AWSFile> findByProject(Project project);
    List<AWSFile> findByIssue(Issue issue);
}
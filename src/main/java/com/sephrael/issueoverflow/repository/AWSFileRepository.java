package com.sephrael.issueoverflow.repository;

import com.sephrael.issueoverflow.entity.AWSFile;
import com.sephrael.issueoverflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AWSFileRepository extends JpaRepository<AWSFile, Long> {
    AWSFile findByUserAndIsProfilePictureTrue(User user);
    AWSFile findByFileKey(String fileKey);
}
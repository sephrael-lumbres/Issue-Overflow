package com.sephrael.issueoverflow.repository;

import com.sephrael.issueoverflow.entity.File;
import com.sephrael.issueoverflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    File findByUserAndIsProfilePicture(User user, boolean isProfilePicture);
}

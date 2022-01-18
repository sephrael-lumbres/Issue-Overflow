package com.sephrael.issuetrackingsystem.repository;

import com.sephrael.issuetrackingsystem.entity.File;
import com.sephrael.issuetrackingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    File findByUserAndIsProfilePicture(User user, boolean isProfilePicture);
}

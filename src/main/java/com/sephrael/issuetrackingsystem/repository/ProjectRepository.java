package com.sephrael.issuetrackingsystem.repository;

import com.sephrael.issuetrackingsystem.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p WHERE p.accessKey = ?1")
    Project findByAccessKey(String accessKey);
    Project findProjectById(Long id);
    List<Project> findAllProjectsByOrganizationId(Long id);
}

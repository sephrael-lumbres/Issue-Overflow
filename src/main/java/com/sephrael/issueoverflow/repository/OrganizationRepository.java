package com.sephrael.issueoverflow.repository;

import com.sephrael.issueoverflow.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    @Query("SELECT o FROM Organization o WHERE o.accessKey = ?1")
    Organization findByAccessKey(String accessKey);
    Organization findOrganizationById(Long id);
}

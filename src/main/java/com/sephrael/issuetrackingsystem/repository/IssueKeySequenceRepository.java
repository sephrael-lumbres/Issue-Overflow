package com.sephrael.issuetrackingsystem.repository;

import com.sephrael.issuetrackingsystem.entity.IssueKeySequence;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueKeySequenceRepository extends CrudRepository<IssueKeySequence, Long> {
    IssueKeySequence findByProjectIdentifierAndProjectId(String projectIdentifier, Long projectId);

    // OLD CODE: caused problems when more than one Project had the same Project Identifier
    //IssueKeySequence findByProjectIdentifier(String projectIdentifier);
}
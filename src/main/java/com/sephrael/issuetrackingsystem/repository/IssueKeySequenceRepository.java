package com.sephrael.issuetrackingsystem.repository;

import com.sephrael.issuetrackingsystem.entity.IssueKeySequence;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueKeySequenceRepository extends CrudRepository<IssueKeySequence, Long> {
    IssueKeySequence findByProjectIdentifier(String projectIdentifier);
}

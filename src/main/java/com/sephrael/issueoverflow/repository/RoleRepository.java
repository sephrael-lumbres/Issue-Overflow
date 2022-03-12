package com.sephrael.issueoverflow.repository;

import com.sephrael.issueoverflow.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
    Role findByName(String name);
    Role findRoleById(Long id);
}

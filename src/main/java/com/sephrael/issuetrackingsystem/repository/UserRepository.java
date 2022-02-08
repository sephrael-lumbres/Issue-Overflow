// file from https://www.codejava.net/frameworks/spring-boot/user-registration-and-login-tutorial

package com.sephrael.issuetrackingsystem.repository;

import com.sephrael.issuetrackingsystem.entity.Organization;
import com.sephrael.issuetrackingsystem.entity.Role;
import com.sephrael.issuetrackingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findByEmail(String email);
    User findUserById(Long id);
    List<User> findByOrganization(Organization organization);
    User findByResetPasswordToken(String token);
    List<User> findByOrganizationAndRole(Organization organization, Role role);

    // OLD CODE to get Users by Organization and Role
    //List<User> findByRoleNameAndOrganizationName(String role, String name);
}

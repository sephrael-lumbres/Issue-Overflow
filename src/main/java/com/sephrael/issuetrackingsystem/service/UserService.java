package com.sephrael.issuetrackingsystem.service;

import com.sephrael.issuetrackingsystem.entity.Issue;
import com.sephrael.issuetrackingsystem.entity.Project;
import com.sephrael.issuetrackingsystem.entity.Role;
import com.sephrael.issuetrackingsystem.entity.User;
import com.sephrael.issuetrackingsystem.repository.IssueRepository;
import com.sephrael.issuetrackingsystem.repository.RoleRepository;
import com.sephrael.issuetrackingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private IssueRepository issueRepository;

    public List<User> listAll() {
        return userRepository.findAll();
    }

    public void registerDefaultUser(User user) {
        Role role = roleRepository.findByName("Member");
        role.addToUser(user);
        user.setEnabled(true);

        userRepository.save(user);
    }

    public void manageUserProjects(User userToBeUpdated, User userBeforeUpdate, List<Project> projects) {
        // adds User to selected Projects if the User was not already involved
        for(Project project : projects) {

            if(!project.getUsers().contains(userToBeUpdated)) {
                project.addUser(userToBeUpdated);
            }
        }

        // removes User from unselected Projects if the User was previously involved
        for(Project project : userBeforeUpdate.getOrganization().getProjects()) {
            if(!projects.contains(project) && project.getUsers().contains(userToBeUpdated)) {
                project.removeUserFromProject(userToBeUpdated);
            }
        }
    }

    public void removeUserFromAllProjects(User userBeforeUpdate) {
        for(Project project : userBeforeUpdate.getOrganization().getProjects()) {
            if(project.getUsers().contains(userBeforeUpdate)) {
                project.removeUserFromProject(userBeforeUpdate);
            }
        }
    }

    public void unassignAllIssuesBeforeUserDeletion(User userBeforeDeletion) {
        for(Issue issue : issueRepository.findByOrganization(userBeforeDeletion.getOrganization())) {
            if(issue.getAssignedTo() == userBeforeDeletion) {
                issue.setAssignedTo(null);
            }
        }
    }

    public List<Role> listRoles() {
        return roleRepository.findAll();
    }
}

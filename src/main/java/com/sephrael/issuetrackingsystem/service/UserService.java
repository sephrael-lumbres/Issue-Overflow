package com.sephrael.issuetrackingsystem.service;

import com.sephrael.issuetrackingsystem.entity.Issue;
import com.sephrael.issuetrackingsystem.entity.Project;
import com.sephrael.issuetrackingsystem.entity.Role;
import com.sephrael.issuetrackingsystem.entity.User;
import com.sephrael.issuetrackingsystem.repository.IssueRepository;
import com.sephrael.issuetrackingsystem.repository.RoleRepository;
import com.sephrael.issuetrackingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private JavaMailSender mailSender;

    public List<User> listAll() {
        return userRepository.findAll();
    }

    public void registerDefaultUser(User user) {
        Role role = roleRepository.findByName("Member");
        role.addToUser(user);
        user.setEnabled(true);

        userRepository.save(user);
    }

    public void updateResetPasswordToken(String token, String email) {
        User user = userRepository.findByEmail(email);

        if(user != null) {
            user.setResetPasswordToken(token);
            userRepository.save(user);
        }
    }

    public User getUserByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    public void sendPasswordResetEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("lumbres.sephrael@gmail.com", "Issue Tracking System");
        helper.setTo(recipientEmail);

        String subject = "Here is the link to reset your password";

        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to reset and change your password:</p>"
                + "<p><a href=\"" + link + "\">Reset password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or if you have not made this request.</p>";

        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setResetPasswordToken(null);

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
            if(issue.getAssignedTo() != null && Objects.equals(issue.getAssignedTo().getId(), userBeforeDeletion.getId())) {
                issue.setAssignedTo(null);
            }
        }
    }

    public List<Role> listRoles() {
        return roleRepository.findAll();
    }
}

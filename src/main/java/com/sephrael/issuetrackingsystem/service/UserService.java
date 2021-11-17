package com.sephrael.issuetrackingsystem.service;

import com.sephrael.issuetrackingsystem.entity.Role;
import com.sephrael.issuetrackingsystem.entity.User;
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

    public List<User> listAll() {
        return (List<User>) userRepository.findAll();
    }

    public void registerDefaultUser(User user) {
        Role role = roleRepository.findByName("Member");
        role.addToUser(user);
        user.setEnabled(true);

        roleRepository.save(role);
        userRepository.save(user);
    }

    public List<Role> listRoles() {
        return roleRepository.findAll();
    }
}

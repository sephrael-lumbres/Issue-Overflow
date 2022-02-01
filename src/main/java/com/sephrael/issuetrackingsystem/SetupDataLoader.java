package com.sephrael.issuetrackingsystem;

import com.sephrael.issuetrackingsystem.entity.Role;
import com.sephrael.issuetrackingsystem.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    RoleRepository roleRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(alreadySetup) {
            return;
        }

        createRoleIfNotFound("Project Manager");
        createRoleIfNotFound("Admin");
        createRoleIfNotFound("Developer");
        createRoleIfNotFound("Guest");

        alreadySetup = true;
    }

    @Transactional
    void createRoleIfNotFound(String name) {
        Role role = roleRepository.findByName(name);
        if(role == null) {
            role = new Role();
            role.setName(name);
            roleRepository.save(role);
        }
    }
}

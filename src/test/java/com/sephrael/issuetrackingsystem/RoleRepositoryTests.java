package com.sephrael.issuetrackingsystem;

import static org.assertj.core.api.Assertions.assertThat;

import com.sephrael.issuetrackingsystem.entity.Role;
import com.sephrael.issuetrackingsystem.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testCreateRoles() {
        Role projectManager = new Role();
        Role admin = new Role();
        Role developer = new Role();
        Role member = new Role();

        projectManager.setName("Project Manager");
        admin.setName("Admin");
        developer.setName("Developer");
        member.setName("Member");

        roleRepository.saveAll(List.of(projectManager, admin, developer, member));

        List<Role> listRoles = roleRepository.findAll();

        assertThat(listRoles.size()).isEqualTo(4);
    }
}

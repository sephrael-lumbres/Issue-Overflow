// DOES NOT WORK YET

package com.sephrael.issueoverflow.controller;

import com.sephrael.issueoverflow.entity.Organization;
import com.sephrael.issueoverflow.entity.Project;
import com.sephrael.issueoverflow.entity.Role;
import com.sephrael.issueoverflow.entity.User;
import com.sephrael.issueoverflow.repository.*;
import com.sephrael.issueoverflow.service.UserService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    protected DataSource dataSource;

    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected UserService userService;

    @MockBean
    protected OrganizationRepository organizationRepository;

    @MockBean
    protected ProjectRepository projectRepository;

    @MockBean
    protected RoleRepository roleRepository;

    @MockBean
    protected IssueKeySequenceRepository issueKeySequenceRepository;

    @MockBean
    protected CommentRepository commentRepository;

    @MockBean
    protected FileRepository fileRepository;

    @MockBean
    protected IssueRepository issueRepository;

    List<User> users = new ArrayList<>();

    @Before
    public void setup() throws Exception {
        User johnDoe = new User();

        Organization organization = new Organization();
        organization.setName("Issue Overflow");

        Role role = new Role();
        role.setName("Project Manager");

        johnDoe.setId(1L);
        johnDoe.setFirstName("John");
        johnDoe.setLastName("Doe");
        johnDoe.setEmail("john.doe@email.com");
        johnDoe.setPassword("password");
        johnDoe.setOrganization(organization);
        role.addToUser(johnDoe);
        johnDoe.setEnabled(true);

        users.add(johnDoe);
        System.out.println(users.size());
    }

    @Test
//    @WithMockUser(username = "john.doe@email.com", password = "password", authorities = "Project Manager")
    public void userList() throws Exception {
        User johnDoe = new User();

        Organization organization = new Organization();
        organization.setId(1L);
        organization.setName("Issue Overflow");

        Role role = new Role();
        role.setName("Project Manager");

        Project project = new Project();
        project.setId(1L);
        project.setName("Call of Duty");
//        project.setOrganization(organization);
        project.setIdentifier("COD");
        project.setColor("Purple");

        johnDoe.setId(1L);
        johnDoe.setFirstName("John");
        johnDoe.setLastName("Doe");
        johnDoe.setEmail("john.doe@email.com");
        johnDoe.setPassword("password");

        johnDoe.setOrganization(organization);
        organization.addToUser(johnDoe);

        List<Project> projects = new ArrayList<>();
        projects.add(project);

        johnDoe.setProjects(projects);
        project.addUser(johnDoe);
        project.setOrganization(organization);
        organization.addToProject(project);

        role.addToUser(johnDoe);
        johnDoe.setEnabled(true);

        users.add(johnDoe);

        System.out.println(users.size());
        Principal principal = Mockito.mock(Principal.class);

        when(principal.getName()).thenReturn("john.doe@email.com");

        mockMvc.perform(get("http://localhost:8080/users").principal(principal))
                .andExpect(status().isOk())
                .andExpect(view().name("users/users-by-organization"))
                .andExpect(model().attributeExists("listUsers"));
    }

//    @Test
//    @WithMockUser(username = "john.doe@email.com", password = "password", roles = "Project Manager")
//    public void testUserList() throws Exception {
//        mockMvc.perform(get("/users"))
//                .andExpect(status().isOk());
//    }
}

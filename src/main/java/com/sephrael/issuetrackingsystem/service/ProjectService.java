package com.sephrael.issuetrackingsystem.service;

import com.sephrael.issuetrackingsystem.entity.Project;
import com.sephrael.issuetrackingsystem.entity.User;
import com.sephrael.issuetrackingsystem.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    // if the new Project's Identifier or Access Key already exists within the current User's Organization's Projects,
    // display an ERROR message
    public String isProjectIdentifierUniqueWithinOrganization(Project requestedProject, User currentUser, boolean isProjectCreation, RedirectAttributes redirectAttributes) {
        for(Project currentProject : currentUser.getOrganization().getProjects()) {
            if (!Objects.equals(currentProject.getId(), requestedProject.getId())) {
                if (Objects.equals(requestedProject.getIdentifier(), currentProject.getIdentifier())) {
                    redirectAttributes.addFlashAttribute("idNotUniqueToOrganization", "Project 'ID' already exists within Organization! Please try again!");
                    return "redirect:/projects/all";
                }
            }
        }

        // checks if User requested a Creation of a Project or an Update of an existing Project
        if(isProjectCreation) {
            requestedProject.addUser(currentUser);

            // this adds the newly created project to the current user's Organization
            currentUser.getOrganization().addToProject(requestedProject);

            projectRepository.save(requestedProject);
            redirectAttributes.addFlashAttribute("projectCreationSuccess", "Project '" + requestedProject.getName() + "'" + " has been successfully created!");
        } else {
            projectRepository.save(requestedProject);
            redirectAttributes.addFlashAttribute("projectUpdateSuccess", "Project has been successfully updated!");
        }

        return "redirect:/projects/all";
    }
}

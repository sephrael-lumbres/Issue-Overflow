package com.sephrael.issueoverflow.controller;

import com.sephrael.issueoverflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/knowledge-base")
public class KnowledgeBaseController {

    @Autowired
    private UserRepository userRepository;

    public void accessCurrentUserDetails(Model model, Principal principal) {
        if(principal != null)
            model.addAttribute("currentUser", userRepository.findByEmail(principal.getName()));
    }

    @RequestMapping("")
    public String showKnowledgeBaseHomePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/knowledge-base-home";
    }

    // ACCOUNT SETTINGS CATEGORY
    @RequestMapping("/account-settings")
    public String showAccountSettingsCategoryPage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/account-settings/category-account-settings";
    }

    // ACCOUNT SETTINGS ARTICLES
    @RequestMapping("/account-settings/edit-account-information")
    public String showEditAccountInformationArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/account-settings/articles/edit-account-information";
    }

    @RequestMapping("/account-settings/change-password")
    public String showChangePasswordArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/account-settings/articles/change-password";
    }

    @RequestMapping("/account-settings/delete-account")
    public String showDeleteAccountArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/account-settings/articles/delete-account";
    }

    // USER SETTINGS CATEGORY
    @RequestMapping("/user-settings")
    public String showUserSettingsCategoryPage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/user-settings/category-user-settings";
    }

    // USER SETTINGS ARTICLES
    @RequestMapping("/user-settings/list-of-users")
    public String showListOfUsersArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/user-settings/articles/list-of-users";
    }

    @RequestMapping("/user-settings/user-privileges")
    public String showUserPrivilegesArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/user-settings/articles/user-privileges";
    }

    @RequestMapping("/user-settings/editing-users")
    public String showEditUsersArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/user-settings/articles/editing-users";
    }

    @RequestMapping("/user-settings/removing-users-from-organization")
    public String showRemoveUserFromOrganizationArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/user-settings/articles/removing-users-from-organization";
    }

    // DASHBOARD CATEGORY
    @RequestMapping("/dashboard")
    public String showDashboardCategoryPage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/dashboard/dashboard-category";
    }

    // DASHBOARD ARTICLES
    @RequestMapping("/dashboard/dashboard-sections")
    public String showDashboardSectionsArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/dashboard/articles/dashboard-sections";
    }

    @RequestMapping("/dashboard/projects")
    public String showDashboardProjectsArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/dashboard/articles/dashboard-projects";
    }

    @RequestMapping("/dashboard/progress-tracker")
    public String showDashboardProgressTrackerArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/dashboard/articles/dashboard-progress-tracker";
    }

    @RequestMapping("/dashboard/recent-activity")
    public String showDashboardRecentActivityArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/dashboard/articles/dashboard-recent-activity";
    }

    @RequestMapping("/dashboard/my-issues")
    public String showDashboardMyIssuesArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/dashboard/articles/dashboard-my-issues";
    }

    @RequestMapping("/dashboard/status-cards")
    public String showDashboardStatusCardsArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/dashboard/articles/dashboard-status-cards";
    }

    @RequestMapping("/dashboard/members")
    public String showDashboardMembersArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/dashboard/articles/dashboard-members";
    }

    // ORGANIZATION CATEGORY
    @RequestMapping("/organization")
    public String showOrganizationCategoryPage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/organization/category-organization";
    }

    // ORGANIZATION ARTICLES
    @RequestMapping("/organization/organization-details")
    public String showOrganizationDetailsArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/organization/articles/organization-details-article";
    }

    @RequestMapping("/organization/access-key")
    public String showOrganizationAccessKeyArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/organization/articles/access-key-article";
    }

    @RequestMapping("/organization/editing-organization")
    public String showEditingOrganizationArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/organization/articles/editing-organization";
    }

    @RequestMapping("/organization/removing-users")
    public String showRemovingUsersFromOrganizationArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/organization/articles/removing-users";
    }

    // PROJECTS CATEGORY
    @RequestMapping("/projects")
    public String showProjectsCategoryPage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/projects/category-projects";
    }

    // PROJECTS ARTICLES
    @RequestMapping("/projects/list-of-projects")
    public String showListOfProjectsArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/projects/articles/list-of-projects";
    }

    @RequestMapping("/projects/project-home")
    public String showProjectHomeArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/projects/articles/project-home";
    }

    @RequestMapping("/projects/creating-projects")
    public String showCreateProjectArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/projects/articles/creating-projects";
    }

    @RequestMapping("/projects/editing-projects")
    public String showEditProjectArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/projects/articles/editing-projects";
    }

    @RequestMapping("/projects/deleting-projects")
    public String showDeleteProjectArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/projects/articles/deleting-projects";
    }

    @RequestMapping("/projects/managing-members")
    public String showManagingMembersArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/projects/articles/managing-members";
    }

    // ISSUES CATEGORY
    @RequestMapping("/issues")
    public String showIssuesCategoryPage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/issues/category-issues";
    }

    // ISSUES ARTICLES
    @RequestMapping("/issues/issue-fields")
    public String showIssueFieldsArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/issues/articles/issue-fields";
    }

    @RequestMapping("/issues/creating-issues")
    public String showCreatingIssuesArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/issues/articles/creating-issues";
    }

    @RequestMapping("/issues/adding-comments")
    public String showAddingCommentsArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/issues/articles/adding-comments";
    }

    @RequestMapping("/issues/attaching-files")
    public String showAttachingFilesArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/issues/articles/attaching-files";
    }

    @RequestMapping("/issues/list-of-issues")
    public String showListOfIssuesArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/issues/articles/list-of-issues";
    }

    @RequestMapping("/issues/filtering-issues")
    public String showFilteringIssuesArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/issues/articles/filtering-issues";
    }

    @RequestMapping("/issues/editing-issues")
    public String showEditingIssuesArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/issues/articles/editing-issues";
    }

    @RequestMapping("/issues/deleting-issues")
    public String showDeletingIssuesArticlePage(Model model, Principal principal) {
        accessCurrentUserDetails(model, principal);
        return "/knowledge-base/issues/articles/deleting-issues";
    }
}

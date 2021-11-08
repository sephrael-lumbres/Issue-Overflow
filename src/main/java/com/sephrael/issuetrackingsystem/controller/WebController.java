package com.sephrael.issuetrackingsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WebController {
    @RequestMapping(value = "/index.html", method = RequestMethod.GET)
    public String index() {return "old-index";}

    @RequestMapping(value = "/auth-password-social.html", method = RequestMethod.GET)
    public String authPasswordSocial() {return "reset-password";}

    @RequestMapping(value = "/auth-redirect.html", method = RequestMethod.GET)
    public String authRedirect() {return "auth-redirect";}

    @RequestMapping(value = "/multi-tenant-add-users.html", method = RequestMethod.GET)
    public String multiTenantAddUsers() {return "multi-tenant-add-users";}

    @RequestMapping(value = "/user-management-add-user.html", method = RequestMethod.GET)
    public String userManagementAddUser() {return "user-management-add-user";}

    @RequestMapping(value = "/user-management-edit-user.html", method = RequestMethod.GET)
    public String userManagementEditUser() {return "user-management-edit-user";}
}

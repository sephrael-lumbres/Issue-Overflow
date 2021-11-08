package com.sephrael.issuetrackingsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {
    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String dashboard() {return "index";}

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {return "landing1";}
}

package com.sephrael.issuetrackingsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/help")
public class HelpController {

    @RequestMapping("/getting-started")
    public String showGettingStartedPage() {

        return "/help/getting-started";
    }

    @RequestMapping("/knowledge-base")
    public String showKnowledgeBasePage() {

        return "/help/knowledge-base-home";
    }

    @RequestMapping("/knowledge-base/category")
    public String showKnowledgeBaseCategoryPage() {

        return "/help/knowledge-base-category";
    }
    @RequestMapping("/knowledge-base/article")
    public String showKnowledgeBaseArticlePage() {

        return "/help/knowledge-base-article";
    }

    @RequestMapping("/contact-us")
    public String showContactUsPage() {

        return "/help/contact-us";
    }
}

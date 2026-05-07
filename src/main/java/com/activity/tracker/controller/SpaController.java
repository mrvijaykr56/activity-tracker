package com.activity.tracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    /**
     * Forwards all non-API and non-static file requests to index.html.
     * This allows Angular's router to handle the routing.
     */
    @RequestMapping(value = "/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/index.html";
    }
}

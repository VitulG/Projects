package com.rggupta.HouseHopper.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    // Define the home page method to return the "index" view name (equivalent to templates/index.html)
    // Map requests to the home page
    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }
}

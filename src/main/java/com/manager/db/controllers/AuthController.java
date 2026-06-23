package com.manager.db.controllers;

import com.manager.db.models.User;
import com.manager.db.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService service;

    AuthController(UserService service){
        this.service = service;
    }

    @GetMapping("/login")
    public String showLogin (@RequestParam(value = "error", required = false) String error,
                             @RequestParam(value = "logout", required = false) String logout,
                             Model model){

        if (error != null){
            model.addAttribute("errorMessage", "Invalid email or password. Or account is blocked.");
        }
        if (logout != null){
            model.addAttribute("logoutMessage", "You have been successfully logged out.");
        }

        return "templates/public/login";

    }

    @GetMapping("/register")
    public String showRegister (Model model){

        model.addAttribute("user", new User());

        return "templates/public/register";

    }

    @PostMapping("/register")
    public String userRegister (@ModelAttribute("user") User user, Model model) {

        try {

            service.save(user);
            return "redirect:auth/login?registered=true";

        }catch (Exception e){

            model.addAttribute("error", e.getMessage());
            return "templates/public/register";

        }

    }

}

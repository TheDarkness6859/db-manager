package com.manager.db.controllers;

import com.manager.db.models.Database;
import com.manager.db.models.User;
import com.manager.db.services.DatabaseService;
import com.manager.db.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final DatabaseService databaseService;

    public AdminController(UserService userService, DatabaseService databaseService) {
        this.userService = userService;
        this.databaseService = databaseService;
    }

    @GetMapping("/users")
    public String listUsers(@RequestParam(value = "search", required = false) String search, Model model) {

        List<User> users = userService.getByNameOrEmail(search);

        model.addAttribute("users", users);

        model.addAttribute("currentSearch", search);

        return "admin/users-list";

    }

    @PostMapping("/users/{id}/block")
    public String blockUser(@PathVariable("id") UUID id) {

        userService.blockUser(id);

        return "redirect:/admin/users?success=blocked";

    }

    @PostMapping("/users/{id}/unblock")

    public String unblockUser(@PathVariable("id") UUID id) {

        userService.unblockUser(id);

        return "redirect:/admin/users?success=unblocked";

    }

    @GetMapping("/databases")
    public String listAllDatabases(Model model) {

        List<Database> allDbs = databaseService.getAllDatabase();

        model.addAttribute("databases", allDbs);

        return "admin/databases-list";
        
    }
}
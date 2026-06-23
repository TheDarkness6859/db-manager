package com.manager.db.controllers;

import com.manager.db.models.Database;
import com.manager.db.models.User;
import com.manager.db.services.DatabaseService;
import com.manager.db.services.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/dashboard")
public class UserController {

    private final DatabaseService databaseService;
    private final UserService userService;

    public UserController(DatabaseService databaseService, UserService userService) {
        this.databaseService = databaseService;
        this.userService = userService;
    }

    @GetMapping
    public String showDashboard(Principal principal, Model model) {

        String email = principal.getName();

        User userLogged = userService.getByNameOrEmail(email).get(0);

        List<Database> myDbs = databaseService.getDatabase(userLogged.getId());

        model.addAttribute("databases", myDbs);
        model.addAttribute("user", userLogged);

        return "private/dashboard";

    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {

        model.addAttribute("database", new Database());

        return "private/create-db";

    }

    @PostMapping("/new")
    public String createDatabase(@Valid @ModelAttribute("database") Database database
            , BindingResult bindingResult
            , Principal principal
            , Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Invalid fields. Please check your data type allocations.");
            return "private/create-db";
        }

        try {

            String email = principal.getName();

            User userLogged = userService.getByNameOrEmail(email).get(0);

            databaseService.save(userLogged.getId(), database);

            return "redirect:/dashboard?success=created";

        } catch (Exception e) {

            model.addAttribute("error", e.getMessage());

            return "private/create-db";

        }
    }

    @PostMapping("/{id}/archive")
    public String archive(@PathVariable("id") UUID id) {

        databaseService.archiveDatabase(id);

        return "redirect:/dashboard?success=archived";

    }
}
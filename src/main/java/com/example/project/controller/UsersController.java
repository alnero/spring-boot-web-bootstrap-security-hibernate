package com.example.project.controller;

import com.example.project.model.User;
import com.example.project.model.Role;
import com.example.project.service.RoleService;
import com.example.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UsersController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UsersController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping()
    public String listUsers(ModelMap model, Authentication authentication) {
        Iterable<User> users = userService.listUsers();
        User authenticatedUser = (User) authentication.getPrincipal();
        model.addAttribute("users", users);
        model.addAttribute("authenticatedUser", authenticatedUser);
        return "admin";
    }

    @GetMapping("/{id}")
    public String showUser(@PathVariable Long id, ModelMap model, Authentication authentication) {
        User authenticatedUser = (User) authentication.getPrincipal();
        Long authenticatedUserId = authenticatedUser.getId();
        String authenticatedUserRole = authenticatedUser.getAuthorities().iterator().next().getAuthority();
        // admin has access to any user page
        if (Role.AvailableRoles.ADMIN.name().equals(authenticatedUserRole) && !id.equals(authenticatedUserId)) {
            User user = userService.getById(id).get();
            model.addAttribute("user", user);
            return "user";
        }
        // user has access only to his own page and re-directed to his own page when trying to access pages of other users
        if (id.equals(authenticatedUserId)) {
            model.addAttribute("user", authenticatedUser);
            return "user";
        } else {
            return "redirect:/users/" + authenticatedUserId;
        }
    }

    @PostMapping("/add")
    public String add(@ModelAttribute User user, @Validated String chosenRole) {
        Role role = roleService.getByName(chosenRole);
        user.setRole(role);
        userService.add(user);
        return "redirect:/users";
    }

    @GetMapping("/edit")
    public String editPage(@RequestParam long id, ModelMap model) {
        User user = userService.getById(id).get();
        model.addAttribute("user", user);
        model.addAttribute("action", "edit");
        return "modal";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute User user, @Validated String chosenRole) {
        Role role = roleService.getByName(chosenRole);
        user.setRole(role);
        userService.edit(user);
        return "redirect:/users";
    }

    @GetMapping("/delete")
    public String deletePage(@RequestParam long id, ModelMap model) {
        User user = userService.getById(id).get();
        model.addAttribute("user", user);
        model.addAttribute("action", "delete");
        return "modal";
    }

    @PostMapping("/delete")
    public String delete(@Validated Long id) {
        userService.delete(userService.getById(id).get());
        return "redirect:/users";
    }
}

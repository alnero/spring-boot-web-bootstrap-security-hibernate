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

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public String listUsers(ModelMap model, Authentication authentication) {
        List<User> users = userService.listUsers();
        User authenticatedUser = (User) authentication.getPrincipal();
        model.addAttribute("users", users);
        model.addAttribute("authenticatedUser", authenticatedUser);
        return "admin";
    }

    @PostMapping("/users/add")
    public String add(@ModelAttribute User user, @Validated String chosenRole) {
        Set<Role> roles = roleService.getByName(chosenRole);
        user.setRoles(roles);
        userService.add(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit")
    public String editPage(@RequestParam long id, ModelMap model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        model.addAttribute("action", "edit");
        return "modal";
    }

    @PostMapping("/users/edit")
    public String edit(@ModelAttribute User user, @Validated String chosenRole) {
        Set<Role> roles = roleService.getByName(chosenRole);
        user.setRoles(roles);
        userService.edit(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete")
    public String deletePage(@RequestParam long id, ModelMap model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        model.addAttribute("action", "delete");
        return "modal";
    }

    @PostMapping("/users/delete")
    public String delete(@Validated Long id) {
        userService.delete(userService.getById(id));
        return "redirect:/admin/users";
    }
}

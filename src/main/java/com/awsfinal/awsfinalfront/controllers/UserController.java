package com.awsfinal.awsfinalfront.controllers;

import com.awsfinal.awsfinalfront.exceptions.ApiError;
import com.awsfinal.awsfinalfront.models.UserDTO;
import com.awsfinal.awsfinalfront.services.UserService;
import com.awsfinal.awsfinalfront.services.ValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.List;


@Controller
@RequestMapping("/")
public class UserController {
    private final UserService userService;
    private final ValidationService validationService;

    @Autowired
    public UserController(UserService userService, ValidationService validationService) {
        this.userService = userService;
        this.validationService = validationService;
    }

    @GetMapping("/")
    public String listUsers(Model model) {
        List<UserDTO> response = userService.getAllUsers();
        model.addAttribute("users", response);
        return "users";
    }

    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "userForm";
    }

    @GetMapping("/edit")
    public String editUserForm(@RequestParam("dni") String userDni, Model model) {
        UserDTO user = userService.getUserByDni(userDni);
        user.setEditing(true);
        model.addAttribute("user",user);
        return "userForm";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute UserDTO user, Model model) {
        try {
            ResponseEntity<UserDTO> validationResponse = validationService.validateUser(user).block();
            if (validationResponse.getStatusCode().is2xxSuccessful()) {
                UserDTO newUser = validationResponse.getBody();
                if (newUser == null) {
                    model.addAttribute("errorMessage", "Error: No se recibió un usuario válido");
                } else if (userService.getUserByDni(newUser.getDni()) == null) {
                    userService.saveUser(newUser);
                } else if (user.isEditing()) {
                    userService.patchUser(newUser);
                    user.setEditing(false);
                } else {
                    model.addAttribute("errorMessage", "DNI existente");
                }
            } else {
                model.addAttribute("errorMessage", "Error inesperado en la validación");
            }
        } catch (WebClientResponseException.BadRequest ex) {
            model.addAttribute("errorMessage", "Error de validación: " + ex.getResponseBodyAsString());
        }
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }


    @GetMapping("/delete")
    public String deleteUser(@RequestParam("dni") String dni, Model model) {
        userService.deleteUser(dni);
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

}

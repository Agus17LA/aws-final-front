package com.awsfinal.awsfinalfront.controllers;

import com.awsfinal.awsfinalfront.models.UserDTO;
import com.awsfinal.awsfinalfront.services.UserService;
import com.awsfinal.awsfinalfront.services.ValidationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

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
                if(userService.getUserByDni(newUser.getDni()) == null){ //si el dni no existe en la base de datos, significa que estamos agregando usuario nuevo
                    userService.saveUser(newUser);
                }else if(user.isEditing()){ //si figura el flag de edit, y el DNI en cuestion figura en la base de datos, sigifnica que estamos editando
                    userService.patchUser(newUser);
                    user.setEditing(false);
                }else{ //si no esta el flag activado, significa que estamos intentando agregar un nuevo usuario pero que su DNI ya existe en la base de datos
                    model.addAttribute("errorMessage", "DNI existente");
                }
                model.addAttribute("users", userService.getAllUsers());
                return "users";
            }
        }catch (Exception ex){
            model.addAttribute("errorMessage", ex.getMessage());
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

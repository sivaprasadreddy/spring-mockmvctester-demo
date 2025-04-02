package com.jetbrains.demo;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users/{id}")
    public String getUserById(@PathVariable Long id, Model model) {
        var user = userRepository.findById(id);
        if (user != null) {
            model.addAttribute("user", user);
            return "user";
        }
        return "not-found";
    }

    @GetMapping("/create-user")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User(null, "", "", ""));
        return "create-user";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        userRepository.create(user);
        if (bindingResult.hasErrors()) {
            return "create-user";
        }
        redirectAttributes.addFlashAttribute("successMessage", "User saved successfully");
        return "redirect:/users";
    }

}

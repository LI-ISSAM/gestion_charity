package com.jee.app.controllers;

import com.jee.app.model.Users;
import com.jee.app.services.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public String profilePage(HttpSession session,
                               Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "profile/index";
    }

    // ✅ Modifier infos
    @PostMapping("/update")
    public String updateProfile(
            @RequestParam String firstName,
            @RequestParam String lastName,
            HttpSession session) {

        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        userService.updateProfile(
                user.getId(), firstName, lastName);

        // ✅ Recharge le user en session
        Users updated = userService.getById(user.getId());
        session.setAttribute("user", updated);

        return "redirect:/profile?updated=true";
    }

    // ✅ Changer la photo
    @PostMapping("/picture")
    public String updatePicture(
            @RequestParam MultipartFile file,
            HttpSession session) {

        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        if (file != null && !file.isEmpty()) {
            // Vérifie que c'est une image
            String contentType = file.getContentType();
            if (contentType == null ||
                !contentType.startsWith("image/")) {
                return "redirect:/profile?error=invalid";
            }
            // Vérifie la taille (max 2MB)
            if (file.getSize() > 2 * 1024 * 1024) {
                return "redirect:/profile?error=size";
            }

            userService.updateProfilePicture(
                    user.getId(), file);

            // ✅ Recharge le user en session
            Users updated = userService.getById(user.getId());
            session.setAttribute("user", updated);
        }

        return "redirect:/profile?picture=true";
    }

    // ✅ Supprimer la photo
    @PostMapping("/picture/delete")
    public String deletePicture(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        userService.removeProfilePicture(user.getId());

        Users updated = userService.getById(user.getId());
        session.setAttribute("user", updated);

        return "redirect:/profile?pictureDeleted=true";
    }
}
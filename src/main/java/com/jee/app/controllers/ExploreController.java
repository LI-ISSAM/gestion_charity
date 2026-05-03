package com.jee.app.controllers;

import com.jee.app.enums.Category;
import com.jee.app.model.CharityAction;
import com.jee.app.model.Users;
import com.jee.app.services.CharityActionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/explore")
@RequiredArgsConstructor
public class ExploreController {

    private final CharityActionService charityActionService;

    @GetMapping
    public String explore(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "createdAt") String sort,
            HttpSession session, Model model) {

        Users user = (Users) session.getAttribute("user");
        model.addAttribute("user", user);

        // ✅ Recherche avec filtres
        Page<CharityAction> results = charityActionService
                .search(keyword, category, location, page, 9);

        model.addAttribute("actions",       results.getContent());
        model.addAttribute("currentPage",   page);
        model.addAttribute("totalPages",    results.getTotalPages());
        model.addAttribute("totalElements", results.getTotalElements());
        model.addAttribute("hasPrevious",   results.hasPrevious());
        model.addAttribute("hasNext",       results.hasNext());

        // Paramètres pour garder les filtres actifs
        model.addAttribute("keyword",       keyword);
        model.addAttribute("category",      category);
        model.addAttribute("location",      location);
        model.addAttribute("categories",    Category.values());

        return "explore/index";
    }
}
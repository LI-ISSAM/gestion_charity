package com.jee.app.controllers;

import com.jee.app.model.Organisation;
import com.jee.app.model.Users;
import com.jee.app.services.CharityActionService;
import com.jee.app.services.OrganisationService;
import com.jee.app.services.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LandingController {

    private final OrganisationService organisationService;
    private final CharityActionService charityActionService;
    private final UserService userService;

    @GetMapping("/")
    public String landing(HttpSession session, Model model,
                          @RequestParam(defaultValue = "0") int page) {

        Users user = (Users) session.getAttribute("user");
        model.addAttribute("user", user);

        if (user != null) {
            Organisation org = organisationService.getByManager(user);
            model.addAttribute("organisation", org);
        }

        Page<Organisation> orgsPage =
                organisationService.getApprovedPaged(page,3);

        model.addAttribute("approvedOrgs", orgsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orgsPage.getTotalPages());
        model.addAttribute("totalElements", orgsPage.getTotalElements());
        model.addAttribute("hasPrevious", orgsPage.hasPrevious());  
        model.addAttribute("hasNext", orgsPage.hasNext());           

        model.addAttribute("totalUsers",
            userService.countAll());
        model.addAttribute("totalActions",
            charityActionService.countActive());
        model.addAttribute("totalOrganisations",
            organisationService.countApproved());
        model.addAttribute("totalDonations", 
            charityActionService.getTotalDonations());
        

    
        return "index";
    }
}
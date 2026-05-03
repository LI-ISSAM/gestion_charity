package com.jee.app.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jee.app.dto.OrganisationDTO;
import com.jee.app.model.Organisation;
import com.jee.app.model.Users;
import com.jee.app.services.CharityActionService;
import com.jee.app.services.OrganisationService;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/organisation")
@RequiredArgsConstructor
@Controller
public class OrganisationController {
    private final CharityActionService charityActionService; 

    private final OrganisationService organisationService;

    @GetMapping("/create")
    public String createPage(HttpSession session , Model model){
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if(organisationService.getByManager(user) != null){
            return "redirect:/organisation/profile";
        }
        model.addAttribute("organisationDTO",new OrganisationDTO());
        return "organisation/create";
    }

    @PostMapping("/create")
    public String create(
        @Valid @ModelAttribute OrganisationDTO organisationDTO,
        BindingResult result,
        HttpSession session,
        Model model
    ){
        Users user = (Users) session.getAttribute("user");
        if (user == null){
            return "redirect:/login";
        }
        if(result.hasErrors()){
            return "organisation/create";
        }
        try {
            organisationService.create(organisationDTO,user);
            return "redirect:/organisation/profile?success=true";
        }catch(RuntimeException e){
            model.addAttribute("error", e.getMessage());
            return "organisation/create";
        }
        
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Organisation org = organisationService.getByManager(user);
        model.addAttribute("organisation", org);
        return "organisation/profile";
    }

    @GetMapping("/edit")
    public String editPage(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Organisation org = organisationService.getByManager(user);
        if (org == null) return "redirect:/organisation/create";

        OrganisationDTO dto = new OrganisationDTO();
        dto.setName(org.getName());
        dto.setLegalAddress(org.getLegalAddress());
        dto.setTaxId(org.getTaxId());
        dto.setDescription(org.getDescription());
        dto.setMission(org.getMission());
        dto.setWebsite(org.getWebsite());
        dto.setPhone(org.getPhone());

        model.addAttribute("organisationDTO", dto);
        model.addAttribute("orgId", org.getId());
        model.addAttribute("currentLogo", org.getLogo());
        return "organisation/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute OrganisationDTO organisationDTO,
                       BindingResult result,
                       HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        if (result.hasErrors()) return "organisation/edit";

        organisationService.update(id, organisationDTO);
        return "redirect:/organisation/profile?updated=true";
    }

    // ── Page publique d'une organisation ──────────────
@GetMapping("/{id}")
public String publicProfile(@PathVariable Long id,
                            HttpSession session,
                            Model model) {
    Organisation org = organisationService.getById(id);
    model.addAttribute("organisation", org);
    model.addAttribute("actions",
            charityActionService.getActiveByOrganisation(org));
    model.addAttribute("user", session.getAttribute("user"));
    return "organisation/public";
}
}
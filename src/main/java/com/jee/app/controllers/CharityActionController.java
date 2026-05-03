package com.jee.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.jee.app.dto.CharityActionDTO;
import com.jee.app.enums.Category;
import com.jee.app.model.CharityAction;
import com.jee.app.model.Organisation;
import com.jee.app.model.Users;
import com.jee.app.services.CharityActionService;
import com.jee.app.services.OrganisationService;
import com.jee.app.services.ParticipationService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/actions")
@RequiredArgsConstructor
public class CharityActionController {
        private final CharityActionService charityActionService;
        private final OrganisationService organisationService;
        private final ParticipationService participationService;

        @GetMapping
        public String list(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Organisation org = organisationService.getByManager(user);
        if (org == null) return "redirect:/organisation/create";

        model.addAttribute("actions",
                charityActionService.getByOrganisation(org));
        model.addAttribute("organisation", org);
        return "actions/list";
    }
        
        @GetMapping("/create")
        public String createPage(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Organisation org = organisationService.getByManager(user);
        if (org == null) return "redirect:/organisation/create";

        model.addAttribute("actionDTO", new CharityActionDTO());
        model.addAttribute("categories", Category.values());
        return "actions/create";
    }

        @PostMapping("/create")
        public String create(@Valid @ModelAttribute CharityActionDTO actionDTO,
                         BindingResult result,
                         HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        if (result.hasErrors()) {
            model.addAttribute("categories", Category.values());
            return "actions/create";
        }

        Organisation org = organisationService.getByManager(user);
        charityActionService.create(actionDTO, org);
        return "redirect:/actions?created=true";
    }

    // ── Modifier une action ────────────────────────────
    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id,
                           HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        CharityAction action = charityActionService.getById(id);

        CharityActionDTO dto = new CharityActionDTO();
        dto.setTitle(action.getTitle());
        dto.setDescription(action.getDescription());
        dto.setShortDescription(action.getShortDescription());
        dto.setCategory(action.getCategory());
        dto.setLocation(action.getLocation());
        dto.setStartDate(action.getStartDate());
        dto.setEndDate(action.getEndDate());
        dto.setTargetAmount(action.getTargetAmount());
        dto.setCurrency(action.getCurrency());

        model.addAttribute("actionDTO", dto);
        model.addAttribute("action", action);
        model.addAttribute("categories", Category.values());
        return "actions/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute CharityActionDTO actionDTO,
                       BindingResult result,
                       HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        if (result.hasErrors()) {
            model.addAttribute("categories", Category.values());
            return "actions/edit";
        }

        charityActionService.update(id, actionDTO);
        return "redirect:/actions?updated=true";
    }

    // ── Publier ────────────────────────────────────────
    @PostMapping("/publish/{id}")
    public String publish(@PathVariable Long id, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        charityActionService.publish(id);
        return "redirect:/actions?published=true";
    }

    // ── Archiver ───────────────────────────────────────
    @PostMapping("/archive/{id}")
    public String archive(@PathVariable Long id, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        charityActionService.archive(id);
        return "redirect:/actions?archived=true";
    }

    // ── Supprimer ──────────────────────────────────────
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        charityActionService.delete(id);
        return "redirect:/actions?deleted=true";
    }

    @PostMapping("/unarchive/{id}")
    public String unarchive(@PathVariable Long id) {
    charityActionService.unarchive(id);
    return "redirect:/actions?unarchived=true";
}
    // ── Détail d'une action ────────────────────────────
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                     HttpSession session, Model model) {

    Users user = (Users) session.getAttribute("user");
    if(user !=null){
        charityActionService.incrementView(id, user);
    }
    else{
        String sessionKey = "ViewedAction_"+id;
        if (session.getAttribute(sessionKey) == null) {
            CharityAction action = charityActionService.getById(id);
            session.setAttribute(sessionKey, true);
        }
    }

    CharityAction action = charityActionService.getById(id);
    model.addAttribute("action", action);
    model.addAttribute("user", session.getAttribute("user"));
    if (user != null) {
        boolean isParticipating = participationService
                .isParticipating(user, action);
        model.addAttribute("isParticipating", isParticipating);

        if (isParticipating) {
            model.addAttribute("participationStatus",
                    participationService.getStatus(user, action));
        }
    }
    
    
    
    
    return "actions/detail";
}
}

package com.jee.app.controllers;

import com.jee.app.enums.ActionStatus;
import com.jee.app.enums.OrganisationsStatus;
import com.jee.app.enums.Role;
import com.jee.app.model.*;
import com.jee.app.repositories.*;
import com.jee.app.services.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;
    private final CharityActionRepository charityActionRepository;
    private final DonationRepository donationRepository;
    private final ParticipationRepository participationRepository;
    private final OrganisationService organisationService;
    private final CharityActionService charityActionService;

    // ── Vérifie accès admin ────────────────────────────
    private boolean isAdmin(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        return user != null && user.getRole() == Role.ADMIN;
    }

    // ── Dashboard principal ────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/";

        // Stats globales
        model.addAttribute("totalUsers",
                userRepository.count());
        model.addAttribute("totalOrgs",
                organisationRepository.count());
        model.addAttribute("totalActions",
                charityActionRepository.count());
        model.addAttribute("totalDonations",
                donationRepository.count());
        model.addAttribute("totalParticipations",
                participationRepository.count());

        // Dons collectés
        double totalCollected = donationRepository.findAll()
                .stream()
                .filter(d -> d.getStatus().name()
                        .equals("COMPLETED"))
                .mapToDouble(d -> d.getAmount() != null
                        ? d.getAmount() : 0.0)
                .sum();
        model.addAttribute("totalCollected", totalCollected);

        // Orgs en attente
        List<Organisation> pendingOrgs =
                organisationRepository.findByStatus(
                        OrganisationsStatus.PENDING);
        model.addAttribute("pendingOrgs", pendingOrgs);

        // Actions actives
        long activeActions = charityActionRepository
                .findAll().stream()
                .filter(a -> a.getStatus() == ActionStatus.ACTIVE)
                .count();
        model.addAttribute("activeActions", activeActions);

        // Derniers utilisateurs
        model.addAttribute("recentUsers",
                userRepository.findAll().stream()
                        .sorted((a, b) -> b.getCreatedAt()
                                .compareTo(a.getCreatedAt()))
                        .limit(5).toList());

        // Toutes les orgs
        model.addAttribute("allOrgs",
                organisationRepository.findAll());

        model.addAttribute("user",
                session.getAttribute("user"));
        return "admin/dashboard";
    }

    // ── Gestion utilisateurs ───────────────────────────
    @GetMapping("/users")
    public String users(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/";
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("user", session.getAttribute("user"));
        return "admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                              HttpSession session) {
        if (!isAdmin(session)) return "redirect:/";
        userRepository.deleteById(id);
        return "redirect:/admin/users?deleted=true";
    }

    @PostMapping("/users/role/{id}")
    public String changeRole(@PathVariable Long id,
                              @RequestParam String role,
                              HttpSession session) {
        if (!isAdmin(session)) return "redirect:/";
        Users user = userRepository.findById(id)
                .orElseThrow();
        user.setRole(Role.valueOf(role));
        userRepository.save(user);
        return "redirect:/admin/users?updated=true";
    }

    // ── Gestion organisations ──────────────────────────
    @PostMapping("/organisations/approve/{id}")
    public String approveOrg(@PathVariable Long id,
                               HttpSession session) {
        if (!isAdmin(session)) return "redirect:/";
        organisationService.approve(id);
        return "redirect:/admin/dashboard?approved=true";
    }

    @PostMapping("/organisations/reject/{id}")
    public String rejectOrg(@PathVariable Long id,
                              HttpSession session) {
        if (!isAdmin(session)) return "redirect:/";
        organisationService.reject(id);
        return "redirect:/admin/dashboard?rejected=true";
    }

    @PostMapping("/organisations/suspend/{id}")
    public String suspendOrg(@PathVariable Long id,
                               HttpSession session) {
        if (!isAdmin(session)) return "redirect:/";
        Organisation org = organisationRepository.findById(id)
                .orElseThrow();
        org.setStatus(OrganisationsStatus.SUSPENDED);
        organisationRepository.save(org);
        return "redirect:/admin/dashboard?suspended=true";
    }

    @PostMapping("/organisations/delete/{id}")
    public String deleteOrg(@PathVariable Long id,
                              HttpSession session) {
        if (!isAdmin(session)) return "redirect:/";
        organisationService.delete(id);
        return "redirect:/admin/dashboard?deleted=true";
    }

    // ── Gestion actions ────────────────────────────────
    @GetMapping("/actions")
    public String actions(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/";
        model.addAttribute("actions",
                charityActionRepository.findAll());
        model.addAttribute("user",
                session.getAttribute("user"));
        return "admin/actions";
    }

    @PostMapping("/actions/delete/{id}")
    public String deleteAction(@PathVariable Long id,
                                HttpSession session) {
        if (!isAdmin(session)) return "redirect:/";
        charityActionRepository.deleteById(id);
        return "redirect:/admin/actions?deleted=true";
    }

    @PostMapping("/actions/archive/{id}")
    public String archiveAction(@PathVariable Long id,
                                 HttpSession session) {
        if (!isAdmin(session)) return "redirect:/";
        charityActionService.archive(id);
        return "redirect:/admin/actions?archived=true";
    }
}
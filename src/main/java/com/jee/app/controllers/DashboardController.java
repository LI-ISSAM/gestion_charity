package com.jee.app.controllers;

import com.jee.app.enums.ActionStatus;
import com.jee.app.enums.OrganisationsStatus;
import com.jee.app.model.*;
import com.jee.app.services.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final OrganisationService organisationService;
    private final CharityActionService charityActionService;
    private final DonationService donationService;
    private final ParticipationService participationService;
    private final UserService userService;

    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);

        // ✅ Organisation du user
        Organisation org = organisationService.getByManager(user);
        model.addAttribute("organisation", org);

        if (org != null) {
            // ── Stats organisation ─────────────────────
            List<CharityAction> allActions =
                    charityActionService.getByOrganisation(org);
            List<CharityAction> activeActions =
                    charityActionService.getActiveByOrganisation(org);
            List<CharityAction> archivedActions =
                    charityActionService.getArchivedByOrganisation(org);

            model.addAttribute("allActions", allActions);
            model.addAttribute("activeActions", activeActions);
            model.addAttribute("archivedActions", archivedActions);
            model.addAttribute("totalActions", allActions.size());
            model.addAttribute("totalActiveActions", activeActions.size());
            model.addAttribute("totalArchivedActions", archivedActions.size());

            // ── Total dons collectés ───────────────────
            double totalCollected = allActions.stream()
                    .mapToDouble(a -> a.getCurrentAmount() != null
                            ? a.getCurrentAmount() : 0.0)
                    .sum();
            model.addAttribute("totalCollected", totalCollected);

            // ── Total participants ─────────────────────
            int totalParticipants = allActions.stream()
                    .mapToInt(CharityAction::getParticipantCount)
                    .sum();
            model.addAttribute("totalParticipants", totalParticipants);

            // ── Total vues ─────────────────────────────
            int totalViews = allActions.stream()
                    .mapToInt(CharityAction::getViewCount)
                    .sum();
            model.addAttribute("totalViews", totalViews);

            // ── Total dons ─────────────────────────────
            int totalDonations = allActions.stream()
                    .mapToInt(CharityAction::getDonationCount)
                    .sum();
            model.addAttribute("totalDonations", totalDonations);
        }

        // ✅ Historique dons de l'utilisateur
        List<com.jee.app.model.Donation> myDonations =
                donationService.getByUser(user);
        model.addAttribute("myDonations", myDonations);

        // ✅ Historique participations
        List<com.jee.app.model.Participation> myParticipations =
                participationService.getByUser(user);
        model.addAttribute("myParticipations", myParticipations);

        return "dashboard";
    }
}
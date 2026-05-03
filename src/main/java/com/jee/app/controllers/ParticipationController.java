package com.jee.app.controllers;

import com.jee.app.model.CharityAction;
import com.jee.app.model.Users;
import com.jee.app.services.CharityActionService;
import com.jee.app.services.ParticipationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/participations")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;
    private final CharityActionService charityActionService;

    // ── Rejoindre ──────────────────────────────────────
    @PostMapping("/join/{actionId}")
    public String join(@PathVariable Long actionId,
                       HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        CharityAction action =
                charityActionService.getById(actionId);
        try {
            participationService.participate(user, action);
            return "redirect:/actions/" + actionId
                    + "?joined=true";
        } catch (RuntimeException e) {
            return "redirect:/actions/" + actionId
                    + "?alreadyJoined=true";
        }
    }

    // ── Annuler ────────────────────────────────────────
    @PostMapping("/cancel/{actionId}")
    public String cancel(@PathVariable Long actionId,
                         HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        CharityAction action =
                charityActionService.getById(actionId);
        participationService.cancel(user, action);
        return "redirect:/actions/" + actionId
                + "?cancelled=true";
    }
}
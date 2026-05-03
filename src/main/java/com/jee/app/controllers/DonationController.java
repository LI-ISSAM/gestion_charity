package com.jee.app.controllers;

import com.jee.app.model.CharityAction;
import com.jee.app.model.Donation;
import com.jee.app.model.Users;
import com.jee.app.services.CharityActionService;
import com.jee.app.services.DonationService;
import com.jee.app.services.StripeService;
import com.stripe.model.checkout.Session;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;
    private final CharityActionService charityActionService;
    private final StripeService stripeService;

    // ── Page de don ────────────────────────────────────
    @GetMapping("/donate/{actionId}")
    public String donatePage(@PathVariable Long actionId,
                             HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        CharityAction action = charityActionService.getById(actionId);
        model.addAttribute("action", action);
        return "donations/donate";
    }

    // ── Créer session Stripe ───────────────────────────
    @PostMapping("/stripe/pay/{actionId}")
    public String pay(@PathVariable Long actionId,
                      @RequestParam Double amount,
                      HttpSession session, Model model) {

        Users user = (Users) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        CharityAction action = charityActionService.getById(actionId);

        try {
            // 1. Sauvegarde la donation en PENDING
            Donation donation = donationService
                    .createPending(user, action, amount);

            // 2. Crée la session Stripe Checkout
            Session stripeSession = stripeService
                    .createCheckoutSession(amount,
                            action.getTitle(), donation.getId());

            // 3. Redirige vers Stripe
            return "redirect:" + stripeSession.getUrl();

        } catch (Exception e) {
            model.addAttribute("error",
                    "Erreur : " + e.getMessage());
            model.addAttribute("action", action);
            return "donations/donate";
        }
    }

    // ── Succès ─────────────────────────────────────────
    @GetMapping("/stripe/success")
    public String success(
            @RequestParam("session_id") String sessionId,
            @RequestParam("donation_id") Long donationId,
            Model model) {
        donationService.confirm(donationId, sessionId);
        model.addAttribute("donationId", donationId);
        return "donations/success";
    }

    // ── Annulation ─────────────────────────────────────
    @GetMapping("/stripe/cancel")
    public String cancel(@RequestParam("donation_id") Long donationId) {
        donationService.fail(donationId);
        return "donations/cancel";
    }
}
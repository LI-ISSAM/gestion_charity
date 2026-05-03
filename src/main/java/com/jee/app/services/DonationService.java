package com.jee.app.services;

import com.jee.app.enums.PaymentMethod;
import com.jee.app.enums.PaymentStatus;
import com.jee.app.model.CharityAction;
import com.jee.app.model.Donation;
import com.jee.app.model.Users;
import com.jee.app.repositories.CharityActionRepository;
import com.jee.app.repositories.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final CharityActionRepository charityActionRepository;

    public Donation createPending(Users user, CharityAction action,
                                   Double amount) {
        Donation donation = Donation.builder()
                .user(user)
                .action(action)
                .amount(amount)
                .currency("EUR")
                .paymentMethod(PaymentMethod.STRIPE)
                .status(PaymentStatus.PENDING)
                .build();
        return donationRepository.save(donation);
    }

    public void confirm(Long donationId, String sessionId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException(
                        "Donation introuvable"));

        donation.setStatus(PaymentStatus.COMPLETED);
        donation.setTransactionId(sessionId);
        donation.setDonatedAt(LocalDateTime.now());
        donationRepository.save(donation);

        CharityAction action = donation.getAction();
        double current = action.getCurrentAmount() != null
                         ? action.getCurrentAmount() : 0.0;
        action.setCurrentAmount(current + donation.getAmount());
        action.setDonationCount(action.getDonationCount() + 1);
        charityActionRepository.save(action);
    }

    public void fail(Long donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException(
                        "Donation introuvable"));
        donation.setStatus(PaymentStatus.FAILED);
        donationRepository.save(donation);
    }

    public List<Donation> getByUser(Users user) {
        return donationRepository.findByUser(user);
    }
}
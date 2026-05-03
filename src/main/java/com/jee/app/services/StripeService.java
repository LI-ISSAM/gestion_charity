package com.jee.app.services;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.public.key}")
    private String publicKey;

    @Value("${app.base.url}")
    private String baseUrl;

    public String getPublicKey() { return publicKey; }

    public Session createCheckoutSession(Double amount,
                                          String actionTitle,
                                          Long donationId)
            throws StripeException {

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(baseUrl
                        + "/donations/stripe/success"
                        + "?session_id={CHECKOUT_SESSION_ID}"
                        + "&donation_id=" + donationId)
                .setCancelUrl(baseUrl
                        + "/donations/stripe/cancel"
                        + "?donation_id=" + donationId)
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData
                                .builder()
                                .setCurrency("eur")
                                .setUnitAmount((long)(amount * 100))
                                .setProductData(
                                    SessionCreateParams.LineItem
                                        .PriceData.ProductData
                                        .builder()
                                        .setName("Don : " + actionTitle)
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build();

        return Session.create(params);
    }
}
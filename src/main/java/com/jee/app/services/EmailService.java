package com.jee.app.services;

import com.jee.app.model.CharityAction;
import com.jee.app.model.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base.url}")
    private String baseUrl;

    // ── Envoi générique ────────────────────────────────
    @Async
    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Erreur email : " + e.getMessage());
        }
    }

    // ── 1. Email de bienvenue après inscription ────────
    @Async
    public void sendWelcomeEmail(Users user) {
        String html = """
            <div style="font-family:'Segoe UI',sans-serif;
                        max-width:600px;margin:0 auto;
                        background:#fff;border-radius:16px;
                        overflow:hidden;box-shadow:0 4px 20px
                        rgba(0,0,0,0.08);">

                <!-- Header -->
                <div style="background:linear-gradient(135deg,
                            #1b4332,#2d6a4f);padding:2.5rem;
                            text-align:center;color:white;">
                    <div style="font-size:3rem;margin-bottom:0.5rem;">
                        🤝
                    </div>
                    <h1 style="margin:0;font-size:1.8rem;
                               font-weight:800;">
                        Bienvenue sur CharityApp !
                    </h1>
                    <p style="opacity:0.85;margin:0.5rem 0 0;">
                        Ensemble, faisons la différence 🌍
                    </p>
                </div>

                <!-- Body -->
                <div style="padding:2rem;">
                    <p style="font-size:1.1rem;color:#1a1a2e;">
                        Bonjour <strong>%s</strong>,
                    </p>
                    <p style="color:#555;line-height:1.7;">
                        Merci de rejoindre notre communauté CharityApp !
                        Votre inscription est bien confirmée.
                    </p>
                    <p style="color:#555;line-height:1.7;">
                        Vous pouvez dès maintenant :
                    </p>
                    <ul style="color:#555;line-height:2;">
                        <li>🔍 Explorer les actions de charité</li>
                        <li>💚 Faire des dons</li>
                        <li>👥 Participer aux initiatives</li>
                        <li>🏢 Créer votre organisation</li>
                    </ul>

                    <!-- Bouton -->
                    <div style="text-align:center;margin:2rem 0;">
                        <a href="%s"
                           style="background:#2d6a4f;color:white;
                                  padding:0.9rem 2.5rem;
                                  border-radius:10px;
                                  text-decoration:none;
                                  font-weight:700;font-size:1rem;
                                  display:inline-block;">
                            Explorer les actions →
                        </a>
                    </div>
                </div>

                <!-- Footer -->
                <div style="background:#f8f9fa;padding:1.5rem;
                            text-align:center;color:#888;
                            font-size:0.82rem;">
                    © 2026 CharityApp — Fait avec ❤️ pour le Maroc
                </div>
            </div>
            """.formatted(user.getFirstName(),
                          baseUrl + "/explore");

        sendHtml(user.getEmail(),
                "🤝 Bienvenue sur CharityApp !", html);
    }

    // ── 2. Email de confirmation de participation ──────
    @Async
    public void sendParticipationConfirmation(Users user,
                                               CharityAction action) {
        String html = """
            <div style="font-family:'Segoe UI',sans-serif;
                        max-width:600px;margin:0 auto;
                        background:#fff;border-radius:16px;
                        overflow:hidden;box-shadow:0 4px 20px
                        rgba(0,0,0,0.08);">

                <div style="background:linear-gradient(135deg,
                            #1b4332,#2d6a4f);padding:2.5rem;
                            text-align:center;color:white;">
                    <div style="font-size:3rem;">✅</div>
                    <h1 style="margin:0.5rem 0 0;font-size:1.6rem;
                               font-weight:800;">
                        Participation confirmée !
                    </h1>
                </div>

                <div style="padding:2rem;">
                    <p style="font-size:1rem;color:#1a1a2e;">
                        Bonjour <strong>%s</strong>,
                    </p>
                    <p style="color:#555;line-height:1.7;">
                        Votre participation à l'action suivante
                        a bien été enregistrée :
                    </p>

                    <!-- Carte action -->
                    <div style="background:#f8fff9;border:1.5px solid
                                #d8f3dc;border-radius:12px;
                                padding:1.2rem;margin:1.5rem 0;">
                        <h3 style="margin:0 0 0.5rem;color:#1b4332;
                                   font-size:1.1rem;">
                            💚 %s
                        </h3>
                        <p style="margin:0;color:#555;font-size:0.9rem;">
                            📍 %s
                        </p>
                        <p style="margin:0.3rem 0 0;color:#555;
                                  font-size:0.9rem;">
                            🏢 %s
                        </p>
                    </div>

                    <div style="text-align:center;margin:1.5rem 0;">
                        <a href="%s"
                           style="background:#2d6a4f;color:white;
                                  padding:0.9rem 2rem;
                                  border-radius:10px;
                                  text-decoration:none;
                                  font-weight:700;
                                  display:inline-block;">
                            Voir l'action →
                        </a>
                    </div>
                </div>

                <div style="background:#f8f9fa;padding:1.5rem;
                            text-align:center;color:#888;
                            font-size:0.82rem;">
                    © 2026 CharityApp
                </div>
            </div>
            """.formatted(
                user.getFirstName(),
                action.getTitle(),
                action.getLocation() != null
                    ? action.getLocation() : "Non précisé",
                action.getOrganisation().getName(),
                baseUrl + "/actions/" + action.getId()
            );

        sendHtml(user.getEmail(),
                "✅ Participation confirmée — " + action.getTitle(),
                html);
    }

    // ── 3. Email nouvelle action aux utilisateurs ──────
    @Async
    public void sendNewActionNotification(List<Users> users,
                                           CharityAction action) {
        String html = """
            <div style="font-family:'Segoe UI',sans-serif;
                        max-width:600px;margin:0 auto;
                        background:#fff;border-radius:16px;
                        overflow:hidden;box-shadow:0 4px 20px
                        rgba(0,0,0,0.08);">

                <div style="background:linear-gradient(135deg,
                            #1b4332,#2d6a4f);padding:2.5rem;
                            text-align:center;color:white;">
                    <div style="font-size:3rem;">🚀</div>
                    <h1 style="margin:0.5rem 0 0;font-size:1.6rem;
                               font-weight:800;">
                        Nouvelle action disponible !
                    </h1>
                </div>

                <div style="padding:2rem;">
                    <p style="color:#555;line-height:1.7;">
                        Une nouvelle action de charité vient d'être
                        publiée sur CharityApp :
                    </p>

                    <div style="background:#f8fff9;border:1.5px solid
                                #d8f3dc;border-radius:12px;
                                padding:1.5rem;margin:1.5rem 0;">
                        <span style="background:#d8f3dc;color:#2d6a4f;
                                     padding:0.2rem 0.7rem;
                                     border-radius:50px;
                                     font-size:0.78rem;
                                     font-weight:700;">
                            %s
                        </span>
                        <h3 style="margin:0.8rem 0 0.5rem;
                                   color:#1b4332;font-size:1.2rem;">
                            %s
                        </h3>
                        <p style="margin:0;color:#555;
                                  font-size:0.9rem;line-height:1.6;">
                            %s
                        </p>
                        <p style="margin:0.5rem 0 0;color:#888;
                                  font-size:0.85rem;">
                            📍 %s &nbsp;|&nbsp; 🏢 %s
                        </p>
                    </div>

                    <div style="text-align:center;margin:1.5rem 0;">
                        <a href="%s"
                           style="background:#2d6a4f;color:white;
                                  padding:0.9rem 2rem;
                                  border-radius:10px;
                                  text-decoration:none;
                                  font-weight:700;
                                  display:inline-block;">
                            Voir l'action →
                        </a>
                    </div>
                </div>

                <div style="background:#f8f9fa;padding:1.5rem;
                            text-align:center;color:#888;
                            font-size:0.82rem;">
                    © 2026 CharityApp —
                    <a href="%s" style="color:#888;">
                        Se désabonner
                    </a>
                </div>
            </div>
            """.formatted(
                action.getCategory().name(),
                action.getTitle(),
                action.getShortDescription() != null
                    ? action.getShortDescription() : "",
                action.getLocation() != null
                    ? action.getLocation() : "Non précisé",
                action.getOrganisation().getName(),
                baseUrl + "/actions/" + action.getId(),
                baseUrl + "/unsubscribe"
            );

        // ✅ Envoie à chaque utilisateur
        for (Users user : users) {
            sendHtml(user.getEmail(),
                    "🚀 Nouvelle action — " + action.getTitle(),
                    html);
        }
    }
}
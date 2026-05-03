package com.jee.app.config;

import com.jee.app.enums.Role;
import com.jee.app.model.Users;
import com.jee.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {

        String adminEmail = "litimi.dev@gmail.com";

        if (!userRepository.existsByEmail(adminEmail)) {

            Users admin = new Users();
            admin.setFirstName("Issam");
            admin.setLastName("Litimi");
            admin.setEmail(adminEmail);
            admin.setMotDePasse(passwordEncoder.encode("issam1212"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);

            System.out.println("✅ Admin créé : "
                    + adminEmail);
        } else {
            System.out.println("ℹ️ Admin déjà existant : "
                    + adminEmail);
        }
    }
}
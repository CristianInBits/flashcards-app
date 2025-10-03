package com.csindila.flashcards.auth.config;

import com.csindila.flashcards.auth.domain.AppUser;
import com.csindila.flashcards.auth.repo.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DevDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataInitializer.class);

    private final AppUserRepository users;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        final String email = "demo@local";
        final String rawPass = "Demo12345!";

        users.findByEmail(email).ifPresentOrElse(u -> {
            u.setPasswordHash(encoder.encode(rawPass)); // <-- resetea siempre en dev
            u.setRole("USER");
            users.save(u);
            log.info("[dev] Usuario demo actualizado: {}", email);
        }, () -> {
            var u = new AppUser();
            u.setEmail(email);
            u.setPasswordHash(encoder.encode(rawPass));
            u.setRole("USER");
            users.save(u);
            log.info("[dev] Usuario demo creado: {}", email);
        });
    }
}

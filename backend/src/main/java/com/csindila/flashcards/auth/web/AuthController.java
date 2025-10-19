package com.csindila.flashcards.auth.web;

import com.csindila.flashcards.auth.domain.AppUser;
import com.csindila.flashcards.auth.repo.AppUserRepository;
import com.csindila.flashcards.security.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    // === Registro ===
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        String email = normalizeEmail(req.email());

        if (users.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email ya registrado"));
        }
        if (!isStrongPassword(req.password())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Password insegura (mín. 8, mayús., minús., dígito, símbolo)"));
        }

        var u = new AppUser();
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRole("USER"); // o el que corresponda
        users.save(u);

        return ResponseEntity.ok(Map.of("message", "Usuario registrado"));
    }

    public record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank String password) {
    }

    // === Login por email ===
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        String email = normalizeEmail(req.email());
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, req.password()));

            AppUser u = users.findByEmail(email).orElseThrow();
            // Claim "roles" como lista (flexible para futuro)
            var roles = List.of(u.getRole()); // p.ej. ["USER"] o ["ADMIN"]
            var token = jwt.generate(email, Map.of("roles", roles));

            return ResponseEntity.ok(new LoginResponse(token, "Bearer", jwt.getExpirationSeconds()));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciales inválidas"));
        }
    }

    public record LoginRequest(@NotBlank String email, @NotBlank String password) {
    }

    public record LoginResponse(String token, String tokenType, long expiresInSec) {
    }

    // === Helpers ===
    private static String normalizeEmail(String raw) {
        return raw == null ? null : raw.trim().toLowerCase();
    }

    private static boolean isStrongPassword(String pwd) {
        if (pwd == null)
            return false;
        // mín. 8, al menos: 1 mayús, 1 minús, 1 dígito, 1 símbolo
        return pwd.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\w\\s]).{8,}$");
    }
}
package com.csindila.flashcards.auth.web;

import com.csindila.flashcards.auth.domain.AppUser;
import com.csindila.flashcards.auth.repo.AppUserRepository;
import com.csindila.flashcards.security.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
        if (users.existsByEmail(req.email())) {
            return ResponseEntity.status(409).body(Map.of("message", "Email ya registrado"));
        }
        // reglas mínimas de password (8+ chars, etc.)
        if (req.password().length() < 8) {
            return ResponseEntity.badRequest().body(Map.of("message", "Password mínimo 8 caracteres"));
        }
        var u = new AppUser();
        u.setEmail(req.email().toLowerCase().trim());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRole("USER");
        users.save(u);
        return ResponseEntity.ok(Map.of("message", "Usuario registrado"));
    }

    public record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank String password) {
    }

    // === Login ===
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.username(), req.password()));
            var token = jwt.generate(req.username(), Map.of("role", "USER"));
            return ResponseEntity.ok(new LoginResponse("Bearer " + token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(Map.of("message", "Credenciales inválidas"));
        }
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    public record LoginResponse(String token) {
    }
}

package com.csindila.flashcards.auth.web;

import com.csindila.flashcards.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    @Value("${app.auth.username}")
    private String cfgUser;

    @Value("${app.auth.password}")
    private String cfgPass;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        if (!cfgUser.equals(req.username()) || !cfgPass.equals(req.password())) {
            return ResponseEntity.status(401).build();
        }
        var token = jwtService.generate(cfgUser, Map.of("role", "USER"));
        return ResponseEntity.ok(new LoginResponse("Bearer " + token));
    }

    public record LoginRequest(String username, String password) {
    }

    public record LoginResponse(String token) {
    }
}

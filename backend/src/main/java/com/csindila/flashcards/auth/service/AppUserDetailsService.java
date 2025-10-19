package com.csindila.flashcards.auth.service;

import com.csindila.flashcards.auth.repo.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository users;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String email = username == null ? null : username.trim().toLowerCase(); // 👈
        var u = users.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return new User(u.getEmail(), u.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole())));
    }
}

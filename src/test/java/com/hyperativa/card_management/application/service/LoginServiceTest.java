package com.hyperativa.card_management.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hyperativa.card_management.domain.User;
import com.hyperativa.card_management.domain.port.UserRepositoryPort;
import com.hyperativa.card_management.security.JwtService;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginService service;

    @Test
    void shouldLoginSuccessfully() {

        String login = "luciano";
        String password = "123";
        String hash = "hashed";

        User user = User.builder()
                .login(login)
                .passwordHash(hash)
                .role("ADMIN")
                .build();

        when(userRepository.searchByLogin(login))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(password, hash))
                .thenReturn(true);

        when(jwtService.generateToken(login, "ADMIN"))
                .thenReturn("token123");

        String token = service.execute(login, password);

        assertEquals("token123", token);

        verify(jwtService).generateToken(login, "ADMIN");
    }

    @Test
    void shouldThrowWhenUserNotFound() {

        when(userRepository.searchByLogin("luciano"))
                .thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> service.execute("luciano", "123"));

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldThrowWhenPasswordIsInvalid() {

        User user = User.builder()
                .login("luciano")
                .passwordHash("hash")
                .role("ADMIN")
                .build();

        when(userRepository.searchByLogin("luciano"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong", "hash"))
                .thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> service.execute("luciano", "wrong"));

        verify(jwtService, never()).generateToken(any(), any());
    }
}


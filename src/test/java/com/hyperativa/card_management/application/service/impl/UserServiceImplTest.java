package com.hyperativa.card_management.application.service.impl;

import com.hyperativa.card_management.domain.User;
import com.hyperativa.card_management.infrastructure.repository.UserRepository;
import com.hyperativa.card_management.security.JwtService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void shouldLoginSuccessfully() {

        User user = new User();
        user.setLogin("luciano");
        user.setPasswordHash("encodedPassword");
        user.setRole("ADMIN");

        when(userRepository.findByLogin("luciano"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123", "encodedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken("luciano", "ADMIN"))
                .thenReturn("jwt-token");

        String token = service.login("luciano", "123");

        assertEquals("jwt-token", token);

        verify(userRepository).findByLogin("luciano");
        verify(passwordEncoder).matches("123", "encodedPassword");
        verify(jwtService).generateToken("luciano", "ADMIN");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByLogin("luciano"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.login("luciano", "123"));

        assertEquals("Invalid credentials", ex.getMessage());

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {

        User user = new User();
        user.setLogin("luciano");
        user.setPasswordHash("encodedPassword");

        when(userRepository.findByLogin("luciano"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123", "encodedPassword"))
                .thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.login("luciano", "123"));

        assertEquals("Invalid credentials", ex.getMessage());

        verify(jwtService, never()).generateToken(any(), any());
    }
}

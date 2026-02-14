package com.hyperativa.card_management.application.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hyperativa.card_management.application.service.UserService;
import com.hyperativa.card_management.domain.User;
import com.hyperativa.card_management.infrastructure.repository.UserRepository;
import com.hyperativa.card_management.security.JwtService;

@Service
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public String login(String login, String password) {
		log.info("logging in...");
		log.info("username: {} password: {}", login, password);
        Optional<User> userOp = userRepository.findByLogin(login);
        if(userOp.isEmpty())
        	log.error("Invalid credentials");
        	userOp.orElseThrow(() -> new RuntimeException("Invalid credentials"));

        User user = userOp.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
        	log.error("Invalid credentials");
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getLogin(), user.getRole());
        log.error("User authenticated! Token: {}", token);
        return token;
    }
}

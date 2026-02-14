package com.hyperativa.card_management.application.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hyperativa.card_management.application.usecase.LoginUseCase;
import com.hyperativa.card_management.domain.User;
import com.hyperativa.card_management.domain.port.UserRepositoryPort;
import com.hyperativa.card_management.security.JwtService;

@Service
public class LoginService implements LoginUseCase {

	private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public LoginService(UserRepositoryPort userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public String execute(String login, String password) {
		log.info("logging in...");
		log.info("username: {} password: {}", login, password);
        Optional<User> userOp = userRepository.searchByLogin(login);
        if(userOp.isEmpty())
        	log.error("Invalid credentials");
        	userOp.orElseThrow(() -> new RuntimeException("Invalid credentials"));

        User user = userOp.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
        	log.error("Invalid credentials");
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getLogin(), user.getRole());
        log.info("User authenticated! Token: {}", token);
        return token;
    }
}

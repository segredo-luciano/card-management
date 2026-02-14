package com.hyperativa.card_management.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hyperativa.card_management.api.dto.request.AuthRequest;
import com.hyperativa.card_management.api.dto.response.AuthResponse;
import com.hyperativa.card_management.application.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService authService) {
        this.userService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {    	    		
		String token = userService.login(request.login(), request.password());
		
		return ResponseEntity.ok(new AuthResponse(token));    	
    }
}


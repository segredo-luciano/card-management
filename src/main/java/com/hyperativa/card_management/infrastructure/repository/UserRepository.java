package com.hyperativa.card_management.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyperativa.card_management.domain.User;

public interface UserRepository extends JpaRepository<User, String> {
	
	Optional<User> findByLogin(String login);
}

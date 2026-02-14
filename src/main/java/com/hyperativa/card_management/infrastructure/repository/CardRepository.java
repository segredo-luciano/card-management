package com.hyperativa.card_management.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyperativa.card_management.domain.Card;

public interface CardRepository extends JpaRepository<Card, String> {
	
	Optional<Card> findByCardHashAndActiveTrue(String cardHash);
}

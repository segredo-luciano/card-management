package com.hyperativa.card_management.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hyperativa.card_management.domain.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, String> {
	
	Optional<Card> findByCardHashAndActiveTrue(String cardHash);
}

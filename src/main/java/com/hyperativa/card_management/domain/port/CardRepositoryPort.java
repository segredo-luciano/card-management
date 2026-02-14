package com.hyperativa.card_management.domain.port;

import java.util.List;
import java.util.Optional;

import com.hyperativa.card_management.domain.Card;

public interface CardRepositoryPort {

    Optional<Card> findByHash(String hash);

    Card save(Card card);
    
    List<Card> saveAll(List<Card> cards);
}


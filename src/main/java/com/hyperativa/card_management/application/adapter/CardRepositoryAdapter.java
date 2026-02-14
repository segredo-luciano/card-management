package com.hyperativa.card_management.application.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.hyperativa.card_management.domain.Card;
import com.hyperativa.card_management.domain.port.CardRepositoryPort;
import com.hyperativa.card_management.infrastructure.repository.CardRepository;

@Repository
public class CardRepositoryAdapter implements CardRepositoryPort {

    private final CardRepository repository;

    public CardRepositoryAdapter(CardRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Card> findByHash(String hash) {
        return repository.findByCardHashAndActiveTrue(hash);
    }

    @Override
    public Card save(Card card) {
        return repository.save(card);
    }
    
    @Override
    public List<Card> saveAll(List<Card> cards) {
        return repository.saveAll(cards);
    }
}


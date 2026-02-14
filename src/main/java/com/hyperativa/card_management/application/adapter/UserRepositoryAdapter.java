package com.hyperativa.card_management.application.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.hyperativa.card_management.domain.User;
import com.hyperativa.card_management.domain.port.UserRepositoryPort;
import com.hyperativa.card_management.infrastructure.repository.UserRepository;

@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {

	private final UserRepository repository;

    public UserRepositoryAdapter(UserRepository repository) {
        this.repository = repository;
    }
	
	@Override
	public Optional<User> searchByLogin(String login) {
		return repository.findByLogin(login);
	}

}

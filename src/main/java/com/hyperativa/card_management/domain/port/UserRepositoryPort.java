package com.hyperativa.card_management.domain.port;

import java.util.Optional;

import com.hyperativa.card_management.domain.User;

public interface UserRepositoryPort {

	Optional<User> searchByLogin(String login);
}

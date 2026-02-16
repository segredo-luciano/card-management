package com.hyperativa.card_management.application.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hyperativa.card_management.domain.User;
import com.hyperativa.card_management.infrastructure.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserRepositoryAdapter adapter;

    @Test
    void shouldReturnUserWhenSearchByLoginExists() {
        String login = "luciano";

        User user = new User();
        user.setLogin(login);

        when(repository.findByLogin(login)).thenReturn(Optional.of(user));

        Optional<User> result = adapter.searchByLogin(login);

        assertTrue(result.isPresent());
        assertEquals(login, result.get().getLogin());

        verify(repository).findByLogin(login);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnEmptyWhenSearchByLoginDoesNotExist() {
        String login = "luciano";

        when(repository.findByLogin(login)).thenReturn(Optional.empty());

        Optional<User> result = adapter.searchByLogin(login);

        assertTrue(result.isEmpty());

        verify(repository).findByLogin(login);
        verifyNoMoreInteractions(repository);
    }
}


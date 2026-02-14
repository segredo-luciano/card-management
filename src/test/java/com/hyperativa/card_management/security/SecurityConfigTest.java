package com.hyperativa.card_management.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Test
    void shouldAllowAccessToAuthEndpoint() throws Exception {

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "login": "admin",
                      "password": "card$management"
                    }
                """))
                .andExpect(result ->
                    assertNotEquals(403, result.getResponse().getStatus())
                );
    }


    @Test
    void shouldReturn403ForProtectedEndpointWithoutToken() throws Exception {

        mockMvc.perform(get("/cards?cardNumber=123"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldLoadBCryptPasswordEncoder() {

        String encoded = passwordEncoder.encode("123");

        assertThat(passwordEncoder.matches("123", encoded)).isTrue();
    }
}
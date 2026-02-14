package com.hyperativa.card_management.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperativa.card_management.api.dto.request.CreateCardRequest;
import com.hyperativa.card_management.api.dto.response.BatchInsertResponse;
import com.hyperativa.card_management.api.dto.response.CardResponse;
import com.hyperativa.card_management.application.service.CardService;
import com.hyperativa.card_management.domain.Card;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();
    }

    @Test
    void shouldReturnCardWhenCardExists() throws Exception {

        CardResponse response = new CardResponse("123");

        when(cardService.handleCardExistence("123"))
                .thenReturn(response);

        mockMvc.perform(get("/cards")
                        .param("cardNumber", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardId").value("123"));
    }

    @Test
    void shouldCreateCard() throws Exception {

        CreateCardRequest request =
                new CreateCardRequest(10L, 20, "9999");

        Card card = new Card();
        card.setId("123f456h");

        when(cardService.createCard(10L, 20, "9999"))
                .thenReturn(card);

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cardId").value("123f456h"));
    }

    @Test
    void shouldProcessBatchFile() throws Exception {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "cards.txt",
                        "text/plain",
                        "file-content".getBytes()
                );

        BatchInsertResponse response =
                new BatchInsertResponse(10, 8, 2, 0, 2345);

        when(cardService.processFile(file))
                .thenReturn(response);

        mockMvc.perform(multipart("/cards/batch")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProcessed").value(10))
                .andExpect(jsonPath("$.inserted").value(8))
                .andExpect(jsonPath("$.duplicates").value(2))
                .andExpect(jsonPath("$.failed").value(0))
                .andExpect(jsonPath("$.processingTimeMs").value(2345));                
    }
}


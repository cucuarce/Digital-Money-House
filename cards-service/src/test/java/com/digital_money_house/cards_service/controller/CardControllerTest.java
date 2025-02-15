package com.digital_money_house.cards_service.controller;

import com.digital_money_house.cards_service.config.jwt.JwtAuthenticationFilter;
import com.digital_money_house.cards_service.dto.request.CardRequestDto;
import com.digital_money_house.cards_service.dto.response.CardResponseDto;
import com.digital_money_house.cards_service.entity.CardIssuer;
import com.digital_money_house.cards_service.entity.CardType;
import com.digital_money_house.cards_service.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = CardController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})
@AutoConfigureMockMvc(addFilters = false)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @Autowired
    private ObjectMapper objectMapper;

    private CardResponseDto mockCard;
    private CardRequestDto cardRequestDto;


    @BeforeEach
    void setUp() {
        mockCard = new CardResponseDto();
        mockCard.setId(1L);
        mockCard.setAccountId(1L);
        mockCard.setCardNumber("1234567812345678");

        cardRequestDto = new CardRequestDto();
        cardRequestDto.setAccountId(1L);
        cardRequestDto.setCardNumber("1234567812345678");
        cardRequestDto.setCardHolderName("John Doe");
        cardRequestDto.setExpirationDate("12/25");
        cardRequestDto.setCardType(CardType.CREDIT);
        cardRequestDto.setIssuer(CardIssuer.VISA);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetCardsByAccount_Success() throws Exception {
        when(cardService.findByAccountId(1L)).thenReturn(List.of(mockCard));

        mockMvc.perform(get("/cards/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].cardNumber").value(mockCard.getCardNumber()));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetCardById_Success() throws Exception {
        when(cardService.findById(1L)).thenReturn(mockCard);

        mockMvc.perform(get("/cards/api/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockCard.getId()));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetCardByAccountAndId_Success() throws Exception {
        when(cardService.findByAccountIdAndCardId(1L, 1L)).thenReturn(mockCard);

        mockMvc.perform(get("/cards/api/accounts/1/cards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockCard.getId()));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testCreateCard_Success() throws Exception {
        doNothing().when(cardService).create(any(CardRequestDto.class));

        mockMvc.perform(post("/cards/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Tarjeta creada exitosamente."));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testDeleteCard_Success() throws Exception {
        doNothing().when(cardService).delete(1L);

        mockMvc.perform(delete("/cards/api/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tarjeta eliminada exitosamente."));
    }
}


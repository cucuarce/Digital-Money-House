package com.digital_money_house.cards_service.service;

import com.digital_money_house.cards_service.dto.request.CardRequestDto;
import com.digital_money_house.cards_service.dto.response.CardResponseDto;
import com.digital_money_house.cards_service.entity.Card;
import com.digital_money_house.cards_service.entity.CardIssuer;
import com.digital_money_house.cards_service.entity.CardType;
import com.digital_money_house.cards_service.exception.ResourceAlreadyExistsException;
import com.digital_money_house.cards_service.exception.ResourceNotFoundException;
import com.digital_money_house.cards_service.feign.AccountClient;
import com.digital_money_house.cards_service.repository.ICardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private ICardRepository cardRepository;

    @Mock
    private AccountClient accountClient;

    @InjectMocks
    private CardService cardService;

    private Card card;

    @BeforeEach
    void setUp() {
        card = new Card();
        card.setId(1L);
        card.setAccountId(100L);
        card.setCardNumber("1234567812345678");
        card.setCardHolderName("Max Payne");
        card.setCardType(CardType.CREDIT);
        card.setIssuer(CardIssuer.VISA);
        card.setExpirationDate("12/25");
    }

    @Test
    void testCreateCard_Success() {
        when(accountClient.getAccountById(anyLong())).thenReturn(null); // Simula que la cuenta existe
        when(cardRepository.existsByCardNumberAndAccountId(any(), any())).thenReturn(false);
        when(cardRepository.save(any())).thenReturn(card);

        CardRequestDto requestDto = new CardRequestDto();
        requestDto.setAccountId(100L);
        requestDto.setCardNumber("1234567812345678");
        requestDto.setCardHolderName("Max Payne");
        requestDto.setCardType(CardType.CREDIT);
        requestDto.setIssuer(CardIssuer.VISA);
        requestDto.setExpirationDate("12/25");

        assertDoesNotThrow(() -> cardService.create(requestDto));

        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void testCreateCard_AlreadyExists() {
        when(accountClient.getAccountById(anyLong())).thenReturn(null);
        when(cardRepository.existsByCardNumberAndAccountId(any(), any())).thenReturn(true);

        CardRequestDto requestDto = new CardRequestDto();
        requestDto.setAccountId(100L);
        requestDto.setCardNumber("1234567812345678");
        requestDto.setCardHolderName("Max Payne");
        requestDto.setCardType(CardType.CREDIT);
        requestDto.setIssuer(CardIssuer.VISA);
        requestDto.setExpirationDate("12/25");

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> cardService.create(requestDto));

        assertEquals("La tarjeta ya está asociada a esta cuenta.", exception.getMessage());

        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void testFindById_Success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        CardResponseDto responseDto = cardService.findById(1L);

        assertNotNull(responseDto);
        assertEquals(card.getCardNumber(), responseDto.getCardNumber());
    }

    @Test
    void testFindById_NotFound() {
        when(cardRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cardService.findById(99L));
    }

    @Test
    void testDeleteById_Success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        doNothing().when(cardRepository).delete(card);

        assertDoesNotThrow(() -> cardService.delete(1L));
        verify(cardRepository, times(1)).delete(card);
    }

    @Test
    void testDeleteById_NotFound() {
        when(cardRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cardService.delete(99L));
    }

    @Test
    void testListAllCards_NotFound() {
        when(cardRepository.findByAccountId(anyLong())).thenReturn(Collections.emptyList());

        List<CardResponseDto> result = cardService.findByAccountId(100L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByAccountIdAndCardId_Success() {
        when(cardRepository.findByAccountIdAndId(100L, 1L)).thenReturn(Optional.of(card));

        CardResponseDto responseDto = cardService.findByAccountIdAndCardId(100L, 1L);

        assertNotNull(responseDto);
        assertEquals(card.getCardNumber(), responseDto.getCardNumber());
    }

    @Test
    void testFindByAccountIdAndCardId_NotFound() {
        when(cardRepository.findByAccountIdAndId(100L, 99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cardService.findByAccountIdAndCardId(100L, 99L));
    }
}

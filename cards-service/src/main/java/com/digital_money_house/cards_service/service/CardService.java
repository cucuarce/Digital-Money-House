package com.digital_money_house.cards_service.service;

import com.digital_money_house.cards_service.dto.request.CardRequestDto;
import com.digital_money_house.cards_service.dto.response.CardResponseDto;
import com.digital_money_house.cards_service.entity.Card;
import com.digital_money_house.cards_service.exception.ResourceAlreadyExistsException;
import com.digital_money_house.cards_service.exception.ResourceNotFoundException;
import com.digital_money_house.cards_service.feign.AccountClient;
import com.digital_money_house.cards_service.repository.ICardRepository;
import com.digital_money_house.cards_service.utils.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CardService {

    private final ICardRepository cardRepository;
    private final AccountClient accountClient;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void create(CardRequestDto requestDto) {

        // Verificar si la cuenta existe antes de asociar la tarjeta
        accountClient.getAccountById(requestDto.getAccountId());

        // Normalizar y validar el nombre del titular
        String normalizedHolderName = ValidationUtils.normalizeCardHolderName(requestDto.getCardHolderName());

        // Validar el número de tarjeta
        ValidationUtils.validateCardNumber(requestDto.getCardNumber());

        // Validar la fecha de expiración
        ValidationUtils.validateExpirationDate(requestDto.getExpirationDate());

        // Verificar si la tarjeta ya está asociada a otra cuenta
        boolean exists = cardRepository.existsByCardNumberAndAccountId(requestDto.getCardNumber(), requestDto.getAccountId());
        if (exists) {
            throw new ResourceAlreadyExistsException("La tarjeta ya está asociada a esta cuenta.", HttpStatus.CONFLICT.value());
        }

        // Convertir DTO a entidad y guardar
        Card newCard = objectMapper.convertValue(requestDto, Card.class);
        newCard.setCardHolderName(normalizedHolderName);
        newCard.setCreatedDate(LocalDate.now());
        newCard.setCratedTime(LocalTime.now());

        cardRepository.save(newCard);
    }

    public List<CardResponseDto> findByAccountId(Long accountId) {
        List<Card> cards = cardRepository.findByAccountId(accountId);

        return cards.stream()
                .map(card -> objectMapper.convertValue(card, CardResponseDto.class))
                .collect(Collectors.toList());
    }

    public CardResponseDto findById(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada.", HttpStatus.NOT_FOUND.value()));

        return objectMapper.convertValue(card, CardResponseDto.class);
    }

    public CardResponseDto findByAccountIdAndCardId(Long accountId, Long cardId) {
        Card card = cardRepository.findByAccountIdAndId(accountId, cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada para esta cuenta.", HttpStatus.NOT_FOUND.value()));

        return objectMapper.convertValue(card, CardResponseDto.class);
    }

    @Transactional
    public void delete(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada.", HttpStatus.NOT_FOUND.value()));

        cardRepository.delete(card);
    }
}

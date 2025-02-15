package com.digital_money_house.cards_service.controller;

import com.digital_money_house.cards_service.dto.JsonMessageDto;
import com.digital_money_house.cards_service.dto.request.CardRequestDto;
import com.digital_money_house.cards_service.dto.response.CardResponseDto;
import com.digital_money_house.cards_service.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Comentar para que me permita las solicitudes desde el gateway al front
@RestController
@RequestMapping("/cards/api")
public class CardController {

    private final CardService cardService;

    @GetMapping("/accounts/{id}")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<List<CardResponseDto>> getCardsByAccount(@PathVariable Long id) {
        return new ResponseEntity<>(cardService.findByAccountId(id), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<CardResponseDto> getCardById(@PathVariable Long id) {
        return new ResponseEntity<>(cardService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/accounts/{accountId}/cards/{cardId}")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<CardResponseDto> getCardByAccountAndId(
            @PathVariable Long accountId,
            @PathVariable Long cardId) {

        return new ResponseEntity<>(cardService.findByAccountIdAndCardId(accountId, cardId), HttpStatus.OK);
    }

    @PostMapping
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<?> createCard(@RequestBody @Valid CardRequestDto cardRequestDto) {
        cardService.create(cardRequestDto);
        return new ResponseEntity<>(new JsonMessageDto("Tarjeta creada exitosamente.", HttpStatus.CREATED.value()), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<?> deleteCard(@PathVariable Long id) {
        cardService.delete(id);
        return new ResponseEntity<>(new JsonMessageDto("Tarjeta eliminada exitosamente.", HttpStatus.OK.value()), HttpStatus.OK);
    }

}

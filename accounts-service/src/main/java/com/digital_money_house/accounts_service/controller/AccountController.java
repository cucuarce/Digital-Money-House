package com.digital_money_house.accounts_service.controller;

import com.digital_money_house.accounts_service.dto.JsonMessageDto;
import com.digital_money_house.accounts_service.dto.request.AccountRequestDto;
import com.digital_money_house.accounts_service.dto.response.AccountResponseDto;
import com.digital_money_house.accounts_service.dto.response.CardClientDto;
import com.digital_money_house.accounts_service.dto.response.TransactionClientDto;
import com.digital_money_house.accounts_service.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@CrossOrigin(origins = "*") //Para permitir las solicitudes desde el gateway al front comentar la anotacion
@RestController
@RequestMapping("/accounts/api")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<AccountResponseDto> getAccountById(@PathVariable Long id) {
        return new ResponseEntity<>(accountService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<List<AccountResponseDto>> getAccountsByUserId(@PathVariable Long id) {
        return new ResponseEntity<>(accountService.findByUserId(id), HttpStatus.OK);
    }

    @GetMapping
    @Secured("ADMIN")
    public ResponseEntity<List<AccountResponseDto>> listAccounts() {
        return new ResponseEntity<>(accountService.listAll(), HttpStatus.OK);
    }

    @PostMapping
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<?> createAccount(@RequestBody @Valid AccountRequestDto accountRequestDto) {
        accountService.create(accountRequestDto);
        return new ResponseEntity<>(new JsonMessageDto("Cuenta creada exitosamente.", HttpStatus.CREATED.value()), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        accountService.deleteById(id);
        return new ResponseEntity<>(new JsonMessageDto("Cuenta eliminada exitosamente.", HttpStatus.OK.value()), HttpStatus.OK);
    }

    @GetMapping("/{id}/transactions")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<List<TransactionClientDto>> getAccountTransactions(@PathVariable Long id) {
        return new ResponseEntity<>(accountService.findLastFiveTransactionsByAccountId(id), HttpStatus.OK);
    }

    @GetMapping("/{accountId}/cards")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<List<CardClientDto>> getCardsByAccount(@PathVariable Long accountId) {
        return new ResponseEntity<>(accountService.getCardsByAccountId(accountId), HttpStatus.OK);
    }

    @GetMapping("/{accountId}/cards/{cardId}")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<CardClientDto> getCardById(@PathVariable Long accountId, @PathVariable Long cardId) {
        return new ResponseEntity<>(accountService.getCardById(accountId, cardId), HttpStatus.OK);
    }

    @GetMapping("/{accountId}/transactions/{transactionId}")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<TransactionClientDto> getTransactionById(@PathVariable Long accountId, @PathVariable Long transactionId) {
        return new ResponseEntity<>(accountService.getTransactionById(accountId, transactionId), HttpStatus.OK);
    }

    @DeleteMapping("/{accountId}/cards/{cardId}")
    @Secured({ "USER" })
    public ResponseEntity<?> deleteCardFromAccount(@PathVariable Long accountId, @PathVariable Long cardId) {
        accountService.deleteCardFromAccount(accountId, cardId);
        return new ResponseEntity<>(new JsonMessageDto("Tarjeta eliminada correctamente.", HttpStatus.OK.value()), HttpStatus.OK);
    }

    @PostMapping("/{accountId}/transactions")
    @Secured({ "USER" })
    public ResponseEntity<?> createTransaction(
            @PathVariable Long accountId,
            @RequestBody @Valid TransactionClientDto transactionRequestDto) {

        transactionRequestDto.setAccountId(accountId);
        accountService.createTransaction(transactionRequestDto);
        return new ResponseEntity<>(new JsonMessageDto("Tipo de transacción: " + transactionRequestDto.getTransactionType() + ". Estado: " + transactionRequestDto.getStatus(), HttpStatus.CREATED.value()), HttpStatus.CREATED);
    }

    @PatchMapping
    @Secured({ "USER" })
    public ResponseEntity<?> updateAlias(@RequestBody AccountRequestDto accountRequestDto) {
        accountService.updateAlias(accountRequestDto);
        return new ResponseEntity<>(new JsonMessageDto("Alias actualizado exitosamente.", HttpStatus.OK.value()), HttpStatus.OK);
    }

}

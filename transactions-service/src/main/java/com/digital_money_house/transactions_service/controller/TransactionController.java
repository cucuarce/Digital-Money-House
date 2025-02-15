package com.digital_money_house.transactions_service.controller;

import com.digital_money_house.transactions_service.dto.JsonMessageDto;
import com.digital_money_house.transactions_service.dto.request.TransactionRequestDto;
import com.digital_money_house.transactions_service.dto.response.TransactionResponseDto;
import com.digital_money_house.transactions_service.service.TransactionService;
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
@RequestMapping("/transactions/api")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{id}")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<TransactionResponseDto> getTransactionById(@PathVariable Long id) {
        return new ResponseEntity<>(transactionService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/last-five/{accountId}")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<List<TransactionResponseDto>> getLastFiveTransactions(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getLastFiveTransactions(accountId));
    }

    @GetMapping("/accounts/{id}")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<List<TransactionResponseDto>> getTransactionsByAccountId(@PathVariable Long id) {
        return new ResponseEntity<>(transactionService.findByAccountId(id), HttpStatus.OK);
    }

    @GetMapping("/accounts/{accountId}/transactions/{transactionId}")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<TransactionResponseDto> getTransactionByAccountAndId(
            @PathVariable Long accountId,
            @PathVariable Long transactionId) {

        return new ResponseEntity<>(transactionService.findByAccountIdAndTransactionId(accountId, transactionId), HttpStatus.OK);
    }

    @PostMapping
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<?> createTransaction(@RequestBody @Valid TransactionRequestDto transactionRequestDto) {
        transactionService.create(transactionRequestDto);
        return new ResponseEntity<>(new JsonMessageDto("Tipo de transacción: " + transactionRequestDto.getTransactionType() + ". Estado: " + transactionRequestDto.getStatus(), HttpStatus.CREATED.value()), HttpStatus.CREATED);
    }

}

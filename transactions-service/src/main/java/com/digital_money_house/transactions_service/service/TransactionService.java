package com.digital_money_house.transactions_service.service;

import com.digital_money_house.transactions_service.dto.request.TransactionRequestDto;
import com.digital_money_house.transactions_service.dto.response.AccountClientDto;
import com.digital_money_house.transactions_service.dto.response.TransactionResponseDto;
import com.digital_money_house.transactions_service.entity.Transaction;
import com.digital_money_house.transactions_service.entity.TransactionStatus;
import com.digital_money_house.transactions_service.exception.RequestValidationException;
import com.digital_money_house.transactions_service.exception.ResourceNotFoundException;
import com.digital_money_house.transactions_service.repository.ITransactionRepository;
import com.digital_money_house.transactions_service.utils.MapperClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final ITransactionRepository transactionRepository;
    private static final ObjectMapper objectMapper = MapperClass.objectMapper();

    public TransactionResponseDto findById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada.", HttpStatus.NOT_FOUND.value()));

        return objectMapper.convertValue(transaction, TransactionResponseDto.class);
    }

    public List<TransactionResponseDto> findByAccountId(Long id) {
        List<Transaction> transactions = transactionRepository.findByAccountId(id);
        if (transactions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron transacciones para el usuario.", HttpStatus.NOT_FOUND.value());
        }

        return transactions.stream()
                .map(transaction -> objectMapper.convertValue(transaction, TransactionResponseDto.class))
                .collect(Collectors.toList());
    }

    public List<TransactionResponseDto> getLastFiveTransactions(Long accountId) {
        List<Transaction> transactions = transactionRepository.findTop5ByAccountIdOrderByCreatedDateDescCreatedTimeDesc(accountId);

        /*if (transactions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron transacciones para el usuario.", HttpStatus.NOT_FOUND.value());
        }*/

        return transactions.stream()
                .map(transaction -> objectMapper.convertValue(transaction, TransactionResponseDto.class))
                .collect(Collectors.toList());
    }

    public TransactionResponseDto findByAccountIdAndTransactionId(Long accountId, Long transactionId) {
        Transaction transaction = transactionRepository.findByAccountIdAndId(accountId, transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada para esta cuenta.", HttpStatus.NOT_FOUND.value()));

        return objectMapper.convertValue(transaction, TransactionResponseDto.class);
    }

    @Transactional
    public void create(TransactionRequestDto transactionRequestDto) {

        if (transactionRequestDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RequestValidationException("El monto debe ser mayor a 0.", HttpStatus.BAD_REQUEST.value());
        }

        Transaction transaction = objectMapper.convertValue(transactionRequestDto, Transaction.class);
        transaction.setCreatedDate(LocalDate.now());
        transaction.setCreatedTime(LocalTime.now());
        transaction.setStatus(
                transactionRequestDto.getStatus() != null ? transactionRequestDto.getStatus() : TransactionStatus.PENDING
        );

        transactionRepository.save(transaction);
    }
}

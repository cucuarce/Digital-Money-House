package com.digital_money_house.transactions_service.service;

import com.digital_money_house.transactions_service.dto.request.TransactionRequestDto;
import com.digital_money_house.transactions_service.dto.response.TransactionResponseDto;
import com.digital_money_house.transactions_service.entity.Transaction;
import com.digital_money_house.transactions_service.entity.TransactionStatus;
import com.digital_money_house.transactions_service.entity.TransactionType;
import com.digital_money_house.transactions_service.exception.RequestValidationException;
import com.digital_money_house.transactions_service.exception.ResourceNotFoundException;
import com.digital_money_house.transactions_service.repository.ITransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private ITransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAccountId(100L);
        transaction.setTransactionType(TransactionType.INCOME);
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setStatus(TransactionStatus.CONFIRMED);
        transaction.setCreatedDate(LocalDate.now());
        transaction.setCreatedTime(LocalTime.now());
    }

    @Test
    void testFindById_Success() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        TransactionResponseDto responseDto = transactionService.findById(1L);

        assertNotNull(responseDto);
        assertEquals(transaction.getAmount(), responseDto.getAmount());
    }

    @Test
    void testFindById_NotFound() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.findById(99L));
    }

    @Test
    void testFindByAccountId_Success() {
        when(transactionRepository.findByAccountId(100L)).thenReturn(List.of(transaction));

        List<TransactionResponseDto> transactions = transactionService.findByAccountId(100L);

        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
        assertEquals(transaction.getAmount(), transactions.get(0).getAmount());
    }

    @Test
    void testFindByAccountId_NotFound() {
        when(transactionRepository.findByAccountId(100L)).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.findByAccountId(100L));
    }

    @Test
    void testGetLastFiveTransactions_Success() {
        when(transactionRepository.findTop5ByAccountIdOrderByCreatedDateDescCreatedTimeDesc(100L))
                .thenReturn(List.of(transaction));

        List<TransactionResponseDto> transactions = transactionService.getLastFiveTransactions(100L);

        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
        assertEquals(transaction.getAmount(), transactions.get(0).getAmount());
    }

    @Test
    void testGetLastFiveTransactions_EmptyList() {
        when(transactionRepository.findTop5ByAccountIdOrderByCreatedDateDescCreatedTimeDesc(100L))
                .thenReturn(Collections.emptyList());

        List<TransactionResponseDto> transactions = transactionService.getLastFiveTransactions(100L);

        assertTrue(transactions.isEmpty());
    }

    @Test
    void testFindByAccountIdAndTransactionId_Success() {
        when(transactionRepository.findByAccountIdAndId(100L, 1L)).thenReturn(Optional.of(transaction));

        TransactionResponseDto responseDto = transactionService.findByAccountIdAndTransactionId(100L, 1L);

        assertNotNull(responseDto);
        assertEquals(transaction.getAmount(), responseDto.getAmount());
    }

    @Test
    void testFindByAccountIdAndTransactionId_NotFound() {
        when(transactionRepository.findByAccountIdAndId(100L, 99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.findByAccountIdAndTransactionId(100L, 99L));
    }

    @Test
    void testCreateTransaction_Success() {
        when(transactionRepository.save(any())).thenReturn(transaction);

        TransactionRequestDto requestDto = new TransactionRequestDto();
        requestDto.setAccountId(100L);
        requestDto.setAmount(new BigDecimal("500.00"));
        requestDto.setStatus(TransactionStatus.PENDING);

        assertDoesNotThrow(() -> transactionService.create(requestDto));

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_InvalidAmount() {
        TransactionRequestDto requestDto = new TransactionRequestDto();
        requestDto.setAccountId(100L);
        requestDto.setAmount(new BigDecimal("-10.00"));

        assertThrows(RequestValidationException.class, () -> transactionService.create(requestDto));

        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}


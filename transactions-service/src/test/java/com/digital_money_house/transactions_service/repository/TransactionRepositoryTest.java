package com.digital_money_house.transactions_service.repository;

import com.digital_money_house.transactions_service.entity.Transaction;
import com.digital_money_house.transactions_service.entity.TransactionStatus;
import com.digital_money_house.transactions_service.entity.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class TransactionRepositoryTest {

    @Autowired
    private ITransactionRepository transactionRepository;

    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();

        transaction1 = new Transaction();
        transaction1.setAccountId(1L);
        transaction1.setAmount(new BigDecimal("100.00"));
        transaction1.setTransactionType(TransactionType.INCOME);
        transaction1.setCreatedDate(LocalDate.now().minusDays(1));
        transaction1.setCreatedTime(LocalTime.now().minusHours(2));
        transaction1.setStatus(TransactionStatus.CONFIRMED);

        transaction2 = new Transaction();
        transaction2.setAccountId(1L);
        transaction2.setAmount(new BigDecimal("50.00"));
        transaction2.setTransactionType(TransactionType.EXPENSE);
        transaction2.setCreatedDate(LocalDate.now());
        transaction2.setCreatedTime(LocalTime.now());
        transaction2.setStatus(TransactionStatus.CONFIRMED);

        transaction3 = new Transaction();
        transaction3.setAccountId(2L);
        transaction3.setAmount(new BigDecimal("200.00"));
        transaction3.setTransactionType(TransactionType.TRANSFER);
        transaction3.setCreatedDate(LocalDate.now().minusDays(2));
        transaction3.setCreatedTime(LocalTime.now().minusHours(3));
        transaction3.setStatus(TransactionStatus.CONFIRMED);

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);
    }

    @Test
    void testFindTop5ByAccountIdOrderByCreatedDateDescCreatedTimeDesc() {
        List<Transaction> transactions = transactionRepository.findTop5ByAccountIdOrderByCreatedDateDescCreatedTimeDesc(1L);

        assertFalse(transactions.isEmpty());
        assertEquals(2, transactions.size());
        assertEquals(transaction2.getId(), transactions.get(0).getId());
    }

    @Test
    void testFindByAccountId() {
        List<Transaction> transactions = transactionRepository.findByAccountId(1L);

        assertFalse(transactions.isEmpty());
        assertEquals(2, transactions.size());
        assertTrue(transactions.stream().anyMatch(t -> t.getAmount().equals(new BigDecimal("100.00"))));
        assertTrue(transactions.stream().anyMatch(t -> t.getAmount().equals(new BigDecimal("50.00"))));
    }

    @Test
    void testFindByAccountIdAndId_Success() {
        Optional<Transaction> foundTransaction = transactionRepository.findByAccountIdAndId(1L, transaction1.getId());

        assertTrue(foundTransaction.isPresent());
        assertEquals(transaction1.getAmount(), foundTransaction.get().getAmount());
    }

    @Test
    void testFindByAccountIdAndId_NotFound() {
        Optional<Transaction> foundTransaction = transactionRepository.findByAccountIdAndId(1L, 999L);

        assertFalse(foundTransaction.isPresent());
    }
}


package com.digital_money_house.transactions_service.repository;

import com.digital_money_house.transactions_service.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ITransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findTop5ByAccountIdOrderByCreatedDateDescCreatedTimeDesc(Long accountId);

    List<Transaction> findByAccountId(Long id);

    Optional<Transaction> findByAccountIdAndId(Long accountId, Long transactionId);

}

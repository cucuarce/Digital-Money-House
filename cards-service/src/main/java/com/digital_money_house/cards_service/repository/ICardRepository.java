package com.digital_money_house.cards_service.repository;

import com.digital_money_house.cards_service.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICardRepository extends JpaRepository<Card, Long> {

    List<Card> findByAccountId(Long accountId);

    boolean existsByCardNumberAndAccountId(String cardNumber, Long accountId);

    Optional<Card> findByAccountIdAndId(Long accountId, Long cardId);

}

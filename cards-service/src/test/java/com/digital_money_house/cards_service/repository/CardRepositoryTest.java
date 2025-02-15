package com.digital_money_house.cards_service.repository;

import com.digital_money_house.cards_service.entity.Card;
import com.digital_money_house.cards_service.entity.CardIssuer;
import com.digital_money_house.cards_service.entity.CardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class CardRepositoryTest {

    @Autowired
    private ICardRepository cardRepository;

    private Card card1;

    @BeforeEach
    void setUp() {
        cardRepository.deleteAll();

        card1 = new Card();
        card1.setAccountId(1L);
        card1.setCardNumber("1234567812345678");
        card1.setCardHolderName("Max Power");
        card1.setExpirationDate("11/27");
        card1.setCardType(CardType.CREDIT);
        card1.setIssuer(CardIssuer.VISA);
        card1.setCreatedDate(null);
        card1.setCratedTime(null);

        cardRepository.save(card1);
    }

    @Test
    void testFindByAccountId() {
        List<Card> cards = cardRepository.findByAccountId(1L);
        assertFalse(cards.isEmpty());
        assertEquals(1, cards.size());
    }

    @Test
    void testExistsByCardNumberAndAccountId_Exists() {
        boolean exists = cardRepository.existsByCardNumberAndAccountId("1234567812345678", 1L);
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByCardNumberAndAccountId_NotExists() {
        boolean exists = cardRepository.existsByCardNumberAndAccountId("9999999999999999", 1L);
        assertThat(exists).isFalse();
    }

    @Test
    void testFindByAccountIdAndId_Exists() {
        Optional<Card> foundCard = cardRepository.findByAccountIdAndId(1L, card1.getId());
        assertThat(foundCard).isPresent();
        assertThat(foundCard.get().getCardNumber()).isEqualTo("1234567812345678");
    }

    @Test
    void testFindByAccountIdAndId_NotExists() {
        Optional<Card> foundCard = cardRepository.findByAccountIdAndId(2L, card1.getId());
        assertThat(foundCard).isNotPresent();
    }
}

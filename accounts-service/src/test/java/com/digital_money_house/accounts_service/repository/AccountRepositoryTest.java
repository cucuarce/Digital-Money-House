package com.digital_money_house.accounts_service.repository;

import com.digital_money_house.accounts_service.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class AccountRepositoryTest {

    @Autowired
    private IAccountRepository accountRepository;

    @Test
    void testSaveAccount() {
        // GIVEN
        Account account = new Account();
        account.setUserId(1L);
        account.setCvu("1234567890123456789012");
        account.setAlias("mi.cuenta.alias");
        account.setBalance(BigDecimal.ZERO);

        // WHEN
        Account savedAccount = accountRepository.save(account);

        // THEN
        assertNotNull(savedAccount.getId());
        assertEquals("1234567890123456789012", savedAccount.getCvu());
        assertEquals("mi.cuenta.alias", savedAccount.getAlias());
    }

    @Test
    void testFindByUserId() {
        // GIVEN
        Account account1 = new Account();
        account1.setUserId(1L);
        account1.setCvu("1234567890123456789012");
        account1.setAlias("cuenta.primaria");
        account1.setBalance(BigDecimal.ZERO);

        Account account2 = new Account();
        account2.setUserId(1L);
        account2.setCvu("9876543210987654321098");
        account2.setAlias("cuenta.secundaria");
        account2.setBalance(BigDecimal.ZERO);

        accountRepository.save(account1);
        accountRepository.save(account2);

        // WHEN
        List<Account> accounts = accountRepository.findByUserId(1L);

        // THEN
        assertFalse(accounts.isEmpty());
        assertEquals(2, accounts.size());
    }

    @Test
    void testFindByCvu() {
        // GIVEN
        Account account = new Account();
        account.setUserId(1L);
        account.setCvu("0234567890123456789012");
        account.setAlias("cuenta.primaria");
        account.setBalance(BigDecimal.ZERO);

        accountRepository.save(account);

        // WHEN
        Account foundAccount = accountRepository.findByCvu("0234567890123456789012");

        // THEN
        assertNotNull(foundAccount);
        assertEquals("cuenta.primaria", foundAccount.getAlias());
    }

    @Test
    void testExistsByCvu() {
        // GIVEN
        Account account = new Account();
        account.setUserId(1L);
        account.setCvu("1234567890123456789012");
        account.setAlias("cuenta.primaria");
        account.setBalance(BigDecimal.ZERO);

        accountRepository.save(account);

        // WHEN
        boolean exists = accountRepository.existsByCvu("1234567890123456789012");

        // THEN
        assertTrue(exists);
    }

    @Test
    void testExistsByAlias() {
        // GIVEN
        Account account = new Account();
        account.setUserId(1L);
        account.setCvu("1234567890123456789012");
        account.setAlias("mi.cuenta.alias");
        account.setBalance(BigDecimal.ZERO);

        accountRepository.save(account);

        // WHEN
        boolean exists = accountRepository.existsByAlias("mi.cuenta.alias");

        // THEN
        assertTrue(exists);
    }
}



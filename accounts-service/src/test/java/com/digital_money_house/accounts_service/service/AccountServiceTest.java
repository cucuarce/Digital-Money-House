package com.digital_money_house.accounts_service.service;

import com.digital_money_house.accounts_service.dto.request.AccountRequestDto;
import com.digital_money_house.accounts_service.dto.response.AccountResponseDto;
import com.digital_money_house.accounts_service.dto.response.UserClientDto;
import com.digital_money_house.accounts_service.entity.Account;
import com.digital_money_house.accounts_service.exception.ResourceNotFoundException;
import com.digital_money_house.accounts_service.feign.UserClient;
import com.digital_money_house.accounts_service.repository.IAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private IAccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Mock
    private UserClient userClient;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setUserId(100L);
        account.setCvu("1234567890123456789012");
        account.setAlias("mi.alias");
        account.setBalance(BigDecimal.ZERO);
    }

    @Test
    void testCreateAccount() {
        UserClientDto mockUser = new UserClientDto();
        mockUser.setId(100L);

        when(userClient.getUserById(anyLong())).thenReturn(mockUser);
        when(accountRepository.existsByCvu(any())).thenReturn(false);
        when(accountRepository.existsByAlias(any())).thenReturn(false);
        when(accountRepository.save(any())).thenReturn(account);

        AccountRequestDto requestDto = new AccountRequestDto();
        requestDto.setUserId(100L);

        assertDoesNotThrow(() -> accountService.create(requestDto));

        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testFindByIdSuccess() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        AccountResponseDto responseDto = accountService.findById(1L);

        assertNotNull(responseDto);
        assertEquals(account.getCvu(), responseDto.getCvu());
    }

    @Test
    void testFindByIdNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.findById(99L));
    }

    @Test
    void testDeleteByIdSuccess() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        doNothing().when(accountRepository).delete(account);

        assertDoesNotThrow(() -> accountService.deleteById(1L));
        verify(accountRepository, times(1)).delete(account);
    }

    @Test
    void testDeleteByIdNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.deleteById(99L));
    }

    @Test
    void testListAllAccountsSuccess() {
        when(accountRepository.findAll()).thenReturn(Collections.singletonList(account));

        List<AccountResponseDto> accounts = accountService.listAll();

        assertFalse(accounts.isEmpty());
        assertEquals(1, accounts.size());
    }

    @Test
    void testListAllAccountsNotFound() {
        when(accountRepository.findAll()).thenReturn(Collections.emptyList());

        List<AccountResponseDto> result = accountService.listAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByUserIdSuccess() {
        when(accountRepository.findByUserId(100L)).thenReturn(Collections.singletonList(account));

        List<AccountResponseDto> accounts = accountService.findByUserId(100L);

        assertFalse(accounts.isEmpty());
        assertEquals(1, accounts.size());
    }

    @Test
    void testFindByUserIdNotFound() {
        when(accountRepository.findByUserId(999L)).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> accountService.findByUserId(999L));
    }

    @Test
    void testUpdateAliasSuccess() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.existsByAlias("el.nuevo.alias")).thenReturn(false);

        AccountRequestDto requestDto = new AccountRequestDto();
        requestDto.setId(1L);
        requestDto.setAlias("el.nuevo.alias");

        assertDoesNotThrow(() -> accountService.updateAlias(requestDto));
        verify(accountRepository, times(1)).save(account);
    }
}


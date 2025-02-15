package com.digital_money_house.accounts_service.controller;

import com.digital_money_house.accounts_service.config.jwt.JwtAuthenticationFilter;
import com.digital_money_house.accounts_service.dto.request.AccountRequestDto;
import com.digital_money_house.accounts_service.dto.response.AccountResponseDto;
import com.digital_money_house.accounts_service.dto.response.CardClientDto;
import com.digital_money_house.accounts_service.dto.response.TransactionClientDto;
import com.digital_money_house.accounts_service.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = AccountController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private AccountResponseDto mockAccount;
    private AccountRequestDto accountRequestDto;
    private TransactionClientDto transactionRequestDto;
    private CardClientDto mockCard;

    @BeforeEach
    void setUp() {
        mockAccount = new AccountResponseDto();
        mockAccount.setId(1L);
        mockAccount.setCvu("1234567890123456789012");

        accountRequestDto = new AccountRequestDto();
        accountRequestDto.setId(1L);
        accountRequestDto.setAlias("nuevo.alias.cvu");
        accountRequestDto.setCvu("1234567890123456789012");
        accountRequestDto.setBalance(BigDecimal.ZERO);
        accountRequestDto.setUserId(1L);

        transactionRequestDto = new TransactionClientDto();
        transactionRequestDto.setAccountId(1L);
        transactionRequestDto.setTransactionType("DEBITO");
        transactionRequestDto.setStatus("APROBADA");

        mockCard = new CardClientDto();
        mockCard.setId(1L);
        mockCard.setAccountId(1L);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetAccountById_Success() throws Exception {
        when(accountService.findById(1L)).thenReturn(mockAccount);

        mockMvc.perform(get("/accounts/api/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockAccount.getId()))
                .andExpect(jsonPath("$.cvu").value(mockAccount.getCvu()));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetAccountsByUserId_Success() throws Exception {
        when(accountService.findByUserId(1L)).thenReturn(List.of(mockAccount));

        mockMvc.perform(get("/accounts/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].cvu").value(mockAccount.getCvu()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testListAccounts_Success() throws Exception {
        when(accountService.listAll()).thenReturn(List.of(mockAccount));

        mockMvc.perform(get("/accounts/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].cvu").value(mockAccount.getCvu()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testListAccounts_EmptyList() throws Exception {
        when(accountService.listAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/accounts/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testCreateAccount_Success() throws Exception {
        doNothing().when(accountService).create(any(AccountRequestDto.class));

        mockMvc.perform(post("/accounts/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Cuenta creada exitosamente."));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testDeleteAccount_Success() throws Exception {
        doNothing().when(accountService).deleteById(1L);

        mockMvc.perform(delete("/accounts/api/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cuenta eliminada exitosamente."));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetAccountTransactions_Success() throws Exception {
        when(accountService.findLastFiveTransactionsByAccountId(1L)).thenReturn(List.of(transactionRequestDto));

        mockMvc.perform(get("/accounts/api/1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetCardsByAccount_Success() throws Exception {
        when(accountService.getCardsByAccountId(1L)).thenReturn(List.of(mockCard));

        mockMvc.perform(get("/accounts/api/1/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetCardById_Success() throws Exception {
        when(accountService.getCardById(1L, 1L)).thenReturn(mockCard);

        mockMvc.perform(get("/accounts/api/1/cards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockCard.getId()));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetTransactionById_Success() throws Exception {
        when(accountService.getTransactionById(1L, 1L)).thenReturn(transactionRequestDto);

        mockMvc.perform(get("/accounts/api/1/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionType").value(transactionRequestDto.getTransactionType()));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testDeleteCardFromAccount_Success() throws Exception {
        doNothing().when(accountService).deleteCardFromAccount(1L, 1L);

        mockMvc.perform(delete("/accounts/api/1/cards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tarjeta eliminada correctamente."));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testCreateTransaction_Success() throws Exception {
        doNothing().when(accountService).createTransaction(any(TransactionClientDto.class));

        mockMvc.perform(post("/accounts/api/1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Tipo de transacción: " + transactionRequestDto.getTransactionType() + ". Estado: " + transactionRequestDto.getStatus()));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testUpdateAlias_Success() throws Exception {
        doNothing().when(accountService).updateAlias(any(AccountRequestDto.class));

        mockMvc.perform(patch("/accounts/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Alias actualizado exitosamente."));
    }
}

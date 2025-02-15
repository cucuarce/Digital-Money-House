package com.digital_money_house.transactions_service.controller;

import com.digital_money_house.transactions_service.config.jwt.JwtAuthenticationFilter;
import com.digital_money_house.transactions_service.dto.request.TransactionRequestDto;
import com.digital_money_house.transactions_service.dto.response.TransactionResponseDto;
import com.digital_money_house.transactions_service.entity.TransactionStatus;
import com.digital_money_house.transactions_service.entity.TransactionType;
import com.digital_money_house.transactions_service.service.TransactionService;
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
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = TransactionController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionResponseDto mockTransaction;
    private TransactionRequestDto transactionRequestDto;

    @BeforeEach
    void setUp() {
        mockTransaction = new TransactionResponseDto();
        mockTransaction.setId(1L);
        mockTransaction.setAccountId(1L);
        mockTransaction.setAmount(BigDecimal.valueOf(100.00));
        mockTransaction.setTransactionType(TransactionType.INCOME);
        mockTransaction.setStatus(TransactionStatus.CONFIRMED);

        transactionRequestDto = new TransactionRequestDto();
        transactionRequestDto.setAccountId(1L);
        transactionRequestDto.setAmount(BigDecimal.valueOf(100.00));
        transactionRequestDto.setTransactionType(TransactionType.INCOME);
        transactionRequestDto.setStatus(TransactionStatus.PENDING);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetTransactionById_Success() throws Exception {
        when(transactionService.findById(1L)).thenReturn(mockTransaction);

        mockMvc.perform(get("/transactions/api/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockTransaction.getId()))
                .andExpect(jsonPath("$.amount").value(mockTransaction.getAmount()));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetLastFiveTransactions_Success() throws Exception {
        when(transactionService.getLastFiveTransactions(1L)).thenReturn(List.of(mockTransaction));

        mockMvc.perform(get("/transactions/api/last-five/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].amount").value(mockTransaction.getAmount()));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetTransactionsByAccountId_Success() throws Exception {
        when(transactionService.findByAccountId(1L)).thenReturn(List.of(mockTransaction));

        mockMvc.perform(get("/transactions/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].amount").value(mockTransaction.getAmount()));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetTransactionByAccountAndId_Success() throws Exception {
        when(transactionService.findByAccountIdAndTransactionId(1L, 1L)).thenReturn(mockTransaction);

        mockMvc.perform(get("/transactions/api/accounts/1/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockTransaction.getId()))
                .andExpect(jsonPath("$.amount").value(mockTransaction.getAmount()));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testCreateTransaction_Success() throws Exception {
        doNothing().when(transactionService).create(any(TransactionRequestDto.class));

        mockMvc.perform(post("/transactions/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Tipo de transacción: " + transactionRequestDto.getTransactionType() + ". Estado: " + transactionRequestDto.getStatus()));
    }
}


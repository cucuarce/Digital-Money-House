package com.digital_money_house.accounts_service.feign;

import com.digital_money_house.accounts_service.dto.response.TransactionClientDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "transactions-service", url = "http://localhost:8084", configuration = FeignClientInterceptor.class)
public interface TransactionClient {

    @GetMapping("transactions/api/last-five/{accountId}")
    List<TransactionClientDto> getLastFiveTransactions(@PathVariable Long accountId);

    @GetMapping("/transactions/api/accounts/{accountId}/transactions/{transactionId}")
    TransactionClientDto getTransactionById(@PathVariable("accountId") Long accountId,
                              @PathVariable("transactionId") Long transactionId);

    @PostMapping("/transactions/api")
    void createTransaction(@RequestBody @Valid TransactionClientDto transactionClientDto);

}

package com.digital_money_house.accounts_service.feign;

import com.digital_money_house.accounts_service.dto.response.CardClientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "cards-service", url = "http://localhost:8085", configuration = FeignClientInterceptor.class)
public interface CardClient {

    @GetMapping("/cards/api/accounts/{accountId}")
    List<CardClientDto> getCardsByAccountId(@PathVariable("accountId") Long accountId);

    @GetMapping("/cards/api/accounts/{accountId}/cards/{cardId}")
    CardClientDto getCardByAccountIdAndId(@PathVariable("accountId") Long accountId,
                                @PathVariable("cardId") Long cardId);

    @DeleteMapping("/cards/api/{id}")
    void deleteCard(@PathVariable Long id);

}

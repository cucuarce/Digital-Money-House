package com.digital_money_house.cards_service.feign;

import com.digital_money_house.cards_service.dto.response.AccountClientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "accounts-service", url = "http://localhost:8083", configuration = FeignClientInterceptor.class)
public interface AccountClient {

    @GetMapping("/accounts/api/{id}")
    AccountClientDto getAccountById(@PathVariable Long id);

}

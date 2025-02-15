package com.digital_money_house.transactions_service.feign;

import com.digital_money_house.transactions_service.dto.response.UserClientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "security-service", url = "http://localhost:8082", configuration = FeignClientInterceptor.class)
public interface UserClient {

    @GetMapping("/users/api/{id}")
    UserClientDto getUserById(@PathVariable Long id);

    @GetMapping("/users/api/email/{email}")
    Optional<UserClientDto> getUserByEmail(@PathVariable String email);

}

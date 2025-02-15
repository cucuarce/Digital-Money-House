package com.digital_money_house.accounts_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserClientDto {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String dni;
    private String phoneNumber;
    private boolean verified;

}

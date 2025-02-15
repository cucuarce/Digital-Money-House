package com.digital_money_house.security_service.dto.response;

import com.digital_money_house.security_service.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String dni;
    private String phoneNumber;
    private boolean verified;
    private Role role;
}

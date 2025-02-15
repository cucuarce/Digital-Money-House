package com.digital_money_house.security_service.dto.request;

import lombok.Data;

@Data
public class PasswordResetRequestDto {
    private String email;
    private String newPassword;
    private String confirmPassword;

}

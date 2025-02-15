package com.digital_money_house.security_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

    private Long id;

    @NotNull(message = "El nombre no puede ser nulo." )
    @NotBlank(message = "El nombre no puede estar vacío." )
    private String firstName;

    @NotNull(message = "El apellido no puede ser nulo." )
    @NotBlank(message = "El apellido no puede estar vacío." )
    private String lastName;

    @NotNull(message = "El email no puede ser nulo." )
    @NotBlank(message = "El email no puede estar vacío." )
    private String email;

    @NotNull(message = "La contraseña no puede ser nula." )
    @NotBlank(message = "La contraseña no puede estar vacía." )
    @Size(min = 6, max = 12)
    private String password;

    @NotNull(message = "El dni no puede ser nulo." )
    @NotBlank(message = "El dni no puede estar vacío." )
    @Size(min = 8, max = 8)
    private String dni;

    @NotNull(message = "El teléfono no puede ser nulo." )
    @NotBlank(message = "El teléfono no puede estar vacío." )
    @Size(min = 10, max = 10)
    private String phoneNumber;

    private boolean verified;

    private String verificationCode;

    public RegisterRequestDto(String firstName, String phoneNumber, String lastName, String email, String password, String dni) {
        this.firstName = firstName;
        this.phoneNumber = phoneNumber;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.dni = dni;
    }
}


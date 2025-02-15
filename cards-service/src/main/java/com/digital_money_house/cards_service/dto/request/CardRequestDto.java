package com.digital_money_house.cards_service.dto.request;

import com.digital_money_house.cards_service.entity.CardIssuer;
import com.digital_money_house.cards_service.entity.CardType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardRequestDto {

    private Long id;

    private Long accountId;

    @NotNull(message = "El número de la tarjeta no puede ser nulo." )
    @NotBlank(message = "El número de la tarjeta no puede estar vacío." )
    private String cardNumber;

    @NotNull(message = "El nombre no puede ser nulo." )
    @NotBlank(message = "El nombre no puede estar vacío." )
    private String cardHolderName;

    @NotNull(message = "La fecha no puede ser nula." )
    @NotBlank(message = "La fecha no puede estar vacía." )
    private String expirationDate;

    @NotNull(message = "El tipo de tarjeta no puede ser nulo." )
    private CardType cardType;

    @NotNull(message = "El proveedor de la tarjeta no puede ser nulo." )
    private CardIssuer issuer;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate createdDate;

    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime cratedTime;

}

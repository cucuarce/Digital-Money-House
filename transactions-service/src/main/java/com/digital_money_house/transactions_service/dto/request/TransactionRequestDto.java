package com.digital_money_house.transactions_service.dto.request;

import com.digital_money_house.transactions_service.entity.TransactionStatus;
import com.digital_money_house.transactions_service.entity.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDto {

    private Long id;

    private Long accountId;

    private TransactionType transactionType;

    @NotNull(message = "El monto no puede ser nulo.")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0.")
    private BigDecimal amount;

    @Pattern(regexp = "\\d{22}", message = "El CVU de origen debe contener exactamente 22 dígitos numéricos.")
    private String originCvu;

    @Pattern(regexp = "\\d{22}", message = "El CVU de destino debe contener exactamente 22 dígitos numéricos.")
    private String destinationCvu;

    private TransactionStatus status;

    private Long cardId;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate createdDate;

    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime createdTime;

}

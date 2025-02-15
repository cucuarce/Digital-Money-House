package com.digital_money_house.accounts_service.utils;

import com.digital_money_house.accounts_service.dto.response.CardClientDto;
import com.digital_money_house.accounts_service.dto.response.TransactionClientDto;
import com.digital_money_house.accounts_service.entity.Account;
import com.digital_money_house.accounts_service.exception.InsufficientFundsException;
import com.digital_money_house.accounts_service.exception.RequestValidationException;
import com.digital_money_house.accounts_service.feign.CardClient;
import com.digital_money_house.accounts_service.repository.IAccountRepository;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.stream.Collectors;

public class ValidationUtils {

    public static String generateCvu() {
        SecureRandom random = new SecureRandom();
        return random.ints(22, 0, 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }

    public static String generateAlias() {
        String[] aliasList = loadAliasList();
        SecureRandom random = new SecureRandom();
        //return aliasList[random.nextInt(aliasList.length)];

        String palabra1 = aliasList[random.nextInt(aliasList.length)];
        String palabra2 = aliasList[random.nextInt(aliasList.length)];
        String palabra3 = aliasList[random.nextInt(aliasList.length)];

        return palabra1 + "." + palabra2 + "." + palabra3;
    }

    public static String[] loadAliasList() {
        try (InputStream inputStream = ValidationUtils.class.getResourceAsStream("/templates/alias.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().toArray(String[]::new);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el archivo de alias.");
        }
    }

    public static boolean isValidAlias(String alias) {
        String regex = "^[^.]+\\.[^.]+\\.[^.]+$"; // Asegura que haya tres palabras separadas por puntos
        return alias.matches(regex);
    }

    public static void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RequestValidationException("El monto debe ser mayor a 0.", HttpStatus.BAD_REQUEST.value());
        }
    }

    public static void validateOriginCvu(Account account, TransactionClientDto transactionRequestDto) {
        if (!transactionRequestDto.getOriginCvu().equals(account.getCvu())) {
            throw new RequestValidationException("El CVU de origen no corresponde a la cuenta especificada.", HttpStatus.BAD_REQUEST.value());
        }
    }

    public static void validateDestinationCvu(IAccountRepository accountRepository, String destinationCvu) {
        if (!accountRepository.existsByCvu(destinationCvu)) {
            throw new RequestValidationException("El CVU de destino no es válido.", HttpStatus.BAD_REQUEST.value());
        }
    }

    /*public static String determineTransactionStatus(Account account, TransactionClientDto transactionRequestDto, IAccountRepository accountRepository) {
        switch (transactionRequestDto.getTransactionType()) {
            case "income":
                return "confirmed";

            case "expense":
                return account.getBalance().compareTo(transactionRequestDto.getAmount()) >= 0 ? "confirmed" : "cancelled";

            case "transfer":
                validateOriginCvu(account, transactionRequestDto);
                validateDestinationCvu(accountRepository, transactionRequestDto.getDestinationCvu());
                return account.getBalance().compareTo(transactionRequestDto.getAmount()) >= 0 ? "confirmed" : "cancelled";

            default:
                throw new RequestValidationException("Tipo de transacción no válido.", HttpStatus.BAD_REQUEST.value());
        }
    }

    public static void validateTransactionRequest(IAccountRepository accountRepository, TransactionClientDto transaction) {

        if (transaction.getTransactionType() == null) {
            throw new RequestValidationException("El tipo de transacción es obligatorio.", HttpStatus.BAD_REQUEST.value());
        }

        switch (transaction.getTransactionType()) {
            case "income":
                if (transaction.getDestinationCvu() == null || transaction.getDestinationCvu().isBlank()) {
                    throw new RequestValidationException("El CVU de destino es obligatorio para ingresos.", HttpStatus.BAD_REQUEST.value());
                }
                if (transaction.getOriginCvu() != null) {
                    throw new RequestValidationException("El CVU de origen no debe enviarse en ingresos.", HttpStatus.BAD_REQUEST.value());
                }

                Account destinationAccount = accountRepository.findByCvu(transaction.getDestinationCvu());
                if (destinationAccount == null || !destinationAccount.getId().equals(transaction.getAccountId())) {
                    throw new RequestValidationException("El ID de la cuenta no coincide con el CVU de destino.", HttpStatus.BAD_REQUEST.value());
                }
                break;

            case "expense":
                if (transaction.getOriginCvu() == null || transaction.getOriginCvu().isBlank()) {
                    throw new RequestValidationException("El CVU de origen es obligatorio para egresos.", HttpStatus.BAD_REQUEST.value());
                }
                if (transaction.getDestinationCvu() != null) {
                    throw new RequestValidationException("El CVU de destino no debe enviarse en egresos.", HttpStatus.BAD_REQUEST.value());
                }

                Account originAccount = accountRepository.findByCvu(transaction.getOriginCvu());
                if (originAccount == null || !originAccount.getId().equals(transaction.getAccountId())) {
                    throw new RequestValidationException("El ID de la cuenta no coincide con el CVU de origen.", HttpStatus.BAD_REQUEST.value());
                }
                break;

            case "transfer":
                if (transaction.getOriginCvu() == null || transaction.getOriginCvu().isBlank()) {
                    throw new RequestValidationException("El CVU de origen es obligatorio para transferencias.", HttpStatus.BAD_REQUEST.value());
                }
                if (transaction.getDestinationCvu() == null || transaction.getDestinationCvu().isBlank()) {
                    throw new RequestValidationException("El CVU de destino es obligatorio para transferencias.", HttpStatus.BAD_REQUEST.value());
                }
                if (transaction.getOriginCvu().equals(transaction.getDestinationCvu())) {
                    throw new RequestValidationException("El CVU de origen y destino no pueden ser el mismo.", HttpStatus.BAD_REQUEST.value());
                }

                Account originAcc = accountRepository.findByCvu(transaction.getOriginCvu());
                if (originAcc == null || !originAcc.getId().equals(transaction.getAccountId())) {
                    throw new RequestValidationException("El ID de la cuenta no coincide con el CVU de origen.", HttpStatus.BAD_REQUEST.value());
                }

                if (!accountRepository.existsByCvu(transaction.getDestinationCvu())) {
                    throw new RequestValidationException("El CVU de destino no es válido.", HttpStatus.BAD_REQUEST.value());
                }
                break;

            default:
                throw new RequestValidationException("Tipo de transacción no válido.", HttpStatus.BAD_REQUEST.value());
        }
    }*/

    public static void validateTransactionRequest(IAccountRepository accountRepository,CardClient cardClient, TransactionClientDto transaction) {

        if (transaction.getTransactionType() == null) {
            throw new RequestValidationException("El tipo de transacción es obligatorio.", HttpStatus.BAD_REQUEST.value());
        }

        switch (transaction.getTransactionType()) {
            case "income":
                if (transaction.getDestinationCvu() == null || transaction.getDestinationCvu().isBlank()) {
                    throw new RequestValidationException("El CVU de destino es obligatorio para ingresos.", HttpStatus.BAD_REQUEST.value());
                }

                if (transaction.getOriginCvu() != null || transaction.getCardId() != null) {
                    throw new RequestValidationException("No se debe enviar CVU de origen ni tarjeta en un ingreso.", HttpStatus.BAD_REQUEST.value());
                }

                Account destinationAccount = accountRepository.findByCvu(transaction.getDestinationCvu());
                if (destinationAccount == null || !destinationAccount.getId().equals(transaction.getAccountId())) {
                    throw new RequestValidationException("El ID de la cuenta no coincide con el CVU de destino.", HttpStatus.BAD_REQUEST.value());
                }
                break;

            case "expense":
                if (transaction.getOriginCvu() == null || transaction.getOriginCvu().isBlank()) {
                    throw new RequestValidationException("El CVU de origen es obligatorio para egresos.", HttpStatus.BAD_REQUEST.value());
                }

                if (transaction.getDestinationCvu() != null || transaction.getCardId() != null) {
                    throw new RequestValidationException("No se debe enviar CVU de destino ni tarjeta en un egreso.", HttpStatus.BAD_REQUEST.value());
                }

                Account originAccount = accountRepository.findByCvu(transaction.getOriginCvu());
                if (originAccount == null || !originAccount.getId().equals(transaction.getAccountId())) {
                    throw new RequestValidationException("El ID de la cuenta no coincide con el CVU de origen.", HttpStatus.BAD_REQUEST.value());
                }
                break;

            case "transfer":
                if ((transaction.getOriginCvu() == null || transaction.getOriginCvu().isBlank()) &&
                        (transaction.getCardId() == null)) {
                    throw new RequestValidationException("Debe proporcionar un CVU de origen o una tarjeta para transferencias.", HttpStatus.BAD_REQUEST.value());
                }

                if (transaction.getOriginCvu() != null && transaction.getCardId() != null) {
                    throw new RequestValidationException("No se puede transferir desde CVU y tarjeta al mismo tiempo.", HttpStatus.BAD_REQUEST.value());
                }

                if (transaction.getOriginCvu() != null && transaction.getOriginCvu().equals(transaction.getDestinationCvu())) {
                    throw new RequestValidationException("El CVU de origen y destino no pueden ser el mismo.", HttpStatus.BAD_REQUEST.value());
                }

                if (transaction.getDestinationCvu() == null || transaction.getDestinationCvu().isBlank()) {
                    throw new RequestValidationException("El CVU de destino es obligatorio para transferencias.", HttpStatus.BAD_REQUEST.value());
                }

                if (!accountRepository.existsByCvu(transaction.getDestinationCvu())) {
                    throw new RequestValidationException("El CVU de destino no es válido.", HttpStatus.BAD_REQUEST.value());
                }

                // Validación del origen de la transferencia
                if (transaction.getOriginCvu() != null) {
                    Account originAcc = accountRepository.findByCvu(transaction.getOriginCvu());
                    if (originAcc == null || !originAcc.getId().equals(transaction.getAccountId())) {
                        throw new RequestValidationException("El ID de la cuenta no coincide con el CVU de origen.", HttpStatus.BAD_REQUEST.value());
                    }
                } else if (transaction.getCardId() != null) {
                    CardClientDto card = cardClient.getCardByAccountIdAndId(transaction.getAccountId(), transaction.getCardId());
                    if (card == null) {
                        throw new RequestValidationException("Tarjeta no encontrada o no pertenece a esta cuenta.", HttpStatus.FORBIDDEN.value());
                    }
                } else {
                    throw new RequestValidationException("Debe proporcionar un CVU de origen o una tarjeta para realizar la transferencia.", HttpStatus.BAD_REQUEST.value());
                }
                break;

            default:
                throw new RequestValidationException("Tipo de transacción no válido.", HttpStatus.BAD_REQUEST.value());
        }
    }

    public static String determineTransactionStatus(Account account, TransactionClientDto transactionRequestDto, IAccountRepository accountRepository, CardClient cardClient) {
        switch (transactionRequestDto.getTransactionType()) {
            case "income":
                return "confirmed";

            case "expense":
                return account.getBalance().compareTo(transactionRequestDto.getAmount()) >= 0 ? "confirmed" : "cancelled";

            case "transfer":
                // Si la transferencia es desde una tarjeta, verificar el tipo
                if (transactionRequestDto.getCardId() != null) {
                    CardClientDto card = cardClient.getCardByAccountIdAndId(account.getId(), transactionRequestDto.getCardId());
                    if (card == null) {
                        throw new RequestValidationException("Tarjeta no encontrada o no pertenece a esta cuenta.", HttpStatus.FORBIDDEN.value());
                    }

                    // Si es tarjeta de débito, validar fondos
                    if (card.getCardType().equals("debit") && account.getBalance().compareTo(transactionRequestDto.getAmount()) < 0) {
                        return "cancelled";
                    }
                    // Si es tarjeta de crédito, siempre permitir la transacción
                    return "confirmed";
                }

                // Si es una transferencia desde CVU
                validateOriginCvu(account, transactionRequestDto);
                validateDestinationCvu(accountRepository, transactionRequestDto.getDestinationCvu());
                return account.getBalance().compareTo(transactionRequestDto.getAmount()) >= 0 ? "confirmed" : "cancelled";

            default:
                throw new RequestValidationException("Tipo de transacción no válido.", HttpStatus.BAD_REQUEST.value());
        }
    }


}

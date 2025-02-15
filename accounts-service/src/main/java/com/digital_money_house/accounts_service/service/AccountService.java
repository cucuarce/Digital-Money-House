package com.digital_money_house.accounts_service.service;

import com.digital_money_house.accounts_service.dto.request.AccountRequestDto;
import com.digital_money_house.accounts_service.dto.response.AccountResponseDto;
import com.digital_money_house.accounts_service.dto.response.CardClientDto;
import com.digital_money_house.accounts_service.dto.response.TransactionClientDto;
import com.digital_money_house.accounts_service.entity.Account;
import com.digital_money_house.accounts_service.exception.InsufficientFundsException;
import com.digital_money_house.accounts_service.exception.RequestValidationException;
import com.digital_money_house.accounts_service.exception.ResourceAlreadyExistsException;
import com.digital_money_house.accounts_service.exception.ResourceNotFoundException;
import com.digital_money_house.accounts_service.feign.CardClient;
import com.digital_money_house.accounts_service.feign.TransactionClient;
import com.digital_money_house.accounts_service.feign.UserClient;
import com.digital_money_house.accounts_service.repository.IAccountRepository;
import com.digital_money_house.accounts_service.utils.MapperClass;
import com.digital_money_house.accounts_service.utils.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final IAccountRepository accountRepository;
    private final UserClient userClient;
    private final CardClient cardClient;
    private final TransactionClient transactionClient;
    private static final ObjectMapper objectMapper = MapperClass.objectMapper();

    @Transactional
    public void create(AccountRequestDto requestDto) {
        var user = userClient.getUserById(requestDto.getUserId());
        if (user == null) {
            throw new ResourceNotFoundException("Usuario no encontrado.", HttpStatus.NOT_FOUND.value());
        }

        String cvu;
        do {
            cvu = ValidationUtils.generateCvu();
        } while (accountRepository.existsByCvu(cvu));

        String alias;
        do {
            alias = ValidationUtils.generateAlias();
        } while (accountRepository.existsByAlias(alias));

        Account newAccount = new Account();
        newAccount.setUserId(user.getId());
        newAccount.setCvu(cvu);
        newAccount.setAlias(alias);
        newAccount.setBalance(BigDecimal.ZERO);
        newAccount.setCreatedDate(LocalDate.now());
        newAccount.setCreatedTime(LocalTime.now());

        accountRepository.save(newAccount);
    }

    public AccountResponseDto findById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada.", HttpStatus.NOT_FOUND.value()));

        return objectMapper.convertValue(account, AccountResponseDto.class);
    }

    public void deleteById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada.", HttpStatus.NOT_FOUND.value()));

        accountRepository.delete(account);
    }

    public List<AccountResponseDto> listAll() {
        List<Account> accounts = accountRepository.findAll();

        return accounts.stream()
                .map(account -> objectMapper.convertValue(account, AccountResponseDto.class))
                .collect(Collectors.toList());
    }

    public List<AccountResponseDto> findByUserId(Long id) {
        List<Account> accounts = accountRepository.findByUserId(id);
        if (accounts.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron cuentas para el usuario.", HttpStatus.NOT_FOUND.value());
        }

        return accounts.stream()
                .map(account -> objectMapper.convertValue(account, AccountResponseDto.class))
                .collect(Collectors.toList());
    }

    public void updateAlias(AccountRequestDto accountRequestDto) {
        Account accountDB = accountRepository.findById(accountRequestDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada.", HttpStatus.NOT_FOUND.value()));

        String newAlias = accountRequestDto.getAlias();
        if (newAlias != null) {

            if (!ValidationUtils.isValidAlias(newAlias)) {
                throw new IllegalArgumentException("El alias debe tener el formato 'palabra.palabra.palabra'.");
            }

            if (accountRepository.existsByAlias(newAlias) && !accountDB.getAlias().equals(newAlias)) {
                throw new ResourceAlreadyExistsException("El alias ya está en uso.", HttpStatus.CONFLICT.value());
            }

            accountDB.setAlias(newAlias);
        }

        accountRepository.save(accountDB);
    }

    public List<TransactionClientDto> findLastFiveTransactionsByAccountId(Long id) {
        Account accounts = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada.", HttpStatus.NOT_FOUND.value()));
        return transactionClient.getLastFiveTransactions(id);
        /*try {
            List<TransactionClientDto> lastTransactions = transactionClient.getLastFiveTransactions(id);

            if (lastTransactions.isEmpty()) {
                throw new ResourceNotFoundException("No se encontraron transacciones para la cuenta.", HttpStatus.NOT_FOUND.value());
            }

            return lastTransactions;
        } catch (FeignException.NotFound e) {
            // Captura la excepción de Feign cuando no hay transacciones y lanza la tuya
            throw new ResourceNotFoundException("No se encontraron transacciones para la cuenta.", HttpStatus.NOT_FOUND.value());
        }*/
    }

    public List<CardClientDto> getCardsByAccountId(Long accountId) {

        accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada.", HttpStatus.NOT_FOUND.value()));

        return cardClient.getCardsByAccountId(accountId);
    }

    public CardClientDto getCardById(Long accountId, Long cardId) {

        accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada.", HttpStatus.NOT_FOUND.value()));

        return cardClient.getCardByAccountIdAndId(accountId, cardId);
    }

    public TransactionClientDto getTransactionById(Long accountId, Long transactionId) {

        accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada.", HttpStatus.NOT_FOUND.value()));

        return transactionClient.getTransactionById(accountId, transactionId);
    }

    @Transactional
    public void deleteCardFromAccount(Long accountId, Long cardId) {

        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Cuenta no encontrada.", HttpStatus.NOT_FOUND.value());
        }

        try {
            cardClient.deleteCard(cardId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Tarjeta no encontrada para la cuenta.", HttpStatus.NOT_FOUND.value());
        }
    }

    @Transactional
    public void createTransaction(TransactionClientDto transactionRequestDto) {

        ValidationUtils.validateTransactionRequest(accountRepository, cardClient, transactionRequestDto);

        Account account = accountRepository.findById(transactionRequestDto.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada.", HttpStatus.NOT_FOUND.value()));

        ValidationUtils.validateAmount(transactionRequestDto.getAmount());

        String newStatus = ValidationUtils.determineTransactionStatus(account, transactionRequestDto, accountRepository, cardClient);
        transactionRequestDto.setStatus(newStatus);

        if ("cancelled".equals(newStatus)) {
            transactionClient.createTransaction(transactionRequestDto);
            throw new InsufficientFundsException("Fondos insuficientes para realizar la transacción.", HttpStatus.GONE.value());
        }

        processBalanceUpdate(account, transactionRequestDto);

        transactionClient.createTransaction(transactionRequestDto);
    }

    private void processBalanceUpdate(Account account, TransactionClientDto transactionRequestDto) {
        switch (transactionRequestDto.getTransactionType()) {
            case "income":
                account.setBalance(account.getBalance().add(transactionRequestDto.getAmount()));
                break;

            case "expense":
                account.setBalance(account.getBalance().subtract(transactionRequestDto.getAmount()));
                break;

            case "transfer":
                Account destinationAccount = accountRepository.findByCvu(transactionRequestDto.getDestinationCvu());
                if (destinationAccount == null) {
                    throw new RequestValidationException("El CVU de destino no es válido.", HttpStatus.BAD_REQUEST.value());
                }

                if (transactionRequestDto.getCardId() != null) {
                    // Obtener la tarjeta
                    CardClientDto card = cardClient.getCardByAccountIdAndId(account.getId(), transactionRequestDto.getCardId());
                    if (card == null) {
                        throw new RequestValidationException("Tarjeta no encontrada o no pertenece a esta cuenta.", HttpStatus.FORBIDDEN.value());
                    }

                    // Si es tarjeta de débito, restar saldo de la cuenta asociada
                    if (card.getCardType().equalsIgnoreCase("debit")) {
                        account.setBalance(account.getBalance().subtract(transactionRequestDto.getAmount()));
                    }

                    // Agregar saldo a la cuenta destino en cualquier caso (débito o crédito)
                    destinationAccount.setBalance(destinationAccount.getBalance().add(transactionRequestDto.getAmount()));

                } else {
                    // Transferencia entre cuentas por CVU
                    account.setBalance(account.getBalance().subtract(transactionRequestDto.getAmount()));
                    destinationAccount.setBalance(destinationAccount.getBalance().add(transactionRequestDto.getAmount()));
                }

                accountRepository.save(destinationAccount);
                break;
        }
        accountRepository.save(account);
    }

}

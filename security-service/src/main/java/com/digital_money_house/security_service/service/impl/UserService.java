package com.digital_money_house.security_service.service.impl;

import com.digital_money_house.security_service.dto.request.RegisterRequestDto;
import com.digital_money_house.security_service.dto.response.UserResponseDto;
import com.digital_money_house.security_service.entity.User;
import com.digital_money_house.security_service.exception.ResourceNotFoundException;
import com.digital_money_house.security_service.repository.IUserRepository;
import com.digital_money_house.security_service.service.EmailService;
import com.digital_money_house.security_service.service.IService;
import com.digital_money_house.security_service.utils.MapperClass;
import com.digital_money_house.security_service.utils.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService implements IService<UserResponseDto, RegisterRequestDto> {

    private final IUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private static final ObjectMapper objectMapper = MapperClass.objectMapper();

    public void update(RegisterRequestDto userRequestDto) {
        User userDB = userRepository.findById(userRequestDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe.", HttpStatus.NOT_FOUND.value()));

        String newName = userRequestDto.getFirstName();
        if (newName != null) {
            ValidationUtils.validateWord(newName);
            ValidationUtils.processNameNormalization(userRequestDto);
            userDB.setFirstName(newName);
        }

        String newLastName = userRequestDto.getLastName();
        if (newLastName != null) {
            ValidationUtils.validateWord(newLastName);
            ValidationUtils.processNameNormalization(userRequestDto);
            userDB.setLastName(newLastName);
        }

        String newEmail = userRequestDto.getEmail();
        if (newEmail != null) {
            ValidationUtils.validateEmail(newEmail);
            userDB.setEmail(newEmail);
        }

        String newPhoneNumber = userRequestDto.getPhoneNumber();
        if (newPhoneNumber != null) {
            ValidationUtils.validatePhoneNumber(newPhoneNumber);
            userDB.setPhoneNumber(newPhoneNumber);
        }

        String newDni = userRequestDto.getDni();
        if (newDni != null) {
            ValidationUtils.validateDni(newDni);
            userDB.setDni(newDni);
        }

        userRepository.save(userDB);
    }

    @Override
    public UserResponseDto findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("El usuario no existe.", HttpStatus.NOT_FOUND.value()));
        return objectMapper.convertValue(user, UserResponseDto.class);
    }

    @Override
    public void deleteById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("El usuario no existe.", HttpStatus.NOT_FOUND.value()));
        userRepository.delete(user);
    }

    @Override
    public List<UserResponseDto> listAll() {
        List<User> usersList = userRepository.findAll();

        return usersList
                .stream()
                .map(user -> objectMapper.convertValue(user, UserResponseDto.class))
                .collect(Collectors.toList());
    }

    public UserResponseDto findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("El usuario no existe.", HttpStatus.NOT_FOUND.value()));
        return objectMapper.convertValue(user, UserResponseDto.class);
    }

}

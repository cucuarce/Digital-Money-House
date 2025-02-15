package com.digital_money_house.security_service.controller;

import com.digital_money_house.security_service.dto.JsonMessageDto;
import com.digital_money_house.security_service.dto.request.RegisterRequestDto;
import com.digital_money_house.security_service.dto.response.UserResponseDto;
import com.digital_money_house.security_service.service.impl.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // Comentar para que me permita las solicitudes desde el gateway al front
@RestController
@RequestMapping("/users/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<UserResponseDto> getUserById (@PathVariable Long id) {
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<?> deleteUserById (@PathVariable Long id){
        userService.deleteById(id);
        return new ResponseEntity<>(new JsonMessageDto("Usuario eliminado exitosamente.",HttpStatus.OK.value()), HttpStatus.OK);
    }

    @GetMapping
    @Secured("ADMIN")
    public ResponseEntity<List<UserResponseDto>> listUsers (){
        return new ResponseEntity<>(userService.listAll(),HttpStatus.OK);
    }

    @PutMapping
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<?> updateUser (@RequestBody @Valid RegisterRequestDto userRequestDto){
        userService.update(userRequestDto);
        return new ResponseEntity<>(new JsonMessageDto("Usuario actualizado exitosamente.",HttpStatus.OK.value()), HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        return new ResponseEntity<>(userService.findByEmail(email), HttpStatus.OK);
    }

}

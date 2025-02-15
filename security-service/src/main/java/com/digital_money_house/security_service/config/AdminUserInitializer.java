package com.digital_money_house.security_service.config;

import com.digital_money_house.security_service.entity.Role;
import com.digital_money_house.security_service.entity.User;
import com.digital_money_house.security_service.repository.IUserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AdminUserInitializer {

    @Autowired
    private IUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createInitialAdminUser() {
        if (userRepository.count() == 0) {
            User adminUser = new User();
            adminUser.setFirstName("Admin");
            adminUser.setLastName("Admin");
            adminUser.setEmail("admin@mail.com");
            String rawPassword = "contraseña";
            String encodedPassword = passwordEncoder.encode(rawPassword);
            adminUser.setPassword(encodedPassword);
            adminUser.setRole(Role.ADMIN);
            userRepository.save(adminUser);
        }
    }
}


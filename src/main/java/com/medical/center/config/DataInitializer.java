package com.medical.center.config;

import com.medical.center.model.Permission;
import com.medical.center.model.Role;
import com.medical.center.model.User;
import com.medical.center.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        userRepository.findByUsername("admin").ifPresentOrElse(
            admin -> {
                // Всегда обновляем пароль до корректного BCrypt хеша
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEnabled(true);
                userRepository.save(admin);
                log.info("Пароль admin обновлён");
            },
            () -> {
                Permission p1 = createPermission("PATIENT", "READ");
                Permission p2 = createPermission("PATIENT", "WRITE");
                Permission p3 = createPermission("DOCTOR", "READ");
                Permission p4 = createPermission("DOCTOR", "WRITE");
                Permission p5 = createPermission("APPOINTMENT", "READ");
                Permission p6 = createPermission("APPOINTMENT", "WRITE");
                Permission p7 = createPermission("RECORD", "READ");
                Permission p8 = createPermission("RECORD", "WRITE");
                Permission p9 = createPermission("REPORT", "READ");

                Role adminRole = new Role();
                adminRole.setTitle("ROLE_ADMIN");
                adminRole.setPermissions(Set.of(p1, p2, p3, p4, p5, p6, p7, p8, p9));
                entityManager.persist(adminRole);

                User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .enabled(true)
                    .roles(Set.of(adminRole))
                    .build();
                userRepository.save(admin);
                log.info("Создан пользователь admin");
            }
        );
    }

    private Permission createPermission(String permission, String operation) {
        Permission p = new Permission();
        p.setPermission(permission);
        p.setOperation(operation);
        entityManager.persist(p);
        return p;
    }
}

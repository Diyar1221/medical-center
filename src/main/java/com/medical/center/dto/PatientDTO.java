package com.medical.center.dto;

import com.medical.center.validation.ValidPhone;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PatientDTO {
    private Long id;

    @NotBlank(message = "Имя обязательно")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    private String lastName;

    private String middleName;

    @NotNull(message = "Дата рождения обязательна")
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthDate;

    @ValidPhone
    private String phone;

    @Email(message = "Некорректный email")
    private String email;

    private String address;

    private LocalDateTime createdAt;
}

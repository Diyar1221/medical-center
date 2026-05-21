package com.medical.center.dto;

import com.medical.center.validation.ValidPhone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DoctorDTO {
    private Long id;

    @NotBlank(message = "Имя обязательно")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    private String lastName;

    private String middleName;

    @NotNull(message = "Специализация обязательна")
    private Long specializationId;

    private String specializationName;

    @ValidPhone
    private String phone;

    @Email(message = "Некорректный email")
    private String email;

    @Min(value = 0, message = "Стаж не может быть отрицательным")
    private Integer experienceYears;

    private LocalDateTime createdAt;
}

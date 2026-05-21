package com.medical.center.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MedicalRecordDTO {
    private Long id;

    @NotNull(message = "Пациент обязателен")
    private Long patientId;

    private String patientFullName;

    @NotNull(message = "Врач обязателен")
    private Long doctorId;

    private String doctorFullName;

    private Long appointmentId;

    @NotNull(message = "Дата визита обязательна")
    private LocalDateTime visitDate;

    @NotBlank(message = "Диагноз обязателен")
    private String diagnosis;

    private String treatment;
    private String notes;
    private LocalDateTime createdAt;
}

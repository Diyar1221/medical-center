package com.medical.center.dto;

import com.medical.center.model.enums.AppointmentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDTO {
    private Long id;

    @NotNull(message = "Пациент обязателен")
    private Long patientId;

    private String patientFullName;

    @NotNull(message = "Врач обязателен")
    private Long doctorId;

    private String doctorFullName;
    private String doctorSpecialization;

    @NotNull(message = "Дата и время обязательны")
    @Future(message = "Дата приёма должна быть в будущем")
    private LocalDateTime dateTime;

    private AppointmentStatus status;
    private String notes;
    private LocalDateTime createdAt;
}

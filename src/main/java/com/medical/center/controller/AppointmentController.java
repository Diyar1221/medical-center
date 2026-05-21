package com.medical.center.controller;

import com.medical.center.dto.AppointmentDTO;
import com.medical.center.model.enums.AppointmentStatus;
import com.medical.center.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Записи на приём", description = "Управление записями пациентов к врачам")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    @Operation(summary = "Получить все записи")
    public ResponseEntity<List<AppointmentDTO>> getAll() {
        return ResponseEntity.ok(appointmentService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить запись по ID")
    public ResponseEntity<AppointmentDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getById(id));
    }

    @GetMapping("/by-patient/{patientId}")
    @Operation(summary = "Записи пациента")
    public ResponseEntity<List<AppointmentDTO>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getByPatient(patientId));
    }

    @GetMapping("/by-doctor/{doctorId}")
    @Operation(summary = "Записи к врачу")
    public ResponseEntity<List<AppointmentDTO>> getByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getByDoctor(doctorId));
    }

    @GetMapping("/schedule")
    @Operation(summary = "Расписание врача на день")
    public ResponseEntity<List<AppointmentDTO>> getSchedule(
        @RequestParam Long doctorId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.getDoctorSchedule(doctorId, date));
    }

    @GetMapping("/by-status")
    @Operation(summary = "Записи по статусу")
    public ResponseEntity<List<AppointmentDTO>> getByStatus(@RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.getByStatus(status));
    }

    @PostMapping
    @Operation(summary = "Создать запись на приём")
    public ResponseEntity<AppointmentDTO> create(@Valid @RequestBody AppointmentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить запись")
    public ResponseEntity<AppointmentDTO> update(@PathVariable Long id,
                                                  @Valid @RequestBody AppointmentDTO dto) {
        return ResponseEntity.ok(appointmentService.update(id, dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Изменить статус записи")
    public ResponseEntity<AppointmentDTO> changeStatus(@PathVariable Long id,
                                                        @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.changeStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить запись")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

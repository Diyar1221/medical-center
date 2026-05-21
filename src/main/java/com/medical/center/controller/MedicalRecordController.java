package com.medical.center.controller;

import com.medical.center.dto.MedicalRecordDTO;
import com.medical.center.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
@Tag(name = "Медицинские карты", description = "Управление медицинскими картами пациентов")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @GetMapping
    @Operation(summary = "Получить все медицинские карты")
    public ResponseEntity<List<MedicalRecordDTO>> getAll() {
        return ResponseEntity.ok(medicalRecordService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить медицинскую карту по ID")
    public ResponseEntity<MedicalRecordDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalRecordService.getById(id));
    }

    @GetMapping("/by-patient/{patientId}")
    @Operation(summary = "История болезни пациента")
    public ResponseEntity<List<MedicalRecordDTO>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicalRecordService.getByPatient(patientId));
    }

    @GetMapping("/by-doctor/{doctorId}")
    @Operation(summary = "Карты, созданные врачом")
    public ResponseEntity<List<MedicalRecordDTO>> getByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(medicalRecordService.getByDoctor(doctorId));
    }

    @PostMapping
    @Operation(summary = "Создать медицинскую карту")
    public ResponseEntity<MedicalRecordDTO> create(@Valid @RequestBody MedicalRecordDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicalRecordService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить медицинскую карту")
    public ResponseEntity<MedicalRecordDTO> update(@PathVariable Long id,
                                                    @Valid @RequestBody MedicalRecordDTO dto) {
        return ResponseEntity.ok(medicalRecordService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить медицинскую карту")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicalRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

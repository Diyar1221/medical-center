package com.medical.center.controller;

import com.medical.center.dto.PatientDTO;
import com.medical.center.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Пациенты", description = "Управление пациентами")
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    @Operation(summary = "Получить всех пациентов")
    public ResponseEntity<List<PatientDTO>> getAll() {
        return ResponseEntity.ok(patientService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пациента по ID")
    public ResponseEntity<PatientDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск пациентов по имени/фамилии")
    public ResponseEntity<List<PatientDTO>> search(@RequestParam String name) {
        return ResponseEntity.ok(patientService.search(name));
    }

    @PostMapping
    @Operation(summary = "Создать пациента")
    public ResponseEntity<PatientDTO> create(@Valid @RequestBody PatientDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные пациента")
    public ResponseEntity<PatientDTO> update(@PathVariable Long id,
                                              @Valid @RequestBody PatientDTO dto) {
        return ResponseEntity.ok(patientService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пациента")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

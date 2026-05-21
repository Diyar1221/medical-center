package com.medical.center.controller;

import com.medical.center.dto.DoctorDTO;
import com.medical.center.service.DoctorService;
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
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Врачи", description = "Управление врачами")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    @Operation(summary = "Получить всех врачей")
    public ResponseEntity<List<DoctorDTO>> getAll() {
        return ResponseEntity.ok(doctorService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить врача по ID")
    public ResponseEntity<DoctorDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск врача по имени/фамилии")
    public ResponseEntity<List<DoctorDTO>> search(@RequestParam String name) {
        return ResponseEntity.ok(doctorService.search(name));
    }

    @GetMapping("/by-specialization/{specializationId}")
    @Operation(summary = "Врачи по специализации")
    public ResponseEntity<List<DoctorDTO>> getBySpecialization(@PathVariable Long specializationId) {
        return ResponseEntity.ok(doctorService.getBySpecialization(specializationId));
    }

    @PostMapping
    @Operation(summary = "Создать врача")
    public ResponseEntity<DoctorDTO> create(@Valid @RequestBody DoctorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные врача")
    public ResponseEntity<DoctorDTO> update(@PathVariable Long id,
                                             @Valid @RequestBody DoctorDTO dto) {
        return ResponseEntity.ok(doctorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить врача")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package com.medical.center.controller;

import com.medical.center.dto.SpecializationDTO;
import com.medical.center.service.SpecializationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specializations")
@RequiredArgsConstructor
@Tag(name = "Специализации", description = "Управление специализациями врачей")
public class SpecializationController {

    private final SpecializationService specializationService;

    @GetMapping
    @Operation(summary = "Получить все специализации")
    public ResponseEntity<List<SpecializationDTO>> getAll() {
        return ResponseEntity.ok(specializationService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить специализацию по ID")
    public ResponseEntity<SpecializationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(specializationService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Создать специализацию")
    public ResponseEntity<SpecializationDTO> create(@Valid @RequestBody SpecializationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(specializationService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить специализацию")
    public ResponseEntity<SpecializationDTO> update(@PathVariable Long id,
                                                     @Valid @RequestBody SpecializationDTO dto) {
        return ResponseEntity.ok(specializationService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить специализацию")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        specializationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

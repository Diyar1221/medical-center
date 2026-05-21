package com.medical.center.controller;

import com.medical.center.dto.StatsDTO;
import com.medical.center.service.ReportService;
import com.medical.center.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Отчёты и статистика", description = "Статистика и экспорт отчётов")
public class ReportController {

    private final ReportService reportService;
    private final StatsService statsService;

    @GetMapping("/stats")
    @Operation(summary = "Общая статистика медицинского центра")
    public ResponseEntity<StatsDTO> getStats() {
        return ResponseEntity.ok(statsService.getStats());
    }

    @GetMapping("/appointments/export")
    @Operation(summary = "Экспорт записей на приём в XLSX")
    public ResponseEntity<byte[]> exportAppointments(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        byte[] data = reportService.generateAppointmentsReport(from, to);
        log.info("Экспорт отчёта по записям с {} по {}", from, to);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=appointments_report.xlsx")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(data);
    }

    @GetMapping("/patients/export")
    @Operation(summary = "Экспорт списка пациентов в XLSX")
    public ResponseEntity<byte[]> exportPatients() {
        byte[] data = reportService.generatePatientsReport();
        log.info("Экспорт отчёта по пациентам");

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=patients_report.xlsx")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(data);
    }
}

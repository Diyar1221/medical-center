package com.medical.center.service;

import com.medical.center.model.Appointment;
import com.medical.center.model.Patient;
import com.medical.center.repository.AppointmentRepository;
import com.medical.center.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public byte[] generateAppointmentsReport(LocalDateTime from, LocalDateTime to) {
        log.info("Генерация отчёта по записям с {} по {}", from, to);
        List<Appointment> appointments = appointmentRepository.findByDateRange(from, to);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Записи на приём");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);

            // Заголовок отчёта
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Отчёт по записям на приём медицинского центра");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

            Row periodRow = sheet.createRow(1);
            periodRow.createCell(0).setCellValue(
                "Период: " + from.format(DATE_FMT) + " — " + to.format(DATE_FMT)
            );
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

            // Заголовки столбцов
            Row headerRow = sheet.createRow(3);
            String[] headers = {"№", "Пациент", "Врач", "Специализация", "Дата/Время", "Статус", "Примечания"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Данные
            int rowNum = 4;
            for (int i = 0; i < appointments.size(); i++) {
                Appointment a = appointments.get(i);
                Row row = sheet.createRow(rowNum++);

                createDataCell(row, 0, String.valueOf(i + 1), dataStyle);
                createDataCell(row, 1, a.getPatient().getLastName() + " " + a.getPatient().getFirstName(), dataStyle);
                createDataCell(row, 2, a.getDoctor().getLastName() + " " + a.getDoctor().getFirstName(), dataStyle);
                createDataCell(row, 3,
                    a.getDoctor().getSpecialization() != null ? a.getDoctor().getSpecialization().getName() : "",
                    dataStyle);
                createDataCell(row, 4, a.getDateTime().format(FMT), dataStyle);
                createDataCell(row, 5, a.getStatus().name(), dataStyle);
                createDataCell(row, 6, a.getNotes() != null ? a.getNotes() : "", dataStyle);
            }

            // Итог
            Row totalRow = sheet.createRow(rowNum + 1);
            Cell totalLabel = totalRow.createCell(0);
            totalLabel.setCellValue("Итого записей: " + appointments.size());
            totalLabel.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, 0, 6));

            // Автоширина столбцов
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            log.info("Отчёт успешно сгенерирован: {} записей", appointments.size());
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Ошибка генерации отчёта", e);
            throw new RuntimeException("Ошибка генерации отчёта", e);
        }
    }

    public byte[] generatePatientsReport() {
        log.info("Генерация отчёта по пациентам");
        List<Patient> patients = patientRepository.findAll();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Пациенты");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Список пациентов медицинского центра");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            Row headerRow = sheet.createRow(2);
            String[] headers = {"№", "Фамилия", "Имя", "Отчество", "Дата рождения", "Телефон"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 3;
            for (int i = 0; i < patients.size(); i++) {
                Patient p = patients.get(i);
                Row row = sheet.createRow(rowNum++);
                createDataCell(row, 0, String.valueOf(i + 1), dataStyle);
                createDataCell(row, 1, p.getLastName(), dataStyle);
                createDataCell(row, 2, p.getFirstName(), dataStyle);
                createDataCell(row, 3, p.getMiddleName() != null ? p.getMiddleName() : "", dataStyle);
                createDataCell(row, 4, p.getBirthDate().format(DATE_FMT), dataStyle);
                createDataCell(row, 5, p.getPhone() != null ? p.getPhone() : "", dataStyle);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка генерации отчёта", e);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private void createDataCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}

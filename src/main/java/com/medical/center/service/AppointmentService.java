package com.medical.center.service;

import com.medical.center.dto.AppointmentDTO;
import com.medical.center.exception.BusinessException;
import com.medical.center.exception.ResourceNotFoundException;
import com.medical.center.model.Appointment;
import com.medical.center.model.Doctor;
import com.medical.center.model.Patient;
import com.medical.center.model.enums.AppointmentStatus;
import com.medical.center.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final TelegramNotificationService telegramService;

    public List<AppointmentDTO> getAll() {
        return appointmentRepository.findAll().stream().map(this::toDto).toList();
    }

    public AppointmentDTO getById(Long id) {
        return toDto(findById(id));
    }

    public List<AppointmentDTO> getByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream().map(this::toDto).toList();
    }

    public List<AppointmentDTO> getByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream().map(this::toDto).toList();
    }

    public List<AppointmentDTO> getByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status).stream().map(this::toDto).toList();
    }

    public List<AppointmentDTO> getDoctorSchedule(Long doctorId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        return appointmentRepository.findScheduleByDoctorAndDate(doctorId, start, end)
            .stream().map(this::toDto).toList();
    }

    public List<AppointmentDTO> getByDateRange(LocalDateTime from, LocalDateTime to) {
        return appointmentRepository.findByDateRange(from, to).stream().map(this::toDto).toList();
    }

    @Transactional
    public AppointmentDTO create(AppointmentDTO dto) {
        Patient patient = patientService.findById(dto.getPatientId());
        Doctor doctor = doctorService.findById(dto.getDoctorId());

        List<Appointment> conflicts = appointmentRepository.findConflicts(doctor.getId(), dto.getDateTime());
        if (!conflicts.isEmpty()) {
            throw new BusinessException("Врач уже занят в это время");
        }

        Appointment appointment = Appointment.builder()
            .patient(patient)
            .doctor(doctor)
            .dateTime(dto.getDateTime())
            .status(AppointmentStatus.SCHEDULED)
            .notes(dto.getNotes())
            .build();

        Appointment saved = appointmentRepository.save(appointment);
        log.info("Создана запись на приём ID={}", saved.getId());
        telegramService.notifyNewAppointment(saved);
        return toDto(saved);
    }

    @Transactional
    public AppointmentDTO update(Long id, AppointmentDTO dto) {
        Appointment appointment = findById(id);
        appointment.setDateTime(dto.getDateTime());
        appointment.setNotes(dto.getNotes());
        log.info("Обновлена запись ID={}", id);
        return toDto(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentDTO changeStatus(Long id, AppointmentStatus status) {
        Appointment appointment = findById(id);
        appointment.setStatus(status);
        Appointment saved = appointmentRepository.save(appointment);
        log.info("Статус записи ID={} изменён на {}", id, status);

        if (status == AppointmentStatus.CANCELLED) {
            telegramService.notifyAppointmentCancelled(saved);
        } else if (status == AppointmentStatus.COMPLETED) {
            telegramService.notifyAppointmentCompleted(saved);
        }
        return toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        appointmentRepository.deleteById(id);
        log.info("Удалена запись ID={}", id);
    }

    public Appointment findById(Long id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Запись на приём", id));
    }

    public AppointmentDTO toDto(Appointment a) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(a.getId());
        dto.setPatientId(a.getPatient().getId());
        dto.setPatientFullName(a.getPatient().getLastName() + " " + a.getPatient().getFirstName());
        dto.setDoctorId(a.getDoctor().getId());
        dto.setDoctorFullName(a.getDoctor().getLastName() + " " + a.getDoctor().getFirstName());
        if (a.getDoctor().getSpecialization() != null) {
            dto.setDoctorSpecialization(a.getDoctor().getSpecialization().getName());
        }
        dto.setDateTime(a.getDateTime());
        dto.setStatus(a.getStatus());
        dto.setNotes(a.getNotes());
        dto.setCreatedAt(a.getCreatedAt());
        return dto;
    }
}

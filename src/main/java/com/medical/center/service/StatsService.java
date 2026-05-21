package com.medical.center.service;

import com.medical.center.dto.StatsDTO;
import com.medical.center.model.enums.AppointmentStatus;
import com.medical.center.repository.AppointmentRepository;
import com.medical.center.repository.DoctorRepository;
import com.medical.center.repository.MedicalRecordRepository;
import com.medical.center.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public StatsDTO getStats() {
        log.debug("Получение статистики медицинского центра");
        return StatsDTO.builder()
            .totalPatients(patientRepository.count())
            .totalDoctors(doctorRepository.count())
            .totalAppointments(appointmentRepository.count())
            .scheduledAppointments(appointmentRepository.countByStatus(AppointmentStatus.SCHEDULED))
            .completedAppointments(appointmentRepository.countByStatus(AppointmentStatus.COMPLETED))
            .cancelledAppointments(appointmentRepository.countByStatus(AppointmentStatus.CANCELLED))
            .totalMedicalRecords(medicalRecordRepository.count())
            .build();
    }
}

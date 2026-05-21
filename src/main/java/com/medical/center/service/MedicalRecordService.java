package com.medical.center.service;

import com.medical.center.dto.MedicalRecordDTO;
import com.medical.center.exception.ResourceNotFoundException;
import com.medical.center.model.Appointment;
import com.medical.center.model.Doctor;
import com.medical.center.model.MedicalRecord;
import com.medical.center.model.Patient;
import com.medical.center.repository.AppointmentRepository;
import com.medical.center.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentRepository appointmentRepository;

    public List<MedicalRecordDTO> getAll() {
        return medicalRecordRepository.findAll().stream().map(this::toDto).toList();
    }

    public MedicalRecordDTO getById(Long id) {
        return toDto(findById(id));
    }

    public List<MedicalRecordDTO> getByPatient(Long patientId) {
        return medicalRecordRepository.findByPatientIdOrderByVisitDateDesc(patientId)
            .stream().map(this::toDto).toList();
    }

    public List<MedicalRecordDTO> getByDoctor(Long doctorId) {
        return medicalRecordRepository.findByDoctorId(doctorId).stream().map(this::toDto).toList();
    }

    @Transactional
    public MedicalRecordDTO create(MedicalRecordDTO dto) {
        Patient patient = patientService.findById(dto.getPatientId());
        Doctor doctor = doctorService.findById(dto.getDoctorId());

        Appointment appointment = null;
        if (dto.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(dto.getAppointmentId()).orElse(null);
        }

        MedicalRecord record = MedicalRecord.builder()
            .patient(patient)
            .doctor(doctor)
            .appointment(appointment)
            .visitDate(dto.getVisitDate())
            .diagnosis(dto.getDiagnosis())
            .treatment(dto.getTreatment())
            .notes(dto.getNotes())
            .build();

        MedicalRecord saved = medicalRecordRepository.save(record);
        log.info("Создана медицинская карта ID={} для пациента ID={}", saved.getId(), patient.getId());
        return toDto(saved);
    }

    @Transactional
    public MedicalRecordDTO update(Long id, MedicalRecordDTO dto) {
        MedicalRecord record = findById(id);
        record.setVisitDate(dto.getVisitDate());
        record.setDiagnosis(dto.getDiagnosis());
        record.setTreatment(dto.getTreatment());
        record.setNotes(dto.getNotes());
        log.info("Обновлена медицинская карта ID={}", id);
        return toDto(medicalRecordRepository.save(record));
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        medicalRecordRepository.deleteById(id);
        log.info("Удалена медицинская карта ID={}", id);
    }

    private MedicalRecord findById(Long id) {
        return medicalRecordRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Медицинская карта", id));
    }

    public MedicalRecordDTO toDto(MedicalRecord r) {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setId(r.getId());
        dto.setPatientId(r.getPatient().getId());
        dto.setPatientFullName(r.getPatient().getLastName() + " " + r.getPatient().getFirstName());
        dto.setDoctorId(r.getDoctor().getId());
        dto.setDoctorFullName(r.getDoctor().getLastName() + " " + r.getDoctor().getFirstName());
        if (r.getAppointment() != null) dto.setAppointmentId(r.getAppointment().getId());
        dto.setVisitDate(r.getVisitDate());
        dto.setDiagnosis(r.getDiagnosis());
        dto.setTreatment(r.getTreatment());
        dto.setNotes(r.getNotes());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }
}

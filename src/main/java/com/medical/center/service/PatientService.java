package com.medical.center.service;

import com.medical.center.dto.PatientDTO;
import com.medical.center.exception.ResourceNotFoundException;
import com.medical.center.model.Patient;
import com.medical.center.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;

    public List<PatientDTO> getAll() {
        log.debug("Получение всех пациентов");
        return patientRepository.findAll().stream().map(this::toDto).toList();
    }

    public PatientDTO getById(Long id) {
        return toDto(findById(id));
    }

    public List<PatientDTO> search(String name) {
        return patientRepository.searchByName(name).stream().map(this::toDto).toList();
    }

    @Transactional
    public PatientDTO create(PatientDTO dto) {
        Patient patient = Patient.builder()
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .middleName(dto.getMiddleName())
            .birthDate(dto.getBirthDate())
            .phone(dto.getPhone())
            .email(dto.getEmail())
            .address(dto.getAddress())
            .build();
        Patient saved = patientRepository.save(patient);
        log.info("Создан пациент: {} {}", saved.getLastName(), saved.getFirstName());
        return toDto(saved);
    }

    @Transactional
    public PatientDTO update(Long id, PatientDTO dto) {
        Patient patient = findById(id);
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setMiddleName(dto.getMiddleName());
        patient.setBirthDate(dto.getBirthDate());
        patient.setPhone(dto.getPhone());
        patient.setEmail(dto.getEmail());
        patient.setAddress(dto.getAddress());
        log.info("Обновлён пациент ID={}", id);
        return toDto(patientRepository.save(patient));
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        patientRepository.deleteById(id);
        log.info("Удалён пациент ID={}", id);
    }

    public Patient findById(Long id) {
        return patientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Пациент", id));
    }

    public PatientDTO toDto(Patient p) {
        PatientDTO dto = new PatientDTO();
        dto.setId(p.getId());
        dto.setFirstName(p.getFirstName());
        dto.setLastName(p.getLastName());
        dto.setMiddleName(p.getMiddleName());
        dto.setBirthDate(p.getBirthDate());
        dto.setPhone(p.getPhone());
        dto.setEmail(p.getEmail());
        dto.setAddress(p.getAddress());
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }
}

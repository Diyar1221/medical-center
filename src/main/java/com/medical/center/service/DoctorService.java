package com.medical.center.service;

import com.medical.center.dto.DoctorDTO;
import com.medical.center.exception.ResourceNotFoundException;
import com.medical.center.model.Doctor;
import com.medical.center.model.Specialization;
import com.medical.center.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecializationService specializationService;

    public List<DoctorDTO> getAll() {
        log.debug("Получение всех врачей");
        return doctorRepository.findAll().stream().map(this::toDto).toList();
    }

    public DoctorDTO getById(Long id) {
        return toDto(findById(id));
    }

    public List<DoctorDTO> getBySpecialization(Long specializationId) {
        return doctorRepository.findBySpecializationId(specializationId).stream()
            .map(this::toDto).toList();
    }

    public List<DoctorDTO> search(String name) {
        return doctorRepository.searchByName(name).stream().map(this::toDto).toList();
    }

    @Transactional
    public DoctorDTO create(DoctorDTO dto) {
        Specialization specialization = specializationService.findById(dto.getSpecializationId());
        Doctor doctor = Doctor.builder()
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .middleName(dto.getMiddleName())
            .specialization(specialization)
            .phone(dto.getPhone())
            .email(dto.getEmail())
            .experienceYears(dto.getExperienceYears())
            .build();
        Doctor saved = doctorRepository.save(doctor);
        log.info("Создан врач: {} {}", saved.getLastName(), saved.getFirstName());
        return toDto(saved);
    }

    @Transactional
    public DoctorDTO update(Long id, DoctorDTO dto) {
        Doctor doctor = findById(id);
        Specialization specialization = specializationService.findById(dto.getSpecializationId());
        doctor.setFirstName(dto.getFirstName());
        doctor.setLastName(dto.getLastName());
        doctor.setMiddleName(dto.getMiddleName());
        doctor.setSpecialization(specialization);
        doctor.setPhone(dto.getPhone());
        doctor.setEmail(dto.getEmail());
        doctor.setExperienceYears(dto.getExperienceYears());
        log.info("Обновлён врач ID={}", id);
        return toDto(doctorRepository.save(doctor));
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        doctorRepository.deleteById(id);
        log.info("Удалён врач ID={}", id);
    }

    public Doctor findById(Long id) {
        return doctorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Врач", id));
    }

    public DoctorDTO toDto(Doctor d) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(d.getId());
        dto.setFirstName(d.getFirstName());
        dto.setLastName(d.getLastName());
        dto.setMiddleName(d.getMiddleName());
        dto.setPhone(d.getPhone());
        dto.setEmail(d.getEmail());
        dto.setExperienceYears(d.getExperienceYears());
        dto.setCreatedAt(d.getCreatedAt());
        if (d.getSpecialization() != null) {
            dto.setSpecializationId(d.getSpecialization().getId());
            dto.setSpecializationName(d.getSpecialization().getName());
        }
        return dto;
    }
}

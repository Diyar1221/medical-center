package com.medical.center.service;

import com.medical.center.dto.SpecializationDTO;
import com.medical.center.exception.BusinessException;
import com.medical.center.exception.ResourceNotFoundException;
import com.medical.center.model.Specialization;
import com.medical.center.repository.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpecializationService {

    private final SpecializationRepository specializationRepository;

    public List<SpecializationDTO> getAll() {
        log.debug("Получение всех специализаций");
        return specializationRepository.findAll().stream()
            .map(this::toDto)
            .toList();
    }

    public SpecializationDTO getById(Long id) {
        return toDto(findById(id));
    }

    @Transactional
    public SpecializationDTO create(SpecializationDTO dto) {
        if (specializationRepository.existsByName(dto.getName())) {
            throw new BusinessException("Специализация с таким названием уже существует");
        }
        Specialization saved = specializationRepository.save(toEntity(dto));
        log.info("Создана специализация: {}", saved.getName());
        return toDto(saved);
    }

    @Transactional
    public SpecializationDTO update(Long id, SpecializationDTO dto) {
        Specialization existing = findById(id);
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        log.info("Обновлена специализация ID={}", id);
        return toDto(specializationRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        specializationRepository.deleteById(id);
        log.info("Удалена специализация ID={}", id);
    }

    public Specialization findById(Long id) {
        return specializationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Специализация", id));
    }

    private SpecializationDTO toDto(Specialization s) {
        SpecializationDTO dto = new SpecializationDTO();
        dto.setId(s.getId());
        dto.setName(s.getName());
        dto.setDescription(s.getDescription());
        return dto;
    }

    private Specialization toEntity(SpecializationDTO dto) {
        return Specialization.builder()
            .name(dto.getName())
            .description(dto.getDescription())
            .build();
    }
}

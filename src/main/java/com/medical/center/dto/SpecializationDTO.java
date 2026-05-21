package com.medical.center.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SpecializationDTO {
    private Long id;

    @NotBlank(message = "Название специализации обязательно")
    private String name;

    private String description;
}

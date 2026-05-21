package com.medical.center.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatsDTO {
    private long totalPatients;
    private long totalDoctors;
    private long totalAppointments;
    private long scheduledAppointments;
    private long completedAppointments;
    private long cancelledAppointments;
    private long totalMedicalRecords;
}

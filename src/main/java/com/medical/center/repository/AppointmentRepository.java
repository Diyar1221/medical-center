package com.medical.center.repository;

import com.medical.center.model.Appointment;
import com.medical.center.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByStatus(AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.dateTime BETWEEN :start AND :end ORDER BY a.dateTime")
    List<Appointment> findScheduleByDoctorAndDate(
        @Param("doctorId") Long doctorId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT a FROM Appointment a WHERE a.dateTime BETWEEN :start AND :end ORDER BY a.dateTime")
    List<Appointment> findByDateRange(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    long countByStatus(AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.dateTime = :dateTime AND a.status = 'SCHEDULED'")
    List<Appointment> findConflicts(
        @Param("doctorId") Long doctorId,
        @Param("dateTime") LocalDateTime dateTime
    );
}

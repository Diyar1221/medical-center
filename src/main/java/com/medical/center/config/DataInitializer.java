package com.medical.center.config;

import com.medical.center.model.*;
import com.medical.center.model.enums.AppointmentStatus;
import com.medical.center.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initAdmin();
        if (patientRepository.count() == 0) {
            initSampleData();
        }
    }

    private void initAdmin() {
        userRepository.findByUsername("admin").ifPresentOrElse(
            admin -> {
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEnabled(true);
                userRepository.save(admin);
                log.info("Пароль admin обновлён");
            },
            () -> {
                Permission p1 = persist(new Permission(null, "PATIENT", "READ"));
                Permission p2 = persist(new Permission(null, "PATIENT", "WRITE"));
                Permission p3 = persist(new Permission(null, "DOCTOR", "READ"));
                Permission p4 = persist(new Permission(null, "DOCTOR", "WRITE"));
                Permission p5 = persist(new Permission(null, "APPOINTMENT", "READ"));
                Permission p6 = persist(new Permission(null, "APPOINTMENT", "WRITE"));
                Permission p7 = persist(new Permission(null, "RECORD", "READ"));
                Permission p8 = persist(new Permission(null, "RECORD", "WRITE"));
                Permission p9 = persist(new Permission(null, "REPORT", "READ"));

                Role adminRole = new Role();
                adminRole.setTitle("ROLE_ADMIN");
                adminRole.setPermissions(Set.of(p1, p2, p3, p4, p5, p6, p7, p8, p9));
                entityManager.persist(adminRole);

                userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .enabled(true)
                    .roles(Set.of(adminRole))
                    .build());
                log.info("Создан пользователь admin");
            }
        );
    }

    private void initSampleData() {
        log.info("Загрузка тестовых данных...");

        // Специализации
        Specialization терапевт = getOrCreateSpec("Терапевт", "Общая терапия");
        Specialization кардиолог = getOrCreateSpec("Кардиолог", "Заболевания сердца и сосудов");
        Specialization невролог = getOrCreateSpec("Невролог", "Заболевания нервной системы");
        Specialization хирург = getOrCreateSpec("Хирург", "Хирургические вмешательства");
        Specialization педиатр = getOrCreateSpec("Педиатр", "Лечение детей");

        // Врачи
        Doctor d1 = saveDoctor("Александр", "Петров", "Иванович", терапевт, "+79001234561", "petrov@clinic.ru", 15);
        Doctor d2 = saveDoctor("Мария", "Иванова", "Сергеевна", кардиолог, "+79001234562", "ivanova@clinic.ru", 10);
        Doctor d3 = saveDoctor("Дмитрий", "Сидоров", "Петрович", невролог, "+79001234563", "sidorov@clinic.ru", 8);
        Doctor d4 = saveDoctor("Елена", "Козлова", "Николаевна", хирург, "+79001234564", "kozlova@clinic.ru", 20);
        Doctor d5 = saveDoctor("Ольга", "Новикова", "Андреевна", педиатр, "+79001234565", "novikova@clinic.ru", 12);

        // Пациенты
        Patient p1 = savePatient("Иван", "Смирнов", "Алексеевич", LocalDate.of(1985, 3, 15), "+79111234501", "smirnov@mail.ru", "ул. Ленина, 10");
        Patient p2 = savePatient("Анна", "Кузнецова", "Владимировна", LocalDate.of(1990, 7, 22), "+79111234502", "kuznetsova@mail.ru", "ул. Пушкина, 5");
        Patient p3 = savePatient("Сергей", "Попов", "Михайлович", LocalDate.of(1978, 11, 30), "+79111234503", "popov@mail.ru", "пр. Мира, 25");
        Patient p4 = savePatient("Наталья", "Лебедева", "Юрьевна", LocalDate.of(1995, 1, 10), "+79111234504", "lebedeva@mail.ru", "ул. Гагарина, 7");
        Patient p5 = savePatient("Михаил", "Соколов", "Дмитриевич", LocalDate.of(1970, 5, 18), "+79111234505", "sokolov@mail.ru", "ул. Советская, 33");
        Patient p6 = savePatient("Татьяна", "Морозова", "Игоревна", LocalDate.of(2000, 9, 5), "+79111234506", "morozova@mail.ru", "ул. Гоголя, 12");
        Patient p7 = savePatient("Алексей", "Волков", "Сергеевич", LocalDate.of(1965, 12, 25), "+79111234507", "volkov@mail.ru", "пр. Победы, 44");

        // Записи на приём (прошедшие — COMPLETED)
        Appointment a1 = saveAppointment(p1, d1, LocalDateTime.now().minusDays(10), AppointmentStatus.COMPLETED, "Плановый осмотр");
        Appointment a2 = saveAppointment(p2, d2, LocalDateTime.now().minusDays(7), AppointmentStatus.COMPLETED, "Боли в груди");
        Appointment a3 = saveAppointment(p3, d3, LocalDateTime.now().minusDays(5), AppointmentStatus.COMPLETED, "Головные боли");
        Appointment a4 = saveAppointment(p5, d1, LocalDateTime.now().minusDays(3), AppointmentStatus.COMPLETED, "Повышенное давление");
        Appointment a5 = saveAppointment(p6, d5, LocalDateTime.now().minusDays(2), AppointmentStatus.CANCELLED, "Педиатрический осмотр");

        // Записи (будущие — SCHEDULED)
        saveAppointment(p1, d2, LocalDateTime.now().plusDays(2), AppointmentStatus.SCHEDULED, "Контрольная ЭКГ");
        saveAppointment(p4, d1, LocalDateTime.now().plusDays(3), AppointmentStatus.SCHEDULED, "Плановый осмотр");
        saveAppointment(p7, d4, LocalDateTime.now().plusDays(5), AppointmentStatus.SCHEDULED, "Консультация хирурга");
        saveAppointment(p2, d3, LocalDateTime.now().plusDays(7), AppointmentStatus.SCHEDULED, "МРТ головного мозга");
        saveAppointment(p3, d2, LocalDateTime.now().plusDays(10), AppointmentStatus.SCHEDULED, "Эхокардиограмма");

        // Медицинские карты
        saveMedRecord(p1, d1, a1, "ОРВИ средней степени тяжести", "Парацетамол 500мг, постельный режим, обильное питьё");
        saveMedRecord(p2, d2, a2, "Нейроциркуляторная дистония", "Валокордин, ограничение физических нагрузок, наблюдение");
        saveMedRecord(p3, d3, a3, "Мигрень без ауры", "Суматриптан при приступах, профилактика амитриптилином");
        saveMedRecord(p5, d1, a4, "Артериальная гипертензия II степени", "Лозартан 50мг/сут, ограничение соли, контроль АД");

        log.info("Тестовые данные успешно загружены: 5 врачей, 7 пациентов, 9 записей, 4 медкарты");
    }

    private Specialization getOrCreateSpec(String name, String description) {
        return specializationRepository.findByName(name).orElseGet(() -> {
            Specialization s = new Specialization();
            s.setName(name);
            s.setDescription(description);
            return specializationRepository.save(s);
        });
    }

    private Doctor saveDoctor(String firstName, String lastName, String middleName,
                               Specialization spec, String phone, String email, int exp) {
        Doctor d = new Doctor();
        d.setFirstName(firstName);
        d.setLastName(lastName);
        d.setMiddleName(middleName);
        d.setSpecialization(spec);
        d.setPhone(phone);
        d.setEmail(email);
        d.setExperienceYears(exp);
        return doctorRepository.save(d);
    }

    private Patient savePatient(String firstName, String lastName, String middleName,
                                 LocalDate birthDate, String phone, String email, String address) {
        Patient p = new Patient();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setMiddleName(middleName);
        p.setBirthDate(birthDate);
        p.setPhone(phone);
        p.setEmail(email);
        p.setAddress(address);
        return patientRepository.save(p);
    }

    private Appointment saveAppointment(Patient patient, Doctor doctor,
                                         LocalDateTime dateTime, AppointmentStatus status, String notes) {
        Appointment a = new Appointment();
        a.setPatient(patient);
        a.setDoctor(doctor);
        a.setDateTime(dateTime);
        a.setStatus(status);
        a.setNotes(notes);
        return appointmentRepository.save(a);
    }

    private void saveMedRecord(Patient patient, Doctor doctor, Appointment appointment,
                                String diagnosis, String treatment) {
        MedicalRecord r = new MedicalRecord();
        r.setPatient(patient);
        r.setDoctor(doctor);
        r.setAppointment(appointment);
        r.setVisitDate(appointment.getDateTime());
        r.setDiagnosis(diagnosis);
        r.setTreatment(treatment);
        medicalRecordRepository.save(r);
    }

    private <T> T persist(T entity) {
        entityManager.persist(entity);
        return entity;
    }
}

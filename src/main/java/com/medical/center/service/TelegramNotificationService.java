package com.medical.center.service;

import com.medical.center.model.Appointment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class TelegramNotificationService {

    @Value("${telegram.bot.token:}")
    private String botToken;

    @Value("${telegram.bot.chat-id:}")
    private String chatId;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final RestTemplate restTemplate = new RestTemplate();

    public void notifyNewAppointment(Appointment appointment) {
        String text = String.format(
            "📅 Новая запись на приём\n\n" +
            "Пациент: %s %s\n" +
            "Врач: %s %s (%s)\n" +
            "Дата/время: %s",
            appointment.getPatient().getLastName(), appointment.getPatient().getFirstName(),
            appointment.getDoctor().getLastName(), appointment.getDoctor().getFirstName(),
            appointment.getDoctor().getSpecialization() != null ? appointment.getDoctor().getSpecialization().getName() : "",
            appointment.getDateTime().format(FORMATTER)
        );
        sendMessage(text);
    }

    public void notifyAppointmentCancelled(Appointment appointment) {
        String text = String.format(
            "❌ Запись отменена\n\nПациент: %s %s\nВрач: %s %s\nДата: %s",
            appointment.getPatient().getLastName(), appointment.getPatient().getFirstName(),
            appointment.getDoctor().getLastName(), appointment.getDoctor().getFirstName(),
            appointment.getDateTime().format(FORMATTER)
        );
        sendMessage(text);
    }

    public void notifyAppointmentCompleted(Appointment appointment) {
        String text = String.format(
            "✅ Приём завершён\n\nПациент: %s %s\nВрач: %s %s\nДата: %s",
            appointment.getPatient().getLastName(), appointment.getPatient().getFirstName(),
            appointment.getDoctor().getLastName(), appointment.getDoctor().getFirstName(),
            appointment.getDateTime().format(FORMATTER)
        );
        sendMessage(text);
    }

    private void sendMessage(String text) {
        if (botToken == null || botToken.isBlank() || botToken.equals("YOUR_BOT_TOKEN")) {
            log.debug("Telegram не настроен, уведомление пропущено");
            return;
        }
        try {
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            Map<String, String> body = new HashMap<>();
            body.put("chat_id", chatId);
            body.put("text", text);
            restTemplate.postForObject(url, body, String.class);
            log.info("Telegram уведомление отправлено");
        } catch (Exception e) {
            log.warn("Не удалось отправить Telegram уведомление: {}", e.getMessage());
        }
    }
}

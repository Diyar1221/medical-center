package com.medical.center.service;

import com.medical.center.model.Appointment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class TelegramNotificationService extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.chat-id}")
    private String chatId;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // бот только отправляет уведомления, не обрабатывает команды
    }

    public void notifyNewAppointment(Appointment appointment) {
        String text = String.format(
            "📅 *Новая запись на приём*\n\n" +
            "👤 Пациент: %s %s\n" +
            "🩺 Врач: %s %s (%s)\n" +
            "🕐 Дата/время: %s",
            appointment.getPatient().getLastName(),
            appointment.getPatient().getFirstName(),
            appointment.getDoctor().getLastName(),
            appointment.getDoctor().getFirstName(),
            appointment.getDoctor().getSpecialization().getName(),
            appointment.getDateTime().format(FORMATTER)
        );
        sendNotification(text);
    }

    public void notifyAppointmentCancelled(Appointment appointment) {
        String text = String.format(
            "❌ *Запись отменена*\n\n" +
            "👤 Пациент: %s %s\n" +
            "🩺 Врач: %s %s\n" +
            "🕐 Дата/время: %s",
            appointment.getPatient().getLastName(),
            appointment.getPatient().getFirstName(),
            appointment.getDoctor().getLastName(),
            appointment.getDoctor().getFirstName(),
            appointment.getDateTime().format(FORMATTER)
        );
        sendNotification(text);
    }

    public void notifyAppointmentCompleted(Appointment appointment) {
        String text = String.format(
            "✅ *Приём завершён*\n\n" +
            "👤 Пациент: %s %s\n" +
            "🩺 Врач: %s %s\n" +
            "🕐 Дата/время: %s",
            appointment.getPatient().getLastName(),
            appointment.getPatient().getFirstName(),
            appointment.getDoctor().getLastName(),
            appointment.getDoctor().getFirstName(),
            appointment.getDateTime().format(FORMATTER)
        );
        sendNotification(text);
    }

    private void sendNotification(String text) {
        try {
            SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode("Markdown")
                .build();
            execute(message);
            log.info("Telegram уведомление отправлено");
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки Telegram уведомления: {}", e.getMessage());
        }
    }
}

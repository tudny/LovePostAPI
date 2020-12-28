package com.tudny.lovepost.data;

import com.tudny.lovepost.LovePostClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class Message {
    final private Integer id;
    final private LocalDateTime dateTime;
    final private String message;

    public Message(MessageJson messageJson) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LovePostClient.DATE_TIME_FORMATTER);

        this.id = messageJson.getId();
        this.message = messageJson.getMessage();
        this.dateTime = LocalDateTime.parse(messageJson.getDateTime(), formatter);
    }

    @SuppressWarnings("unused")
    public Integer getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @SuppressWarnings("unused")
    public String getMessage() {
        return message;
    }

    public static Message convert(MessageJson messageJson) {
        return new Message(messageJson);
    }

    public static List<Message> convert(List<MessageJson> messageJsons) {
        return messageJsons.stream().map(Message::convert).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", message='" + message + '\'' +
                '}';
    }
}

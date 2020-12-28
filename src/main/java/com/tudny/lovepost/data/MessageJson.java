package com.tudny.lovepost.data;

import java.time.LocalDateTime;

public class MessageJson {
    final private Integer id;
    final private String dateTime;
    final private String message;

    public MessageJson(Integer id, String dateTime, String message) {
        this.id = id;
        this.dateTime = dateTime;
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getMessage() {
        return message;
    }
}

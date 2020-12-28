package com.tudny.lovepost.data;

import java.time.LocalDateTime;

public class MessageJson {
    private Integer id;
    private String dateTime;
    private String message;

    public MessageJson(Integer id, String dateTime, String message) {
        this.id = id;
        this.dateTime = dateTime;
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

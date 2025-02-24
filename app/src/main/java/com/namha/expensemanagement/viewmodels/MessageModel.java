package com.namha.expensemanagement.viewmodels;

public class MessageModel {

    public static final String SENT_BY_ME = "me";  // Đổi thành public static final
    public static final String SENT_BY_BOT = "bot"; // Đổi thành public static final

    private String message;
    private String sentBy;

    public MessageModel(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }
}

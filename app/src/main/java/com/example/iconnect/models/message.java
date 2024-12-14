package com.example.iconnect.models;

public class message {

    String messageBody;
    String messageBy;
    int feeling = -1;

    public int getFeeling() {
        return feeling;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }

    public String getMesageId() {
        return mesageId;
    }

    public void setMesageId(String mesageId) {
        this.mesageId = mesageId;
    }

    String mesageId;


    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getMessageBy() {
        return messageBy;
    }

    public void setMessageBy(String messageBy) {
        this.messageBy = messageBy;
    }


    public message() {
    }
}

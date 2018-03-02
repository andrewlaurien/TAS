package com.payvenue.txtassistant.Model;

public class Message {
    public String id, dateInit, dateSched, threadId, messsageTo, messageBody, type, status, recipientName;

    public Message(String id, String dateInit, String dateSched, String threadId,
                   String messsageTo, String messageBody, String type, String status,
                   String recipientName) {
        this.id = id;
        this.dateInit = dateInit;
        this.dateSched = dateSched;
        this.threadId = threadId;
        this.messsageTo = messsageTo;
        this.messageBody = messageBody;
        this.type = type;
        this.status = status;
        this.recipientName = recipientName;
    }

    public void updateStatus(String status){
        this.status = status;
    }
}

package models;

import java.sql.Timestamp;

public class MessageM {
    private int id;
    private int senderId;
    private int receiverId;
    private String content;
    private Timestamp dateM;

    public MessageM() {}

    public MessageM(int senderId, int receiverId, String content, Timestamp dateM) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.dateM = dateM;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Timestamp getDateM() { return dateM; }
    public void setDateM(Timestamp dateM) { this.dateM = dateM; }
}

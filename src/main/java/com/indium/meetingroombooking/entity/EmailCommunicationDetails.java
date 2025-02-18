package com.indium.meetingroombooking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_communication_details")
public class EmailCommunicationDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_comm_id")
    private Short emailCommId;

    @Column(name = "from_user")
    private String fromUser;

    @Column(name="email_to",columnDefinition = "json")
    private String emailTo;

    @Column(name="email_cc",columnDefinition = "json")
    private String emailCc;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name="created_on")
    private LocalDateTime createdOn;

    public Short getEmailCommId() {
        return emailCommId;
    }

    public void setEmailCommId(Short emailCommId) {
        this.emailCommId = emailCommId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public String getEmailCc() {
        return emailCc;
    }

    public void setEmailCc(String emailCc) {
        this.emailCc = emailCc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}

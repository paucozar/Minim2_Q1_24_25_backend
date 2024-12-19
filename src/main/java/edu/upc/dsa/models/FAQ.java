package edu.upc.dsa.models;

import java.util.Date;

public class FAQ {
    public String question;
    public String answer;
    public String sender;
    public Date date;

    public FAQ() {
    }

    public FAQ(String question, String answer, String sender, Date date) {
        this.question = question;
        this.answer = answer;
        this.sender = sender;
        this.date = date;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

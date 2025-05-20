package org.bupt.persosnalfinance.dto;

public class ChatRequest {
    private String message;
    public ChatRequest() {}
    public ChatRequest(String msg) { this.message = msg; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}


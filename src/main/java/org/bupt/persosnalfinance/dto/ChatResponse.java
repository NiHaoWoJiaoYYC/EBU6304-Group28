package org.bupt.persosnalfinance.dto;

public class ChatResponse {
    private String answer;
    public ChatResponse() {}
    public ChatResponse(String ans) { this.answer = ans; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}

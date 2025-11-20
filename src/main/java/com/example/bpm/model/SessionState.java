package com.example.bpm.model;

import java.util.ArrayList;
import java.util.List;

public class SessionState {
    private String sessionId;
    private String agentid;
    private String cardMemberNumber;
    private String pnr;
    private List<HubEvent> events = new ArrayList<>();
    private String status;
    private String lastUpdated;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getAgentid() { return agentid; }
    public void setAgentid(String agentid) { this.agentid = agentid; }

    public String getCardMemberNumber() { return cardMemberNumber; }
    public void setCardMemberNumber(String cardMemberNumber) { this.cardMemberNumber = cardMemberNumber; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public List<HubEvent> getEvents() { return events; }
    public void setEvents(List<HubEvent> events) { this.events = events; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
}

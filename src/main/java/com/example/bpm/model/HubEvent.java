package com.example.bpm.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HubEvent {
    private String type;
    private String agentid;
    private String cardMemberNumber;
    private String source;
    private String pnr;
    private String timestamp;

    public HubEvent() {}

    public HubEvent(String type, String agentid, String cardMemberNumber, String source, String pnr, String timestamp) {
        this.type = type;
        this.agentid = agentid;
        this.cardMemberNumber = cardMemberNumber;
        this.source = source;
        this.pnr = pnr;
        this.timestamp = timestamp;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAgentid() { return agentid; }
    public void setAgentid(String agentid) { this.agentid = agentid; }

    public String getCardMemberNumber() { return cardMemberNumber; }
    public void setCardMemberNumber(String cardMemberNumber) { this.cardMemberNumber = cardMemberNumber; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}

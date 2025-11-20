package com.example.bpm;

import com.example.bpm.model.HubEvent;

public class SessionActivitiesImpl implements SessionActivities {
    @Override
    public void logEvent(HubEvent event) {
        System.out.println("[Activity] Logging event: " + event.getType() + 
                           " from source " + event.getSource() +
                           " for session " + event.getAgentid() + 
                           "-" + event.getCardMemberNumber());
    }

    @Override
    public void notifyCRM(HubEvent event) {
        System.out.println("[Activity] Notifying CRM about PNR: " + event.getPnr());
    }
}

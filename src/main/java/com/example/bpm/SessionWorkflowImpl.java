package com.example.bpm;

import com.example.bpm.model.HubEvent;
import com.example.bpm.model.SessionState;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.time.Instant;

public class SessionWorkflowImpl implements SessionWorkflow {

    private final SessionActivities activities = Workflow.newActivityStub(SessionActivities.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofMinutes(1)).build());

    private SessionState state;

    @Override
    public SessionState run(HubEvent initialEvent) {
        String agentid = initialEvent.getAgentid();
        String cardMemberNumber = initialEvent.getCardMemberNumber();
        String sessionId = agentid + "-" + cardMemberNumber;

        state = new SessionState();
        state.setSessionId(sessionId);
        state.setAgentid(agentid);
        state.setCardMemberNumber(cardMemberNumber);
        state.setStatus("ACTIVE");
        state.setLastUpdated(Instant.now().toString());

        System.out.println("[Workflow] Started session " + sessionId);
        
        handleEvent(initialEvent);

        // Wait for completion or timeout
        Workflow.await(Duration.ofHours(1), () -> "COMPLETED".equals(state.getStatus()));
        
        if (!"COMPLETED".equals(state.getStatus())) {
             System.out.println("[Workflow] Session " + sessionId + " timed out.");
             state.setStatus("TIMED_OUT");
        }

        return state;
    }

    @Override
    public void hubEventSignal(HubEvent event) {
        // Wait for workflow initialization if state is not ready yet
        Workflow.await(() -> state != null);
        
        System.out.println("[Workflow] Received signal for session " + state.getSessionId() + ": " + event.getSource());
        handleEvent(event);
    }

    private void handleEvent(HubEvent event) {
        state.getEvents().add(event);
        state.setLastUpdated(Instant.now().toString());
        
        activities.logEvent(event);

        if (event.getPnr() != null) {
            System.out.println("[Workflow] PNR found in event: " + event.getPnr());
            state.setPnr(event.getPnr());
            activities.notifyCRM(event);
        }

        if ("SESSION_END".equals(event.getType())) {
            state.setStatus("COMPLETED");
        }
    }
}

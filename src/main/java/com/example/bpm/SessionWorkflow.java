package com.example.bpm;

import com.example.bpm.model.HubEvent;
import com.example.bpm.model.SessionState;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface SessionWorkflow {
    @WorkflowMethod
    SessionState run(HubEvent initialEvent);

    @SignalMethod
    void hubEventSignal(HubEvent event);
}

package com.example.bpm;

import com.example.bpm.model.HubEvent;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface SessionActivities {
    @ActivityMethod
    void logEvent(HubEvent event);

    @ActivityMethod
    void notifyCRM(HubEvent event);
}

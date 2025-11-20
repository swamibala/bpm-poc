package com.example.bpm;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class BpmWorker {
    public static void main(String[] args) {
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);
        WorkerFactory factory = WorkerFactory.newInstance(client);

        Worker worker = factory.newWorker("hub-events-queue");
        worker.registerWorkflowImplementationTypes(SessionWorkflowImpl.class);
        worker.registerActivitiesImplementations(new SessionActivitiesImpl());

        factory.start();
        System.out.println("Worker started. Listening on hub-events-queue");
    }
}

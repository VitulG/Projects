package com.scaler.paymentservice.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReconcileScheduler {

    @Scheduled(cron = "0 0 * * * *")
    public void reconcile() {
        // reconciliation process goes here
    }
}

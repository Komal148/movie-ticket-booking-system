package com.example.movieticketbookingsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private int holdDurationMinutes = 15;
    private double refundDefaultPercent = 80.0;

    public int getHoldDurationMinutes() {
        return holdDurationMinutes;
    }

    public void setHoldDurationMinutes(int holdDurationMinutes) {
        this.holdDurationMinutes = holdDurationMinutes;
    }

    public double getRefundDefaultPercent() {
        return refundDefaultPercent;
    }

    public void setRefundDefaultPercent(double refundDefaultPercent) {
        this.refundDefaultPercent = refundDefaultPercent;
    }
}

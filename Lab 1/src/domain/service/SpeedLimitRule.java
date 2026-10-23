package domain.service;

import domain.models.TelemetryPing;
import domain.models.Vehicle;

public final class SpeedLimitRule implements TrackingRule {
    private final double globalToleranceKmh;

    public SpeedLimitRule(double globalToleranceKmh) {
        if (globalToleranceKmh < 0) {
            throw new IllegalArgumentException("Tolerance cannot be negative");
        }
        this.globalToleranceKmh = globalToleranceKmh;
    }

    @Override
    public boolean isViolated(Vehicle vehicle, TelemetryPing ping) {
        return ping.speedKmh() > (vehicle.maxSpeedKmh() + globalToleranceKmh);
    }

    @Override
    public String getViolationMessage(Vehicle vehicle) {
        return "Speed limit exceeded (max allowed: %.1f km/h)".formatted(vehicle.maxSpeedKmh());
    }
}

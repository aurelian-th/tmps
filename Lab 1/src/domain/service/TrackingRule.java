package domain.service;

import domain.models.TelemetryPing;
import domain.models.Vehicle;

public interface TrackingRule {
    boolean isViolated(Vehicle vehicle, TelemetryPing ping);

    String getViolationMessage(Vehicle vehicle);
}

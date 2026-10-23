package domain.service;

import domain.models.TelemetryPing;

public interface AlertDispatcherPort {
    void dispatchAlert(TelemetryPing ping, String message);
}

package app;

public interface AlertDispatcherPort {
    void dispatchAlert(TelemetryPing ping, String message);
}
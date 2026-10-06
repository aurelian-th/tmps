package app;

public interface TrackingRule {
    boolean isViolated(TelemetryPing ping);

    String getViolationMessage();
}
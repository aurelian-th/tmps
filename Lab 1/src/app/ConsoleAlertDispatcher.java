package app;

public final class ConsoleAlertDispatcher implements AlertDispatcherPort {
    @Override
    public void dispatchAlert(TelemetryPing ping, String message) {
        System.out.printf("[alert] timestamp: %s | coordinates: (%.4f, %.4f) | speed: %.1f km/h | reason: %s%n",
                ping.timestamp(), ping.latitude(), ping.longitude(), ping.speedKmh(), message);
    }
}
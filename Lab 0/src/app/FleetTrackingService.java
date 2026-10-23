package app;

import java.util.List;
import java.util.Objects;

public final class FleetTrackingService {
    private final VehicleRepository vehicleRepository;
    private final AlertDispatcherPort alertDispatcher;
    private final List<TrackingRule> rules;

    public FleetTrackingService(
            VehicleRepository vehicleRepository,
            AlertDispatcherPort alertDispatcher,
            List<TrackingRule> rules) {
        this.vehicleRepository = Objects.requireNonNull(vehicleRepository);
        this.alertDispatcher = Objects.requireNonNull(alertDispatcher);
        this.rules = List.copyOf(rules);
    }

    public void processTelemetry(TelemetryPing ping) {
        var vehicle = vehicleRepository.findById(ping.vehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Unknown vehicle with ID: " + ping.vehicleId()));

        for (var rule : rules) {
            if (rule.isViolated(ping)) {
                var message = "[%s - %s] %s".formatted(
                        vehicle.type(),
                        vehicle.licensePlate(),
                        rule.getViolationMessage());
                alertDispatcher.dispatchAlert(ping, message);
            }
        }
    }
}
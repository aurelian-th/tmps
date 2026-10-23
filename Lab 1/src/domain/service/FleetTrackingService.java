package domain.service;

import domain.models.TelemetryPing;
import domain.repository.VehicleRepository;
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

    public boolean processTelemetry(TelemetryPing ping) {
        var vehicleOpt = vehicleRepository.findById(ping.vehicleId());
        if (vehicleOpt.isEmpty()) {
            throw new IllegalArgumentException("Unknown vehicle with ID: " + ping.vehicleId());
        }

        var vehicle = vehicleOpt.get();
        var hasViolation = false;

        for (var rule : rules) {
            if (rule.isViolated(vehicle, ping)) {
                hasViolation = true;
                var message = "[%s - %s] %s".formatted(
                        vehicle.type(),
                        vehicle.licensePlate(),
                        rule.getViolationMessage(vehicle));
                alertDispatcher.dispatchAlert(ping, message);
            }
        }
        return !hasViolation;
    }
}

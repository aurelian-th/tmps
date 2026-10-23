package domain.models;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record TelemetryPing(
        UUID vehicleId,
        double latitude,
        double longitude,
        double speedKmh,
        Instant timestamp) {
    public TelemetryPing {
        Objects.requireNonNull(vehicleId, "vehicleId must not be null");
        Objects.requireNonNull(timestamp, "timestamp must not be null");
        if (speedKmh < 0) {
            throw new IllegalArgumentException("Speed cannot be negative");
        }
    }
}

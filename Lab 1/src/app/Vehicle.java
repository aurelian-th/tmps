package app;

import java.util.Objects;
import java.util.UUID;

public record Vehicle(
        UUID id,
        String licensePlate,
        VehicleType type) {
    public Vehicle {
        Objects.requireNonNull(id, "Vehicle ID cannot be null");
        Objects.requireNonNull(licensePlate, "License plate cannot be null");
        Objects.requireNonNull(type, "Vehicle type cannot be null");
    }
}
package domain.models;

import java.util.Objects;
import java.util.UUID;

public final class Vehicle implements Prototype<Vehicle> {
    private final UUID id;
    private final String licensePlate;
    private final VehicleType type;
    private final double maxSpeedKmh;
    private final double payloadCapacityKg;
    private final double batteryCapacityKwh;

    public Vehicle(
            UUID id,
            String licensePlate,
            VehicleType type,
            double maxSpeedKmh,
            double payloadCapacityKg,
            double batteryCapacityKwh) {
        this.id = Objects.requireNonNull(id, "Vehicle ID cannot be null");
        this.licensePlate = Objects.requireNonNull(licensePlate, "License plate cannot be null");
        this.type = Objects.requireNonNull(type, "Vehicle type cannot be null");
        this.maxSpeedKmh = maxSpeedKmh;
        this.payloadCapacityKg = payloadCapacityKg;
        this.batteryCapacityKwh = batteryCapacityKwh;
    }

    public static VehicleBuilder builder() {
        return new VehicleBuilder();
    }

    @Override
    public Vehicle copy() {
        return new Vehicle(
                id,
                licensePlate,
                type,
                maxSpeedKmh,
                payloadCapacityKg,
                batteryCapacityKwh);
    }

    public Vehicle cloneWithNewPlate(UUID newId, String newPlate) {
        return new Vehicle(
                newId,
                newPlate,
                type,
                maxSpeedKmh,
                payloadCapacityKg,
                batteryCapacityKwh);
    }

    public UUID id() {
        return id;
    }

    public String licensePlate() {
        return licensePlate;
    }

    public VehicleType type() {
        return type;
    }

    public double maxSpeedKmh() {
        return maxSpeedKmh;
    }

    public double payloadCapacityKg() {
        return payloadCapacityKg;
    }

    public double batteryCapacityKwh() {
        return batteryCapacityKwh;
    }

    @Override
    public String toString() {
        return "%s [%s] - maxSpeed: %.0f km/h, payload: %.0f kg, battery: %.1f kWh (id: %s)".formatted(
                type, licensePlate, maxSpeedKmh, payloadCapacityKg, batteryCapacityKwh, id);
    }
}

package domain.models;

import java.util.Objects;
import java.util.UUID;

public final class VehicleBuilder {
    private UUID id = UUID.randomUUID();
    private String licensePlate;
    private VehicleType type = VehicleType.PASSENGER_CAR;
    private double maxSpeedKmh = 130.0;
    private double payloadCapacityKg = 400.0;
    private double batteryCapacityKwh = 0.0;

    public VehicleBuilder id(UUID id) {
        this.id = Objects.requireNonNull(id, "Vehicle ID cannot be null");
        return this;
    }

    public VehicleBuilder licensePlate(String licensePlate) {
        this.licensePlate = Objects.requireNonNull(licensePlate, "License plate cannot be null");
        return this;
    }

    public VehicleBuilder type(VehicleType type) {
        this.type = Objects.requireNonNull(type, "Vehicle type cannot be null");
        return this;
    }

    public VehicleBuilder maxSpeedKmh(double maxSpeedKmh) {
        if (maxSpeedKmh <= 0) {
            throw new IllegalArgumentException("Max speed must be strictly positive");
        }
        this.maxSpeedKmh = maxSpeedKmh;
        return this;
    }

    public VehicleBuilder payloadCapacityKg(double payloadCapacityKg) {
        if (payloadCapacityKg < 0) {
            throw new IllegalArgumentException("Payload capacity cannot be negative");
        }
        this.payloadCapacityKg = payloadCapacityKg;
        return this;
    }

    public VehicleBuilder batteryCapacityKwh(double batteryCapacityKwh) {
        if (batteryCapacityKwh < 0) {
            throw new IllegalArgumentException("Battery capacity cannot be negative");
        }
        this.batteryCapacityKwh = batteryCapacityKwh;
        return this;
    }

    public Vehicle build() {
        if (licensePlate == null || licensePlate.isBlank()) {
            throw new IllegalStateException("License plate must be configured before building Vehicle");
        }
        return new Vehicle(
                id,
                licensePlate.strip(),
                type,
                maxSpeedKmh,
                payloadCapacityKg,
                batteryCapacityKwh);
    }
}

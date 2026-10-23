package domain.factory;

import domain.models.Vehicle;
import domain.models.VehicleType;

public final class ScooterFactory implements VehicleFactory {
    @Override
    public Vehicle createVehicle(String licensePlate) {
        return Vehicle.builder()
                .licensePlate(licensePlate)
                .type(VehicleType.SCOOTER)
                .maxSpeedKmh(25.0)
                .payloadCapacityKg(15.0)
                .batteryCapacityKwh(2.8)
                .build();
    }
}

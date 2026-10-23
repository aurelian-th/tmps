package domain.factory;

import domain.models.Vehicle;
import domain.models.VehicleType;

public final class CargoVanFactory implements VehicleFactory {
    @Override
    public Vehicle createVehicle(String licensePlate) {
        return Vehicle.builder()
                .licensePlate(licensePlate)
                .type(VehicleType.CARGO_VAN)
                .maxSpeedKmh(90.0)
                .payloadCapacityKg(1800.0)
                .batteryCapacityKwh(0.0)
                .build();
    }
}

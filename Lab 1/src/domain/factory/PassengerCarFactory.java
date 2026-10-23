package domain.factory;

import domain.models.Vehicle;
import domain.models.VehicleType;

public final class PassengerCarFactory implements VehicleFactory {
    @Override
    public Vehicle createVehicle(String licensePlate) {
        return Vehicle.builder()
                .licensePlate(licensePlate)
                .type(VehicleType.PASSENGER_CAR)
                .maxSpeedKmh(130.0)
                .payloadCapacityKg(450.0)
                .batteryCapacityKwh(0.0)
                .build();
    }
}

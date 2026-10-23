package domain.factory;

import domain.models.Vehicle;

public interface VehicleFactory {
    Vehicle createVehicle(String licensePlate);
}

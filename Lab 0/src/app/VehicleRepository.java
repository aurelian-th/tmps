package app;

import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository {
    void registerVehicle(Vehicle vehicle);

    Optional<Vehicle> findById(UUID id);
}
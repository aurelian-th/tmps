package domain.repository;

import domain.models.Vehicle;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository {
    void registerVehicle(Vehicle vehicle);

    Optional<Vehicle> findById(UUID id);

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    List<Vehicle> findAll();

    boolean removeById(UUID id);
}

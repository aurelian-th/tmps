package app;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class InMemoryVehicleRepository implements VehicleRepository {
    private final Map<UUID, Vehicle> storage = new HashMap<>();

    @Override
    public void registerVehicle(Vehicle vehicle) {
        storage.put(vehicle.id(), vehicle);
    }

    @Override
    public Optional<Vehicle> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }
}
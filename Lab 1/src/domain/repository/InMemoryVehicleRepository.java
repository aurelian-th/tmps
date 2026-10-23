package domain.repository;

import domain.models.Vehicle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryVehicleRepository implements VehicleRepository {
    private final Map<UUID, Vehicle> storage = new ConcurrentHashMap<>();

    @Override
    public void registerVehicle(Vehicle vehicle) {
        storage.put(vehicle.id(), vehicle);
    }

    @Override
    public Optional<Vehicle> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Vehicle> findByLicensePlate(String licensePlate) {
        return storage.values().stream()
                .filter(v -> v.licensePlate().equalsIgnoreCase(licensePlate.strip()))
                .findFirst();
    }

    @Override
    public List<Vehicle> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean removeById(UUID id) {
        return storage.remove(id) != null;
    }
}

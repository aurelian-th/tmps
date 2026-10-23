package client;

import domain.models.TelemetryPing;
import domain.models.Vehicle;
import domain.models.VehicleType;
import domain.repository.InMemoryVehicleRepository;
import domain.service.FleetConfigManager;
import domain.service.FleetTrackingService;
import domain.service.SpeedLimitRule;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class FleetCreationalTest {
    public static void main(String[] args) {
        testSingleton();
        testBuilder();
        testPrototype();
        testTelemetryProcessing();
        System.out.println("All creational pattern unit tests passed successfully.");
    }

    private static void testSingleton() {
        var instance1 = FleetConfigManager.getInstance();
        var instance2 = FleetConfigManager.getInstance();
        if (instance1 != instance2) {
            throw new AssertionError("Singleton pattern failed: different instances returned");
        }
        if (instance1.speedToleranceKmh() < 0) {
            throw new AssertionError("Invalid speed tolerance in configuration");
        }
    }

    private static void testBuilder() {
        var vehicle = Vehicle.builder()
                .licensePlate("MD-EV-100")
                .type(VehicleType.PASSENGER_CAR)
                .maxSpeedKmh(160.0)
                .payloadCapacityKg(500.0)
                .batteryCapacityKwh(82.0)
                .build();

        if (!"MD-EV-100".equals(vehicle.licensePlate())) {
            throw new AssertionError("Builder did not set license plate properly");
        }
        if (vehicle.batteryCapacityKwh() != 82.0) {
            throw new AssertionError("Builder did not set battery capacity properly");
        }
        if (vehicle.maxSpeedKmh() != 160.0) {
            throw new AssertionError("Builder did not set max speed properly");
        }
    }

    private static void testPrototype() {
        var base = Vehicle.builder()
                .licensePlate("MD-TMPL-01")
                .type(VehicleType.CARGO_VAN)
                .maxSpeedKmh(90.0)
                .payloadCapacityKg(1800.0)
                .batteryCapacityKwh(0.0)
                .build();

        var cloneId = UUID.randomUUID();
        var cloned = base.cloneWithNewPlate(cloneId, "MD-TMPL-02");

        if (cloned.id().equals(base.id())) {
            throw new AssertionError("Prototype clone should have unique ID");
        }
        if (!"MD-TMPL-02".equals(cloned.licensePlate())) {
            throw new AssertionError("Prototype clone did not update license plate");
        }
        if (cloned.payloadCapacityKg() != base.payloadCapacityKg() || cloned.maxSpeedKmh() != base.maxSpeedKmh()) {
            throw new AssertionError("Prototype clone failed to copy specifications accurately");
        }
    }

    private static void testTelemetryProcessing() {
        var repository = new InMemoryVehicleRepository();
        var speedRule = new SpeedLimitRule(0.0);
        var trackingService = new FleetTrackingService(repository, (ping, msg) -> {}, List.of(speedRule));

        var scooter = Vehicle.builder()
                .licensePlate("MD-SC-10")
                .type(VehicleType.SCOOTER)
                .maxSpeedKmh(25.0)
                .payloadCapacityKg(15.0)
                .batteryCapacityKwh(2.8)
                .build();
        repository.registerVehicle(scooter);

        var okPing = new TelemetryPing(scooter.id(), 47.0, 28.0, 20.0, Instant.now());
        if (!trackingService.processTelemetry(okPing)) {
            throw new AssertionError("Compliant speed was incorrectly flagged as violation");
        }

        var overPing = new TelemetryPing(scooter.id(), 47.0, 28.0, 35.0, Instant.now());
        if (trackingService.processTelemetry(overPing)) {
            throw new AssertionError("Overspeed ping was not flagged as violation");
        }
    }
}

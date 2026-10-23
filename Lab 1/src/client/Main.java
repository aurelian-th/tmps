package client;

import domain.models.TelemetryPing;
import domain.models.Vehicle;
import domain.models.VehicleType;
import domain.repository.InMemoryVehicleRepository;
import domain.service.ConsoleAlertDispatcher;
import domain.service.FleetConfigManager;
import domain.service.FleetTrackingService;
import domain.service.SpeedLimitRule;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        var repository = new InMemoryVehicleRepository();
        var dispatcher = new ConsoleAlertDispatcher();
        var config = FleetConfigManager.getInstance();

        var speedRule = new SpeedLimitRule(config.speedToleranceKmh());
        var service = new FleetTrackingService(repository, dispatcher, List.of(speedRule));

        var isDemo = args.length > 0 && (args[0].equals("--demo") || args[0].equals("-d"));

        if (isDemo) {
            runAutomatedDemonstration(repository, service, config);
            return;
        }

        // Pre-populate with typical fleet vehicles using Builder
        populateInitialFleet(repository);

        var scanner = new Scanner(System.in);
        var tui = new FleetConsoleTui(repository, service);
        tui.run(scanner);
    }

    private static void populateInitialFleet(InMemoryVehicleRepository repository) {
        var scooter = Vehicle.builder()
                .licensePlate("MD-SC-001")
                .type(VehicleType.SCOOTER)
                .maxSpeedKmh(25.0)
                .payloadCapacityKg(15.0)
                .batteryCapacityKwh(2.8)
                .build();

        var car = Vehicle.builder()
                .licensePlate("MD-PC-550")
                .type(VehicleType.PASSENGER_CAR)
                .maxSpeedKmh(130.0)
                .payloadCapacityKg(450.0)
                .batteryCapacityKwh(0.0)
                .build();

        var van = Vehicle.builder()
                .licensePlate("MD-TR-102")
                .type(VehicleType.CARGO_VAN)
                .maxSpeedKmh(90.0)
                .payloadCapacityKg(1800.0)
                .batteryCapacityKwh(0.0)
                .build();

        repository.registerVehicle(scooter);
        repository.registerVehicle(car);
        repository.registerVehicle(van);
    }

    private static void runAutomatedDemonstration(
            InMemoryVehicleRepository repository,
            FleetTrackingService service,
            FleetConfigManager config) {

        System.out.println("ManaCar Creational Design Patterns Demonstration");

        // 1. Singleton Pattern Demonstration
        System.out.printf("1. Singleton Pattern configuration: region=%s, speedTolerance=%.1f km/h, version=%s%n",
                config.fleetRegion(), config.speedToleranceKmh(), config.systemVersion());

        // 2. Builder Pattern Demonstration
        System.out.println("%n2. Builder Pattern instantiation:".formatted());
        var deliveryVan = Vehicle.builder()
                .licensePlate("MD-TR-900")
                .type(VehicleType.CARGO_VAN)
                .maxSpeedKmh(90.0)
                .payloadCapacityKg(1800.0)
                .batteryCapacityKwh(0.0)
                .build();

        var customEv = Vehicle.builder()
                .licensePlate("MD-EV-777")
                .type(VehicleType.PASSENGER_CAR)
                .maxSpeedKmh(150.0)
                .payloadCapacityKg(420.0)
                .batteryCapacityKwh(77.5)
                .build();

        repository.registerVehicle(deliveryVan);
        repository.registerVehicle(customEv);
        System.out.println(" - Constructed via VehicleBuilder: " + deliveryVan);
        System.out.println(" - Constructed via VehicleBuilder: " + customEv);

        // 3. Prototype Pattern Demonstration
        System.out.println("%n3. Prototype Pattern instantiation:".formatted());
        var clonedVan = deliveryVan.cloneWithNewPlate(UUID.randomUUID(), "MD-TR-901");
        repository.registerVehicle(clonedVan);
        System.out.println(" - Original template vehicle: " + deliveryVan);
        System.out.println(" - Cloned unit via Prototype: " + clonedVan);

        // 4. Telemetry Processing & Rule Evaluation
        System.out.println("%n4. Telemetry Ingestion & Compliance Evaluation:".formatted());
        var now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        var normalPing = new TelemetryPing(customEv.id(), 47.0245, 28.8322, 120.0, now);
        System.out.println("Ingesting normal telemetry for EV (speed: 120.0 km/h, max: 150.0 km/h):");
        var ok = service.processTelemetry(normalPing);
        if (ok) {
            System.out.println("Status: telemetry ping recorded within normal parameters.");
        }

        var violationPing = new TelemetryPing(deliveryVan.id(), 47.0290, 28.8390, 98.5, now);
        System.out.println("Ingesting excessive speed telemetry for cargo van (speed: 98.5 km/h, max: 90.0 km/h):");
        service.processTelemetry(violationPing);
    }
}

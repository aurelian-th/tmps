package client;

import domain.factory.CargoVanFactory;
import domain.factory.PassengerCarFactory;
import domain.factory.ScooterFactory;
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

        // Pre-populate with typical fleet vehicles for immediate testing
        populateInitialFleet(repository);

        var scanner = new Scanner(System.in);
        var tui = new FleetConsoleTui(repository, service);
        tui.run(scanner);
    }

    private static void populateInitialFleet(InMemoryVehicleRepository repository) {
        var scooterFactory = new ScooterFactory();
        var carFactory = new PassengerCarFactory();
        var vanFactory = new CargoVanFactory();

        repository.registerVehicle(scooterFactory.createVehicle("MD-SC-001"));
        repository.registerVehicle(carFactory.createVehicle("MD-PC-550"));
        repository.registerVehicle(vanFactory.createVehicle("MD-TR-102"));
    }

    private static void runAutomatedDemonstration(
            InMemoryVehicleRepository repository,
            FleetTrackingService service,
            FleetConfigManager config) {

        System.out.println("ManaCar Creational Design Patterns Demonstration");
        System.out.printf("Singleton config loaded: region=%s, speedTolerance=%.1f km/h%n%n",
                config.fleetRegion(), config.speedToleranceKmh());

        // 1. Factory Method Demonstration
        System.out.println("1. Factory Method instantiation:");
        var scooterFactory = new ScooterFactory();
        var vanFactory = new CargoVanFactory();

        var scooter = scooterFactory.createVehicle("MD-SC-101");
        var cargoVan = vanFactory.createVehicle("MD-TR-900");
        repository.registerVehicle(scooter);
        repository.registerVehicle(cargoVan);
        System.out.println(" - Created via ScooterFactory: " + scooter);
        System.out.println(" - Created via CargoVanFactory: " + cargoVan);

        // 2. Builder Pattern Demonstration
        System.out.println("%n2. Builder Pattern instantiation:".formatted());
        var customEv = Vehicle.builder()
                .licensePlate("MD-EV-777")
                .type(VehicleType.PASSENGER_CAR)
                .maxSpeedKmh(150.0)
                .payloadCapacityKg(420.0)
                .batteryCapacityKwh(77.5)
                .build();
        repository.registerVehicle(customEv);
        System.out.println(" - Created via VehicleBuilder: " + customEv);

        // 3. Prototype Pattern Demonstration
        System.out.println("%n3. Prototype Pattern instantiation:".formatted());
        var clonedVan = cargoVan.cloneWithNewPlate(UUID.randomUUID(), "MD-TR-901");
        repository.registerVehicle(clonedVan);
        System.out.println(" - Original template vehicle: " + cargoVan);
        System.out.println(" - Cloned unit via Prototype: " + clonedVan);

        // 4. Telemetry Processing & Rule Evaluation
        System.out.println("%n4. Telemetry Ingestion & Compliance Evaluation:".formatted());
        var now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        var normalPing = new TelemetryPing(scooter.id(), 47.0245, 28.8322, 22.0, now);
        System.out.println("Ingesting normal telemetry for scooter (speed: 22.0 km/h, max: 25.0 km/h):");
        var ok = service.processTelemetry(normalPing);
        if (ok) {
            System.out.println("Status: telemetry ping recorded within normal parameters.");
        }

        var violationPing = new TelemetryPing(cargoVan.id(), 47.0290, 28.8390, 98.5, now);
        System.out.println("Ingesting excessive speed telemetry for cargo van (speed: 98.5 km/h, max: 90.0 km/h):");
        service.processTelemetry(violationPing);
    }
}

package client;

import domain.models.TelemetryPing;
import domain.models.Vehicle;
import domain.models.VehicleBuilder;
import domain.models.VehicleType;
import domain.repository.VehicleRepository;
import domain.service.FleetConfigManager;
import domain.service.FleetTrackingService;

import java.time.Instant;
import java.util.Scanner;
import java.util.UUID;

public final class FleetConsoleTui {
    private final VehicleRepository repository;
    private final FleetTrackingService trackingService;

    public FleetConsoleTui(VehicleRepository repository, FleetTrackingService trackingService) {
        this.repository = repository;
        this.trackingService = trackingService;
    }

    public void run(Scanner scanner) {
        var running = true;
        while (running) {
            printMenu();
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            var choice = scanner.nextLine().strip();
            switch (choice) {
                case "1" -> handleBuilder(scanner);
                case "2" -> handlePrototype(scanner);
                case "3" -> handleListVehicles();
                case "4" -> handleTelemetryPing(scanner);
                case "5" -> handleRemoveVehicle(scanner);
                case "6" -> handleShowConfig();
                case "0", "exit", "quit" -> running = false;
                default -> System.out.println("Invalid command. Enter a number between 0 and 6.");
            }
            System.out.println();
        }
        System.out.println("Exiting ManaCar console.");
    }

    private void printMenu() {
        System.out.println("ManaCar Fleet Telemetry Management");
        System.out.println("1. Build custom vehicle (Builder)");
        System.out.println("2. Clone vehicle from template (Prototype)");
        System.out.println("3. List registered fleet vehicles");
        System.out.println("4. Ingest telemetry ping");
        System.out.println("5. Remove vehicle");
        System.out.println("6. Inspect system configuration (Singleton)");
        System.out.println("0. Exit");
    }

    private void handleBuilder(Scanner scanner) {
        System.out.print("Enter license plate: ");
        if (!scanner.hasNextLine()) return;
        var plate = scanner.nextLine().strip();
        if (plate.isBlank()) {
            System.out.println("License plate cannot be blank.");
            return;
        }

        if (repository.findByLicensePlate(plate).isPresent()) {
            System.out.printf("Vehicle with license plate '%s' is already registered.%n", plate);
            return;
        }

        System.out.println("Select vehicle type:");
        System.out.println(" 1. SCOOTER");
        System.out.println(" 2. PASSENGER_CAR");
        System.out.println(" 3. CARGO_VAN");
        System.out.print("Choice [1-3 or name]: ");
        if (!scanner.hasNextLine()) return;
        var typeInput = scanner.nextLine().strip();
        var type = parseVehicleType(typeInput);

        System.out.print("Enter max speed (km/h) [e.g. 110.0]: ");
        var speed = parseDoubleOrDefault(scanner, 110.0);
        if (speed <= 0) {
            System.out.println("Max speed must be strictly positive.");
            return;
        }

        System.out.print("Enter payload capacity (kg) [e.g. 350.0]: ");
        var payload = parseDoubleOrDefault(scanner, 350.0);
        if (payload < 0) {
            System.out.println("Payload capacity cannot be negative.");
            return;
        }

        System.out.print("Enter battery capacity (kWh) [e.g. 0.0]: ");
        var battery = parseDoubleOrDefault(scanner, 0.0);
        if (battery < 0) {
            System.out.println("Battery capacity cannot be negative.");
            return;
        }

        try {
            var vehicle = Vehicle.builder()
                    .licensePlate(plate)
                    .type(type)
                    .maxSpeedKmh(speed)
                    .payloadCapacityKg(payload)
                    .batteryCapacityKwh(battery)
                    .build();

            repository.registerVehicle(vehicle);
            System.out.println("Constructed via VehicleBuilder: " + vehicle);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Failed to build vehicle: " + e.getMessage());
        }
    }

    private VehicleType parseVehicleType(String input) {
        if (input == null || input.isBlank()) {
            System.out.println("Defaulting to PASSENGER_CAR.");
            return VehicleType.PASSENGER_CAR;
        }
        var normalized = input.strip().toUpperCase();
        return switch (normalized) {
            case "1", "SCOOTER" -> VehicleType.SCOOTER;
            case "2", "CAR", "PASSENGER_CAR", "PASSENGER CAR" -> VehicleType.PASSENGER_CAR;
            case "3", "VAN", "CARGO_VAN", "CARGO VAN" -> VehicleType.CARGO_VAN;
            default -> {
                System.out.println("Unrecognized vehicle type. Defaulting to PASSENGER_CAR.");
                yield VehicleType.PASSENGER_CAR;
            }
        };
    }

    private void handlePrototype(Scanner scanner) {
        var vehicles = repository.findAll();
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles registered to clone from.");
            return;
        }

        System.out.println("Available template vehicles:");
        for (var i = 0; i < vehicles.size(); i++) {
            System.out.printf(" %d. %s%n", i + 1, vehicles.get(i));
        }
        System.out.print("Select index to clone: ");
        if (!scanner.hasNextLine()) return;
        var indexStr = scanner.nextLine().strip();
        try {
            var index = Integer.parseInt(indexStr) - 1;
            if (index < 0 || index >= vehicles.size()) {
                System.out.println("Index out of range.");
                return;
            }
            var template = vehicles.get(index);
            System.out.print("Enter new license plate: ");
            if (!scanner.hasNextLine()) return;
            var newPlate = scanner.nextLine().strip();
            if (newPlate.isBlank()) {
                System.out.println("License plate cannot be blank.");
                return;
            }
            if (repository.findByLicensePlate(newPlate).isPresent()) {
                System.out.printf("Vehicle with license plate '%s' is already registered.%n", newPlate);
                return;
            }

            var clone = template.cloneWithNewPlate(UUID.randomUUID(), newPlate);
            repository.registerVehicle(clone);
            System.out.println("Cloned via Prototype: " + clone);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Failed to clone vehicle: " + e.getMessage());
        }
    }

    private void handleListVehicles() {
        var vehicles = repository.findAll();
        if (vehicles.isEmpty()) {
            System.out.println("Fleet is empty.");
            return;
        }
        System.out.printf("Registered fleet vehicles (%d units):%n", vehicles.size());
        for (var v : vehicles) {
            System.out.println(" - " + v);
        }
    }

    private void handleTelemetryPing(Scanner scanner) {
        System.out.print("Enter vehicle license plate: ");
        if (!scanner.hasNextLine()) return;
        var plate = scanner.nextLine().strip();
        if (plate.isBlank()) {
            System.out.println("License plate cannot be blank.");
            return;
        }

        var vehicleOpt = repository.findByLicensePlate(plate);
        if (vehicleOpt.isEmpty()) {
            System.out.println("No vehicle found with license plate: " + plate);
            return;
        }

        var vehicle = vehicleOpt.get();
        System.out.print("Enter current speed (km/h): ");
        var speed = parseDoubleOrDefault(scanner, 50.0);
        if (speed < 0) {
            System.out.println("Speed cannot be negative.");
            return;
        }

        try {
            var ping = new TelemetryPing(vehicle.id(), 47.0245, 28.8322, speed, Instant.now());
            var compliant = trackingService.processTelemetry(ping);
            if (compliant) {
                System.out.printf("Telemetry compliant: %.1f km/h within allowed limit (%.1f km/h).%n",
                        speed, vehicle.maxSpeedKmh());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to process telemetry: " + e.getMessage());
        }
    }

    private void handleRemoveVehicle(Scanner scanner) {
        System.out.print("Enter license plate to remove: ");
        if (!scanner.hasNextLine()) return;
        var plate = scanner.nextLine().strip();
        if (plate.isBlank()) {
            System.out.println("License plate cannot be blank.");
            return;
        }

        var vehicleOpt = repository.findByLicensePlate(plate);
        if (vehicleOpt.isEmpty()) {
            System.out.println("No vehicle found with license plate: " + plate);
            return;
        }

        var removed = repository.removeById(vehicleOpt.get().id());
        if (removed) {
            System.out.println("Vehicle removed: " + plate);
        } else {
            System.out.println("Failed to remove vehicle.");
        }
    }

    private void handleShowConfig() {
        var config = FleetConfigManager.getInstance();
        System.out.println("Singleton Fleet Configuration:");
        System.out.println(" - System version: " + config.systemVersion());
        System.out.println(" - Fleet region: " + config.fleetRegion());
        System.out.println(" - Ingestion interval: " + config.telemetryPingIntervalSec() + " s");
        System.out.println(" - Global speed tolerance: " + config.speedToleranceKmh() + " km/h");
    }

    private double parseDoubleOrDefault(Scanner scanner, double defaultValue) {
        if (!scanner.hasNextLine()) return defaultValue;
        var input = scanner.nextLine().strip();
        if (input.isBlank()) return defaultValue;
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric input. Using default: " + defaultValue);
            return defaultValue;
        }
    }
}

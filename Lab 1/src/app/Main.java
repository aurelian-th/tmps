package app;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        var repository = new InMemoryVehicleRepository();
        var dispatcher = new ConsoleAlertDispatcher();
        var speedLimitRule = new SpeedLimitRule(80.0);

        var service = new FleetTrackingService(repository, dispatcher, List.of(speedLimitRule));

        var vehicleId = UUID.randomUUID();
        var deliveryVan = new Vehicle(vehicleId, "MD-TR-102", VehicleType.CARGO_VAN);
        repository.registerVehicle(deliveryVan);

        System.out.println("ManaCar fleet telemetry tracking");

        var ping1 = new TelemetryPing(vehicleId, 47.0245, 28.8322, 65.0, Instant.now().truncatedTo(ChronoUnit.SECONDS));
        service.processTelemetry(ping1);
        System.out.println("Status: telemetry ping recorded within normal parameters.");

        var ping2 = new TelemetryPing(vehicleId, 47.0290, 28.8390, 96.2, Instant.now().truncatedTo(ChronoUnit.SECONDS));
        service.processTelemetry(ping2);
    }
}
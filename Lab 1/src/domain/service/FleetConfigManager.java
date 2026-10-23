package domain.service;

public final class FleetConfigManager {
    private final String fleetRegion;
    private final int telemetryPingIntervalSec;
    private final double speedToleranceKmh;
    private final String systemVersion;

    private FleetConfigManager() {
        this.fleetRegion = "EU-MD-CHISINAU";
        this.telemetryPingIntervalSec = 5;
        this.speedToleranceKmh = 3.0;
        this.systemVersion = "2.1.0-creational";
    }

    private static final class Holder {
        private static final FleetConfigManager INSTANCE = new FleetConfigManager();
    }

    public static FleetConfigManager getInstance() {
        return Holder.INSTANCE;
    }

    public String fleetRegion() {
        return fleetRegion;
    }

    public int telemetryPingIntervalSec() {
        return telemetryPingIntervalSec;
    }

    public double speedToleranceKmh() {
        return speedToleranceKmh;
    }

    public String systemVersion() {
        return systemVersion;
    }
}

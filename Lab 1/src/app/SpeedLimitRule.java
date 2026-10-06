package app;

public final class SpeedLimitRule implements TrackingRule {
    private final double maxSpeedKmh;

    public SpeedLimitRule(double maxSpeedKmh) {
        if (maxSpeedKmh <= 0) {
            throw new IllegalArgumentException("Speed limit must be strictly positive");
        }
        this.maxSpeedKmh = maxSpeedKmh;
    }

    @Override
    public boolean isViolated(TelemetryPing ping) {
        return ping.speedKmh() > maxSpeedKmh;
    }

    @Override
    public String getViolationMessage() {
        return "Speed limit exceeded (" + maxSpeedKmh + " km/h allowed)";
    }
}
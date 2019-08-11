package ev3dev.sensors.slamtec.model;

/**
 * Scan distances store information about:
 *
 * - angle measured - distance from the object - quality of the measure - start
 * flag
 *
 */
public class ScanDistance {

    private final int angle;
    private final float distance;
    private final int quality;
    private final boolean start;

    public ScanDistance(int angle, float distance, int quality, boolean start) {
        this.angle = angle;
        this.distance = distance;
        this.quality = quality;
        this.start = start;
    }

    public boolean isStarted() {
        return start;
    }

    public int getQuality() {
        return quality;
    }

    public float getDistance() {
        return distance;
    }

    public int getAngle() {
        return angle;
    }
}

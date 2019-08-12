package ev3dev.sensors.slamtec;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import ev3dev.sensors.slamtec.RPLidarDeviceInfo;
import ev3dev.sensors.slamtec.RPLidarHealth;
import ev3dev.sensors.slamtec.RPLidarListener;
import ev3dev.sensors.slamtec.RPLidarLowLevelDriver;
import ev3dev.sensors.slamtec.RPLidarMeasurement;

/**
 * High-level class to control RPLIDAR A1 devices (e.g. A1M8).
 * 
 * @author Declan Freeman-Gleason
 */
public class RPLidarA1 {
    /** Milliseconds */
    private final int kTimeout = 700;

    private final RPLidarLowLevelDriver mDriver;

    /**
     * This is the pointcloud currently being built. This will be empty if this
     * class is instantiated in direct-measurement mode.
     */
    private List<RPLidarMeasurement> mCurrentPointcloud = new ArrayList<>();
    private final AtomicBoolean mIsClosing = new AtomicBoolean(false);
    private RPLidarHealth mLatestHealthReport = new RPLidarHealth();
    private RPLidarDeviceInfo mLatestDeviceInfo = new RPLidarDeviceInfo();

    /**
     * This is the PWM duty cycle of the motor. This number controls speed but is
     * not tied to any actual units. This number should be on the range of [0,
     * 1023].
     */
    private int mMotorPWM = 660;

    public RPLidarA1(String usbPortPath, PointcloudConsumer pointcloudConsumer) {
        mDriver = setupDriver(usbPortPath, (RPLidarMeasurement measurement) -> {
            if (measurement.start && mCurrentPointcloud.size() > 0) {
                pointcloudConsumer.accept(mCurrentPointcloud);
                mCurrentPointcloud = new ArrayList<>();
            }
            mCurrentPointcloud.add(measurement);
        });
    }

    /**
     * Instantiates the device in direct measurement mode. The consumer will be
     * called with {@link RPLidarMeasurement}s as they are recieved from the device.
     * 
     * If you want to get a pointcloud every full scan you should use the other
     * constructor.
     */
    public RPLidarA1(String usbPortPath, Consumer<RPLidarMeasurement> measurementConsumer) {
        mDriver = setupDriver(usbPortPath, measurementConsumer);
    }

    // We would just put this into the above constructor, but we need to avoid
    // Java's constraints on accessing fields from constructor calls (i.e. we would
    // try to call "this(usbPortPath, ...)" in the point cloud consumer constructor.
    // In doing that we need to pass a lambda which accesses member variables. You
    // cannot access member variables in a call to this()).
    private RPLidarLowLevelDriver setupDriver(String usbPortPath, Consumer<RPLidarMeasurement> measurementConsumer) {
        try {
            return new RPLidarLowLevelDriver(usbPortPath, new RPLidarListener() {

                @Override
                public void handleMeasurement(RPLidarMeasurement measurement) {
                    if (mIsClosing.get()) {
                        return;
                    }

                    measurementConsumer.accept(measurement);
                }

                @Override
                public void handleDeviceInfo(RPLidarDeviceInfo info) {
                    mLatestDeviceInfo = info;
                }

                @Override
                public void handleDeviceHealth(RPLidarHealth health) {
                    mLatestHealthReport = health;
                }
            });
        } catch (Exception e) {
            throw new RPLidarException(e);
        }
    }

    public void start() {
        if (mIsClosing.get()) {
            return;
        }

        mDriver.setVerbose(false);

        mDriver.sendReset();
        // PWM duty cycle/start motor command is ignored by v1
        mDriver.sendStartMotor(mMotorPWM);
        mDriver.sendScan(kTimeout);
    }

    public void stop() {
        mIsClosing.set(true);

        mDriver.sendStopMotor();
        mDriver.shutdown();
    }

    /**
     * @return Motor PWM duty cycle on the range of [0, 1023].
     */
    public int getMotorPWM() {
        return mMotorPWM;
    }

    /**
     * This sets the PWM duty cycle of the motor. This operation is not supported on
     * all devices, and is not directly related to any physical angular velocity.
     * 
     * @param motorPWM Motor PWM duty cycle on the range of [0, 1023].
     */
    public void setMotorPWM(int motorPWM) {
        if (motorPWM < 0 || motorPWM > 1023) {
            throw new RPLidarException("motorPWM must be on the range of [0, 1023]");
        }

        mMotorPWM = motorPWM;
        mDriver.sendStartMotor(mMotorPWM);
    }

    /**
     * @return Device info, or an empty device info object if no device info has
     *         been recieved yet.
     */
    public RPLidarDeviceInfo getDeviceInfo() {
        return mLatestDeviceInfo;
    }

    /**
     * @return Latest health report, or an empty health object if no report has been
     *         recieved yet.
     */
    public RPLidarHealth getDeviceHealth() {
        return mLatestHealthReport;
    }

    public static class RPLidarException extends RuntimeException {
        public RPLidarException(Exception e) {
            super(e);
        }

        public RPLidarException(String message) {
            super(message);
        }
    }

    // We have to alias Consumer<List<RPLidarMeasurement>> to deal with type
    // erasure.
    public static interface PointcloudConsumer extends Consumer<List<RPLidarMeasurement>> {
    }
}
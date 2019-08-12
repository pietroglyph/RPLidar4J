package examples;

import ev3dev.sensors.slamtec.RPLidarA1;
import ev3dev.sensors.slamtec.RPLidarDevice;
import ev3dev.sensors.slamtec.RPLidarMeasurement;

public class DirectMeasurementDemo {

    public static void main(String[] args) {
        final String usbPort = "/dev/ttyUSB0";
        final RPLidarDevice lidar = new RPLidarA1(usbPort, (RPLidarMeasurement m) -> {
            System.out.println(m);
        });
        lidar.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lidar.stop();
        System.out.println("End demo");
        System.exit(0);
    }
}

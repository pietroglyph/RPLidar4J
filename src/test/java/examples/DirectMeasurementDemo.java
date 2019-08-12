package examples;

import ev3dev.sensors.slamtec.RPLidarA1;
import ev3dev.sensors.slamtec.RPLidarMeasurement;

public class DirectMeasurementDemo {

    public static void main(String[] args) {

        System.out.println("Testing RPLidarNew");
        final String USBPort = "/dev/ttyUSB0";
        final RPLidarA1 lidar = new RPLidarA1(USBPort, (RPLidarMeasurement m) -> {
            System.out.println(m);
        });
        lidar.start();

        try {
            // Thread.sleep(5000);
            while (true) {
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lidar.stop();
        System.out.println("End demo");
        System.exit(0);
    }
}

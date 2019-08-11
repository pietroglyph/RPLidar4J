package examples;

import ev3dev.sensors.slamtec.RPLidarA1;

public class Demo2 {

    public static void main(String[] args) throws Exception {

        System.out.println("Testing RPLidar on a EV3Dev with Java");
        final String USBPort = "/dev/ttyUSB0";
        final RPLidarA1 lidar = new RPLidarA1(USBPort);
        lidar.init();

        for (int x = 0; x <= 10; x++) {
            final long counter = lidar.scan().getDistances().stream().count();
            System.out.println("Iteration: " + x + ", Measures: " + counter);
        }

        lidar.close();
        System.out.println("End demo");
        System.exit(0);
    }
}

package ev3dev.sensors.slamtec;

import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;

public class RPLidarA1Tests {

	private final String kUSBPort = "/dev/ttyUSB0";

	private Boolean gotMeasurementUpdate = false;
	private Boolean gotPointcloud = false;

	@Test
	public void getDistanceStreamTest() throws Exception {
		final RPLidarDevice lidar = new RPLidarA1(kUSBPort, (RPLidarMeasurement m) -> {
			if (m != null && !m.isInvalid()) {
				synchronized (gotMeasurementUpdate) {
					gotMeasurementUpdate.notify();
					gotMeasurementUpdate = true;
				}
			}
		});
		lidar.start();

		synchronized (gotMeasurementUpdate) {
			gotMeasurementUpdate.wait(1000);
			assertTrue(gotMeasurementUpdate);
		}

		lidar.stop();
	}

	@Test
	public void return360DistanceTest() throws Exception {
		final RPLidarDevice lidar = new RPLidarA1(kUSBPort, (List<RPLidarMeasurement> pointcloud) -> {
			assertTrue("Pointcloud is empty", pointcloud.size() > 0);
			synchronized (gotPointcloud) {
				gotPointcloud.notify();
				gotPointcloud = true;
			}
		});
		lidar.start();

		synchronized (gotPointcloud) {
			gotPointcloud.wait(1000);
			assertTrue(gotPointcloud);
		}

		lidar.stop();
	}
}
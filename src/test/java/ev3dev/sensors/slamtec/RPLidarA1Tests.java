package ev3dev.sensors.slamtec;

import ev3dev.sensors.slamtec.model.Scan;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class RPLidarA1Tests {

	@Test
	@Ignore("Stream usage")
	public void getDistanceStreamTest() throws Exception {

		final String USBPort = "/dev/ttyUSB0";
		final RPLidarA1 lidar = new RPLidarA1(USBPort);
		lidar.init();
		lidar.scan().getDistances().stream().forEach(System.out::println);
		lidar.close();
	}

	@Test
	public void return360DistanceTest() throws Exception {

		final String USBPort = "/dev/ttyUSB0";
		final RPLidarA1 lidar = new RPLidarA1(USBPort);
		lidar.init();
		final Scan scan = lidar.scan();
		lidar.close();

		assertThat(scan, is(notNullValue()));
		assertThat(scan.getDistances().size(), is(RPLidarProvider.SCAN_DEGREES));
	}
}
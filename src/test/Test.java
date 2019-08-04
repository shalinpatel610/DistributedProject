package test;

import java.util.stream.IntStream;

import utils.Config;
import utils.Constants;
import utils.UDPUtilities;

public class Test {

	public static void main(String[] args) {
		// testHardWareFailure();
		testSoftwareFailure();
	}

	/**
	 * 
	 */
	private static void testSoftwareFailure() {
		// inform RM's
		// TODO change rangeClosed
		IntStream.rangeClosed(3, 3).forEach(i -> {
			UDPUtilities.udpCommunication(Config.getStringConfig("RM" + i + "_IP"),
					Config.getConfig("RM" + i + "_PORT"), null, Constants.OP_SOFTWARE_CRASH, 0);
		});

		byte[] reply = UDPUtilities.udpCommunication(Config.getStringConfig("INSTANCE" + 3 + "_IP"),
				Config.getConfig("INSTANCE" + 3 + "_MTL_PORT"), null, Constants.OP_ISALIVE, 1000);
		System.out.println(" is replica1 instantiated : " + new String(reply));

	}

	/**
	 * 
	 */
	// test hardware crash
	private static void testHardWareFailure() {
		// inform RM's
		// TODO change rangeClosed
		IntStream.rangeClosed(3, 3).forEach(i -> {
			UDPUtilities.udpCommunication(Config.getStringConfig("RM" + i + "_IP"),
					Config.getConfig("RM" + i + "_PORT"), null, Constants.OP_HARDWARE_CRASH, 0);
		});

		byte[] reply = UDPUtilities.udpCommunication(Config.getStringConfig("INSTANCE" + 3 + "_IP"),
				Config.getConfig("INSTANCE" + 3 + "_MTL_PORT"), null, Constants.OP_ISALIVE, 1000);
		System.out.println(" is replica1 instantiated : " + new String(reply));

	}

}

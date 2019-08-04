package server.instance4;

import java.io.File;
import java.io.IOException;

import server.instance4.logging.CustomLogger;
import utils.Constants;
import server.instance4.service.DEMS;

public class Instance4Server {

	public static void main(String[] args) {

		try {
			
			setupLogging("MTLServer");			
			setupLogging("TORServer");
			setupLogging("OTWServer");
			
			new Thread(() -> { (new DEMS("MTL")).udpServer(); }).start();
			new Thread(() -> { (new DEMS("TOR")).udpServer(); }).start();
			new Thread(() -> { (new DEMS("OTW")).udpServer(); }).start();

			System.out.println("Insance 4 Server initated");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void setupLogging(String serverName) throws IOException {

		File files = new File(String.format(Constants.SERVER_LOG_DIRECTORY,"Instance4"));
		if (!files.exists())
			files.mkdirs();

		files = new File(String.format(Constants.SERVER_LOG_DIRECTORY,"Instance4") + serverName+".log");
		if (!files.exists())
			files.createNewFile();

		CustomLogger.setUpLogger(files.getAbsolutePath());
	}

}

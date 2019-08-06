package server.instance1;

import server.instance1.remoteObject.EnrollmentImpl;
import server.instance1.remoteObject.EnrollmentInterface;
import server.instance1.util.City;
import utils.Config;

import java.io.IOException;

public class Instance1Server {

	private static EnrollmentInterface mtlServer;
	private static EnrollmentInterface torServer;
	private static EnrollmentInterface otwServer;

	
	public static void main(String[] args) throws IOException {
		getInstance("MTL");
		getInstance("TOR");
		getInstance("OTW");
		
		System.out.println("Instance 2 Server initiated");
	}
	
	public static EnrollmentInterface getInstance(String serverName) throws IOException {

		if (serverName.equalsIgnoreCase("MTL")) {

			if (mtlServer == null) {
				mtlServer = new EnrollmentImpl(City.MTL.toString(),"MTL_Server.log");
				startUDPServer(mtlServer, Config.getConfig("INSTANCE1_MTL_PORT"));
			}
			return mtlServer;

		} else if (serverName.equalsIgnoreCase("TOR")) {

			if (torServer == null) {
				torServer = new EnrollmentImpl(City.TOR.toString(),"TOR_Server.log");
				startUDPServer(torServer,Config.getConfig("INSTANCE1_TOR_PORT"));
			}

			return torServer;

		} else if (serverName.equalsIgnoreCase("OTW")) {

			if (otwServer == null) {
				otwServer = new EnrollmentImpl(City.OTW.toString(),"OTW_Server.log");
				startUDPServer(otwServer,Config.getConfig("INSTANCE1_OTW_PORT"));
			}

			return otwServer;
		}

		return null;
	}

	private static void startUDPServer(EnrollmentInterface instance, int portNo) {
		new Thread(() -> {
			((EnrollmentImpl) instance).UDPServer(portNo);
		}).start();
	}

}

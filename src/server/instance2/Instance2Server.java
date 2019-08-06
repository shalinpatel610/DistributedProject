package server.instance2;

import java.io.IOException;

import server.instance2.remoteObject.EnrollmentImpl;
import server.instance2.remoteObject.EnrollmentInterface;
import server.instance2.util.City;
import utils.Config;

public class Instance2Server {

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
				startUDPServer(mtlServer, Config.getConfig("INSTANCE2_MTL_PORT"));
			}
			return mtlServer;

		} else if (serverName.equalsIgnoreCase("TOR")) {

			if (torServer == null) {
				torServer = new EnrollmentImpl(City.TOR.toString(),"TOR_Server.log");
				startUDPServer(torServer,Config.getConfig("INSTANCE2_TOR_PORT"));
			}

			return torServer;

		} else if (serverName.equalsIgnoreCase("OTW")) {

			if (otwServer == null) {
				otwServer = new EnrollmentImpl(City.OTW.toString(),"OTW_Server.log");
				startUDPServer(otwServer,Config.getConfig("INSTANCE2_OTW_PORT"));
			}

			return otwServer;
		}

		return null;
	}

	private static void startUDPServer(EnrollmentInterface instance, int portNo) {
		// start the department's UDP server for inter-department communication
		// the UDP server is started on a new thread
		new Thread(() -> {
			((EnrollmentImpl) instance).UDPServer(portNo);
		}).start();
	}

}

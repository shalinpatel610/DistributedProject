package server.instance4.pojo;

import utils.Config;

public enum City {

	MTL(Config.getConfig("INSTANCE4_MTL_PORT")), TOR(Config.getConfig("INSTANCE4_TOR_PORT")),
	OTW(Config.getConfig("INSTANCE4_OTW_PORT"));

	int udpPort;

	private City(int udpPort) {
		this.udpPort = udpPort;
	}

	public int getUdpPort() {
		return udpPort;
	}

	public static boolean departmentExist(String city) {
		for (City c : City.values()) {
			if (c.toString().equals(city.toUpperCase()))
				return true;
		}
		return false;
	}
}

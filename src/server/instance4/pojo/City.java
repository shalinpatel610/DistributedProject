package server.instance4.pojo;

import utils.Config;

public enum City {

	MTL(Config.getConfig("INSTANCE4_MTL_PORT")), 
	TOR(Config.getConfig("INSTANCE4_TOR_PORT")), 
	OTW(Config.getConfig("INSTANCE4_OTW_PORT"));
	
	int udpPort;

	private City(int udpPort) {
		this.udpPort = udpPort;
	}

	public int getUdpPort() {
		return udpPort;
	}

	public static boolean cityExist(final String City) {
		for (City city : City.values()) {
			if (city.toString().equals(City.toUpperCase()))
				return true;
		}
		return false;
	}

}

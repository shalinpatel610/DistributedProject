package client.controller.data;

import utils.corbaInterface.IDEMS;

public class Cache {

	private static Cache instance = null;
	
	public IDEMS dems;
	public String id;
	public enum ClientType {
		CLIENT, MANAGER
	}
	
	public static Cache getInstance() {
		
		if(instance == null)
			instance = new Cache();
		
		return instance;
	}
	
	public String getProvince() {
		return id.substring(0, 3);
	}

	public ClientType getClientType() {
		
		if(id.substring(3, 4).equals("M"))
			return ClientType.MANAGER;
		else if(id.substring(3, 4).equals("C"))
			return ClientType.CLIENT;
		
		return null;
	}

	public boolean checkValidId() {
		
		String province = this.getProvince();
		
		if( !(province.equals("MTL") || province.equals("TOR") || province.equals("OTW"))){
			System.out.println("Invalid User id");
			return false;
			
		} else if(this.getClientType() == null) {
			System.out.println("Invalid User id");
			return false;
			
		} else if(id.length() != 8) {
			System.out.println("Invalid User id");
			return false;
		}
			
		return true;
	}
	
}
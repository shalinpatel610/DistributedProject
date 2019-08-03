package frontEnd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.AbstractMap.SimpleEntry;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;

import utils.corbaInterface.IDEMSPOA;

public class FrontEndEngine extends IDEMSPOA {
	
	private ORB orb;
	private String server;

	public FrontEndEngine(String server) {
		super();
		this.server = server;
	}
	
	public void setORB(ORB orb) {
		this.orb = orb;
	}
	
	public void shutdown() {
		orb.shutdown(false);
	}

	@Override
	public boolean addEvent(String managerId, String eventId, String eventType, int capacity) {
		
		StringJoiner joiner = new StringJoiner("&")
				.add(server)
				.add("addEvent")
				.add(managerId)
				.add(eventId)
				.add(eventType)
				.add(String.valueOf(capacity));
		
		String message = joiner.toString();		
		Object result = FrontEndUtitlies.sendUDPRequest(message);
				
		return (boolean) result;
	}

	@Override
	public boolean removeEvent(String managerId, String eventId, String eventType) {

		StringJoiner joiner = new StringJoiner("&")
				.add(server)
				.add("removeEvent")
				.add(managerId)
				.add(eventId)
				.add(eventType);
		
		String message = joiner.toString();		
		Object result = FrontEndUtitlies.sendUDPRequest(message);
		
		return (boolean) result;
	}

	@Override
	public Any listEventAvailability(String managerId, String eventType) {

		StringJoiner joiner = new StringJoiner("&")
				.add(server)
				.add("listEventAvailability")
				.add(managerId)
				.add(eventType);
		
		String message = joiner.toString();		
		HashMap<String, Integer> result = (HashMap<String, Integer>) FrontEndUtitlies.sendUDPRequest(message); 
		
		Any any = orb.create_any();
		any.insert_Value((Serializable) result);	
		return any;
	}

	@Override
	public Any bookEvent(String customerId, String eventId, String eventType) {
		
		StringJoiner joiner = new StringJoiner("&")
				.add(server)
				.add("bookEvent")
				.add(customerId)
				.add(eventId)
				.add(eventType);
		
		String message = joiner.toString();		
		SimpleEntry<Boolean, String> result = (SimpleEntry<Boolean, String>) FrontEndUtitlies.sendUDPRequest(message);
		
		Any any = orb.create_any();
		any.insert_Value((Serializable) result);	
		return any;
	}

	@Override
	public Any getBookingSchedule(String customerId) {

		StringJoiner joiner = new StringJoiner("&")
				.add(server)
				.add("getBookingSchedule")
				.add(customerId);
		
		String message = joiner.toString();		
		HashMap<String, ArrayList<String>> result = (HashMap<String, ArrayList<String>>) FrontEndUtitlies.sendUDPRequest(message);
		
		Any any = orb.create_any();
		any.insert_Value((Serializable) result);	
		return any;
	}

	@Override
	public boolean cancelEvent(String customerId, String eventId, String eventType) {
		
		StringJoiner joiner = new StringJoiner("&")
				.add(server)
				.add("cancelEvent")
				.add(customerId)
				.add(eventId)
				.add(eventType);
		
		String message = joiner.toString();		
		Object result = FrontEndUtitlies.sendUDPRequest(message);
		
		return (boolean) result;
	}

	@Override
	public Any swapEvent(String customerId, String neweventId, String oldeventId, String newEventType, String oldEventType) {

		StringJoiner joiner = new StringJoiner("&")
				.add(server)
				.add("swapEvent")
				.add(customerId)
				.add(neweventId)
				.add(oldeventId)
				.add(newEventType)
				.add(oldEventType);
		
		String message = joiner.toString();		
		SimpleEntry<Boolean, String> result = (SimpleEntry<Boolean, String>) FrontEndUtitlies.sendUDPRequest(message);
		
		Any any = orb.create_any();
		any.insert_Value((Serializable) result);	
		return any;
	}	
	
}

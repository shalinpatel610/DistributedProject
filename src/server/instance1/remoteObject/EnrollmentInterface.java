package server.instance1.remoteObject;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;

public interface EnrollmentInterface extends Remote {

	boolean addEvent(String managerId, String eventId, String eventType, int capacity) throws RemoteException;

	boolean removeEvent(String managerId, String eventId, String eventType) throws RemoteException;

	HashMap<String, Integer> listEventAvailability(String managerId, String eventType) throws RemoteException;


	SimpleEntry<Boolean, String> bookEvent(String customerId, String eventId, String eventType) throws RemoteException;

	HashMap<String, ArrayList<String>> getBookingSchedule(String customerId) throws RemoteException;

	boolean cancelEvent(String customerId, String eventId, String eventType) throws RemoteException;
	
	SimpleEntry<Boolean, String> swapEvent(String customerId, String neweventId, String oldeventId, String oldEventType, String newEventType) throws RemoteException;
	
	byte[] getInternalState();

	void setState(HashMap<String, HashMap<String, HashMap<String, Object>>> data);

}

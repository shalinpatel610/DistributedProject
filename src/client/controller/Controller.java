package client.controller;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.omg.CORBA.Any;

import client.controller.data.Cache;
import utils.Logger;

public class Controller {
	
	private static String readEventType() {
		
		System.out.print("Event Type: ");
		String eventType = new Scanner(System.in).nextLine().toUpperCase();
		
		if(!eventType.equals("SEMINAR") && !eventType.equals("CONFERENCES") && !eventType.equals("TRADE SHOWS")) {
			System.out.println("\nResult: Illegal Event Type \n");
			return null;
		}
		
		return eventType;
	}
	
	private static String readEventId() {
		String message = "Enter Event ID: ";
		System.out.print(message);
		String eventId = new Scanner(System.in).nextLine().toUpperCase();
		
		if( !(eventId.startsWith("MTL") || eventId.startsWith("TOR") || eventId.startsWith("OTW")) ) {
			System.out.println("\nResult: Illegal Event Id \n");
			return null;
		} else if(eventId.length() != 10) {
			System.out.println("\nResult: Illegal Event Id \n");
			return null;
		}
		
		return eventId;
	}
	
	private static String readCustomerId() {
		
		System.out.print("Customer Id: ");
		String customerId = new Scanner(System.in).nextLine().toUpperCase();
		
		String province = customerId.substring(0, 4);
		if( !(province.equals("MTL") || province.equals("TOR") || province.equals("OTW"))){
			System.out.println("\nResult: Invalid Student id (Department) \n");
			return null;
			
		} else if(!customerId.substring(3, 4).equals("C")) {
			System.out.println("\nResult: Invalid Customer id (User Type) \n");
			return null;
			
		} else if(customerId.length() != 8) {
			System.out.println("\nResult: Invalid Student id (Number) \n");
			return null;
		}
		
		return customerId;
	}
	
	public static void addEvent() {
		
		String eventType = readEventType();
		if(eventType == null) return;

		String eventId = readEventId();
		if(eventId == null) return;
		
		if(!eventId.startsWith(Cache.getInstance().getProvince())) {
			System.out.println("\nResult: You cant add event to other provinces \n");
			return;
		}
		
		System.out.print("Max Capacity: ");
		int maxCapacity = new Scanner(System.in).nextInt();
		
		if(Cache.getInstance().dems.addEvent(Cache.getInstance().id, eventId, eventType, maxCapacity))
			Logger.addCourse_Successfull(eventId, eventType, Cache.getInstance().id);
		else
			Logger.addCourse_Failed(eventId, eventType, Cache.getInstance().id);
		
	}

	public static void removeEvent() {
		
		String semester = readEventType();
		if(semester == null) return;
		
		String eventId = readEventId();
		if(eventId == null) return;
		
		if(Cache.getInstance().dems.removeEvent(Cache.getInstance().id, eventId, semester))
			Logger.removeCourse_Successfull(eventId, semester, Cache.getInstance().id);
		else
			Logger.removeCourse_Failed(eventId, semester, Cache.getInstance().id);
	}

	public static void listEventAvailability() {
		
		String eventType = readEventType();
		if(eventType == null) return;
				
		Any any = Cache.getInstance().dems.listEventAvailability(Cache.getInstance().id, eventType);
		HashMap<String, Integer> events = (HashMap<String, Integer>) any.extract_Value();
		
		Logger.listAvailableCourses(Cache.getInstance().id, eventType, events);
	}	
	
	public static void bookEvent() {
		
		String eventType = readEventType();
		if(eventType == null) return;
		
		String eventId = readEventId();
		if(eventId == null) return;
		
		String studentId = Cache.getInstance().id;
		
		if(Cache.getInstance().getClientType() == Cache.ClientType.MANAGER){
			studentId = readCustomerId();
			if(studentId == null) return;
		}
		
		Any any = Cache.getInstance().dems.bookEvent(studentId, eventId, eventType);
		SimpleEntry<Boolean, String> result = (SimpleEntry<Boolean, String>) any.extract_Value();
		Logger.enrolCourse(studentId, eventId, eventType, result.getKey(), result.getValue().replaceAll("_", " "));
		
	}
	
	public static void cancelEvent() {
		
		String eventId = readEventId();
		if(eventId == null) return;

		String eventType = readEventType();
		if(eventType == null) return;

		String customerId = Cache.getInstance().id;
		
		if(Cache.getInstance().getClientType() == Cache.ClientType.MANAGER){
			customerId = readCustomerId();
			if(customerId == null) return;
		}

		boolean result = Cache.getInstance().dems.cancelEvent(customerId, eventId, eventType);
		Logger.dropCourse(customerId, eventId, result);
	}

	public static void getBookingSchedule() {
		
		String customerId = Cache.getInstance().id;
		
		if(Cache.getInstance().getClientType() == Cache.ClientType.MANAGER){
			customerId = readCustomerId();
			if(customerId == null) return;
		}

		Any any = Cache.getInstance().dems.getBookingSchedule(customerId);
		HashMap<String, ArrayList<String>> result = (HashMap<String, ArrayList<String>>) any.extract_Value();
		
		Logger.getClassSchedule(customerId, result);
		
	}

	public static void swapEvent() {
		
		String customerId = Cache.getInstance().id;
		
		if(Cache.getInstance().getClientType() == Cache.ClientType.MANAGER){
			customerId = readCustomerId();
			if(customerId == null) return;
		}

		System.out.println("Details of old Event");
		String oldEventId = readEventId();
		if(oldEventId == null) return;

		String oldEventType = readEventType();
		if(oldEventType == null) return;

		System.out.println("Details of new Event");
		String newEventId = readEventId();
		if(newEventId == null) return;

		String newEventType = readEventType();
		if(newEventType == null) return;

		Any any = Cache.getInstance().dems.swapEvent(customerId, newEventId, oldEventId, newEventType, oldEventType);
		SimpleEntry<Boolean, String> result = (SimpleEntry<Boolean, String>) any.extract_Value();
		
		Logger.swapCourse(customerId, newEventId, oldEventId, result);
		
	}

}
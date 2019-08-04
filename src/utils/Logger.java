package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import client.controller.data.Cache;

public class Logger {
	
	public static boolean isServer = false;

	public static void log(String logStatement) {
		
		String time = new SimpleDateFormat("[HH:mm:ss dd/MM/yyyy]").format(new java.util.Date()) + " "; 
		
		if(!isServer) System.out.print("\nResult: ");
		else {
			System.out.print(time);
		}
		
		System.out.println(logStatement);
		
		if(!isServer)
			System.out.println("");
						
		writeLogToFile(time + logStatement);
	}
	
	private static void writeLogToFile(String logstatement){
		
		logstatement += "\n";
		
		try{

				
				String folder = "../Logs/Instance1/ClientLogs/";
				
				if(Cache.getInstance().getClientType() == Cache.ClientType.MANAGER) folder += "Advisor/";
				else folder += "Student/";
				
				File logFile = new File(folder + Cache.getInstance().id + ".txt");
				logFile.getParentFile().mkdirs();
				logFile.createNewFile();
				
				FileOutputStream logFileOutputStream = new FileOutputStream(logFile, true);
				logFileOutputStream.write(logstatement.getBytes());
				logFileOutputStream.close();
			
		} catch (IOException ignored){}
		
	}
	
	public static void addEvent_Successful(String eventId, String eventType, String managerId) {
		log("[Add Event] [Successfull] " + eventId + " successfully added to " + eventType + " eventType, by " + managerId);
	}
	
	public static void addEvent_Failed(String eventId, String eventType, String managerId) {
		log("[Add Event] [Failed] " +eventId + " failed to add to " + eventType + " eventType, by " + managerId + " as it is already exists");
	}

	public static void removeEvent_Failed(String eventId, String eventType, String adivsorId) {
		log("[Remove Event] [Failed] " + eventId + " failed to remove from " + eventType + " eventType, by " + adivsorId + " as it is doesn't exists");
	}

	public static void removeEvent_Successful(String eventId, String eventType, String adivsorId) {
		log("[Remove Event] [Successfull] " + eventId + " successfully removed from " + eventType + " eventType, by " + adivsorId);
	}
	
	public static void listAvailableEvents(String managerId, String eventType, HashMap<String, Integer> result){
		
		String temp = "[List Available Events] [Successfull] " + managerId + " has requested Event availability for " + eventType + " eventType \n";
		
		for (Entry<String, Integer> event : result.entrySet())
			temp += event.getKey() + " => " + event.getValue() + ", ";
		
		log(temp);
	}
	
	public static void bookEvent(String customerId, String eventId, String eventType, boolean status, String message) {
		
		if(status)
			log("[Enrol Event] [Successfull] " + customerId + " has enrolled into Event " + eventId + " for " + eventType + " eventType \n");
		else
			log("[Enrol Event] [Failed] " + customerId + " has tried to enrolled into Event " + eventId + " for " + eventType + " eventType failed because of " + message + " \n");
				
	}
	
	public static void cancelEvent(String customerId, String eventId, String eventType,boolean status) {
		
		if(status)
			log("[Cancel Event] [Successfull] " + customerId + " has been successfully Cancelped from Event " + eventId + " \n");
		else
			log("[Cancel Event] [Failed] " + customerId + " has tried to Cancel from Event " + eventId + " but failed because he isn't enrolled it in any eventType \n");

	}
	
	public static void getBookingSchedule(String customerId, HashMap<String, ArrayList<String>> result) {
		
		String temp = "[Booking Schedule] [Successfull] " + customerId + " has requested booking schedule \n";
		
		for (Entry<String, ArrayList<String>> entry : result.entrySet()) {
			
			temp += entry.getKey() + ": ";
			
			if(entry.getValue().size() > 0) {
				for (String Event: entry.getValue())
					temp += Event + ", ";
			} else
				temp += "None";
			
			temp += "\n";
		}
		
		log(temp);
		
	}

	public static void swapEvent(String customerId, String neweventId, String oldeventId, String newEventType, String oldEventType, SimpleEntry<Boolean, String> result) {
				
		if(result.getKey())
			log("[Swap Event] [Successfull] " + customerId + " has successfully swapped Event from " + oldeventId + " to " + neweventId);
		else 
			log("[Swap Event] [Failed] " + customerId + " has requested to swap Event from " + oldeventId + " to " + neweventId + " but failed because " + result.getValue());

	}
	
}

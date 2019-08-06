package server.instance4.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import server.instance3.util.EventType;
import server.instance4.pojo.City;
import server.instance4.pojo.Utility;
import utils.Config;
import utils.Constants;
import utils.UDPUtilities;

public class DEMS {

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private City City;
	private Lock _lock;
	private HashMap<String, HashMap<String, HashMap<String, Object>>> CityDatabase;

	public DEMS(String city) {
		this.City = City.valueOf(city);
		CityDatabase = new HashMap<>();
		_lock = new ReentrantLock(true);

	}


	public boolean addEvent(String managerId, String eventId, String eventType, int CAPACITY) {
		boolean status = false;
		String msg = "";
		// locking
		_lock.lock();

		if (CityDatabase.containsKey(eventType)) {
			HashMap<String, HashMap<String, Object>> events = CityDatabase.get(eventType);

			if (events.containsKey(eventId)) {
				status = false;
				msg = "Events already exists for " + eventType + " eventType.";
			} else {
				HashMap<String, Object> eventDetails = new HashMap<>();
				eventDetails.put(Constants.CAPACITY, CAPACITY);
				eventDetails.put(Constants.CUSTOMERS_ENROLLED, 0);
				eventDetails.put(Constants.CUSTOMER_IDS, new HashSet<String>());
				events.put(eventId, eventDetails);
				status = true;
				msg = eventId + " Added.";
			}
		} else {
			// eventType is non-existant
			HashMap<String, Object> eventDetails = new HashMap<>();
			eventDetails.put(Constants.CAPACITY, CAPACITY);
			eventDetails.put(Constants.CUSTOMERS_ENROLLED, 0);
			eventDetails.put(Constants.CUSTOMER_IDS, new HashSet<String>());
			HashMap<String, HashMap<String, Object>> events = new HashMap<>();
			events.put(eventId, eventDetails);

			// synchronizing the write operation to the in-memory database
			CityDatabase.put(eventType, events);

			status = true;
			msg = eventId + " Added.";
		}		

		// releasing the lock
		_lock.unlock();

		LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_ADD_EVENT,
				Arrays.asList(managerId, eventId, eventType, CAPACITY), status, msg));

		return status;
	}


	public boolean removeEvent(String managerId, String eventId, String eventType) {

		boolean status = false;
		String msg = "";
		if (CityDatabase.containsKey(eventType)) {
			HashMap<String, HashMap<String, Object>> events = CityDatabase.get(eventType);
			// locking
			_lock.lock();
			synchronized (events) {

				if (events.containsKey(eventId)) {

					events.remove(eventId);
					status = true;
					msg = eventId + " removed";
				} else {
					status = false;
					msg = eventType + " eventType doesn't offer this event yet.";
				}
			}
			// releasing the lock
			_lock.unlock();
		} else {
			status = false;
			msg = eventType + " eventType doesn't have any event yet.";
		}

		LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_REMOVE_EVENT,
				Arrays.asList(managerId, eventId, eventType), status, msg));

		return status;
	}

	

	public HashMap<String, Integer> listEventAvailability(String managerId, String eventType) {

		HashMap<String, Integer> result = new HashMap<>();
		result.putAll(listEventAvailabilityForThisServer(eventType));

		// inquire different Citys
		for (City City : City.values()) {
			if (City != City) {
				result.putAll((HashMap<String, Integer>) Utility.byteArrayToObject(
						udpCommunication(City, eventType, Constants.OP_LIST_EVENT_AVAILABILITY)));
			}
		}

		LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_LIST_EVENT_AVAILABILITY,
				Arrays.asList(managerId, eventType), result != null, result));

		return result;
	}


	private HashMap<String, Integer> listEventAvailabilityForThisServer(String eventType) {
		HashMap<String, Integer> result = new HashMap<>();
		// get events from the current City
		if (CityDatabase.containsKey(eventType)) {
			CityDatabase.get(eventType).forEach(
					(event, eventDetails) -> result.put(event, (Integer) eventDetails.get(Constants.CAPACITY)
							- (Integer) eventDetails.get(Constants.CUSTOMERS_ENROLLED)));
		}

		return result;
	}

	

	public SimpleEntry<Boolean, String> bookEvent(String customerId, String eventId, String eventType) {

		boolean status = true;
		String msg = null;
		SimpleEntry<Boolean, String> result = null;

		// get student schedule
		HashMap<String, ArrayList<String>> customerSchedule = getBookingSchedule(customerId);

		List<String> Cityevents = new ArrayList<>();
		List<String> outOfCityevents = new ArrayList<>();
		customerSchedule.forEach((sem, events) -> {
			events.forEach((event) -> {
				City c = City.valueOf(event.substring(0, 3).toUpperCase());
				if (c == this.City)
					Cityevents.add(event);
				else
					outOfCityevents.add(event);
			});
		});
		City eventCity = City.valueOf(eventId.substring(0, 3).toUpperCase());
		// enroll in this City only
		if (City == eventCity) {

			// student already taking this event
			if (Cityevents.contains(eventId)) {
				status = false;
				msg = customerId + " is already enrolled in " + eventId + ".";
			}
			if (status) {
				result = enrollmentForThisCity(customerId, eventId, eventType);
			}

		} else {

			if (outOfCityevents.size() >= Constants.MAX_CROSS_EVENTS) {
				status = false;
				msg = customerId + " is already enrolled in " + Constants.MAX_CROSS_EVENTS
						+ " out-of-City events.";
			} else {
				// enquire respective City
				for (City City : City.values()) {
					if (City == eventCity) {
						HashMap<String, String> data = new HashMap<>();
						data.put(Constants.CUSTOMER_ID, customerId);
						data.put(Constants.EVENT_ID, eventId);
						data.put(Constants.EVENT_TYPE, eventType);

						result = (SimpleEntry<Boolean, String>) Utility.byteArrayToObject(
								udpCommunication(eventCity, data, Constants.OP_BOOK_EVENT));
					}
				}
			}

			// status = false;
			// msg = "City not found.";
		}

		if (result == null)
			result = new SimpleEntry<Boolean, String>(status, msg);

		LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_BOOK_EVENT,
				Arrays.asList(customerId, eventId, eventType), result.getKey(), result.getValue()));

		return result;
	}

	private SimpleEntry<Boolean, String> enrollmentForThisCity(String customerId, String eventId,
			String eventType) {
		boolean status;
		String msg;
		if (CityDatabase.containsKey(eventType)) {
			HashMap<String, HashMap<String, Object>> events = CityDatabase.get(eventType);

			if (events.containsKey(eventId)) {
				HashMap<String, Object> eventDetails = events.get(eventId);

				_lock.lock(); // acquire the lock
				if (((Integer) eventDetails.get(Constants.CAPACITY)
						- (Integer) eventDetails.get(Constants.CUSTOMERS_ENROLLED)) > 0) {

					status = ((HashSet<String>) eventDetails.get(Constants.CUSTOMER_IDS)).add(customerId);
					if (status) {
						eventDetails.put(Constants.CUSTOMERS_ENROLLED,
								(Integer) eventDetails.get(Constants.CUSTOMERS_ENROLLED) + 1);
						status = true;
						msg = "Enrollment Successful.";
					} else {
						status = false;
						msg = customerId + " is already enrolled in " + eventId + ".";
					}

				} else {
					status = false;
					msg = eventId + " is full.";
				}
				_lock.unlock(); // release the lock
			} else {
				status = false;
				msg = eventId + " is not offered in " + eventType + " eventType.";
			}
		} else {
			status = false;
			msg = "No events avialable for " + eventType + " eventType.";
		}

		return new SimpleEntry<Boolean, String>(status, msg);
	}

	public HashMap<String, ArrayList<String>> getBookingSchedule(String customerId) {
		HashMap<String, ArrayList<String>> schedule = new HashMap<>();
		schedule.putAll(getBookingScheduleThisServer(customerId));

		// inquire different Citys
		for (City City : City.values()) {
			if (City != this.City) {

				HashMap<String, ArrayList<String>> CitySchedule = (HashMap<String, ArrayList<String>>) Utility
						.byteArrayToObject(udpCommunication(City, customerId, Constants.OP_GET_BOOKING_SCHEDULE));

				for (String eventType : CitySchedule.keySet()) {
					if (schedule.containsKey(eventType)) {
						schedule.get(eventType).addAll(CitySchedule.get(eventType));
					} else {
						schedule.put(eventType, CitySchedule.get(eventType));
					}
				}
			}
		}
		LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_GET_BOOKING_SCHEDULE, Arrays.asList(customerId),
				schedule != null, schedule));

		return schedule;
	}

	private HashMap<String, ArrayList<String>> getBookingScheduleThisServer(String customerId) {
		HashMap<String, ArrayList<String>> schedule = new HashMap<>();
		CityDatabase.forEach((eventType, events) -> {
			events.forEach((event, details) -> {
				if (((HashSet<String>) details.get(Constants.CUSTOMER_IDS)).contains(customerId)) {
					if (schedule.containsKey(eventType)) {
						schedule.get(eventType).add(event);
					} else {
						ArrayList<String> temp = new ArrayList<>();
						temp.add(event);
						schedule.put(eventType, temp);
					}
				}
			});
		});
		
		for(EventType eventType : EventType.values()) {
			if(!schedule.containsKey(eventType.name())){
				schedule.put(eventType.name(),new ArrayList<String>());
			}
		}
		
		return schedule;
	}

	public boolean cancelEvent(String customerId, String eventId, String eventType) {

		City eventCity = City.valueOf(eventId.substring(0, 3).toUpperCase());
		SimpleEntry<Boolean, String> result;
		if (City == eventCity) {
			result = cancelEventOnThisServer(customerId, eventId);
		} else {
			HashMap<String, String> data = new HashMap<>();
			data.put(Constants.CUSTOMER_ID, customerId);
			data.put(Constants.EVENT_ID, eventId);
			data.put(Constants.EVENT_TYPE, eventType);
			result = (SimpleEntry<Boolean, String>) Utility
					.byteArrayToObject(udpCommunication(eventCity, data, Constants.OP_CANCEL_EVENT));
		}

		// LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_CANCEL_EVENT,
		// 		Arrays.asList(customerId, eventId), result.getKey(), result.getValue()));
		return result.getKey();
	}

	private SimpleEntry<Boolean, String> cancelEventOnThisServer(String customerId, String eventId) {
		final Map<Boolean, String> temp = new HashMap<>();
		if (CityDatabase.size() > 0) {
			CityDatabase.forEach((sem, events) -> {
				if (events.containsKey(eventId)) {
					events.forEach((event, eventDetails) -> {
						// locking
						_lock.lock();
						if (event.equals(eventId)) {
							boolean status = ((HashSet<String>) eventDetails.get(Constants.CUSTOMER_IDS))
									.remove(customerId);
							if (status) {
								eventDetails.put(Constants.CUSTOMERS_ENROLLED,
										((Integer) eventDetails.get(Constants.CUSTOMERS_ENROLLED) - 1));
								temp.put(true, "success");
							} else {
								temp.put(false, customerId + " isn't enrolled in " + eventId + ".");
							}
						}
						// releasing the lock
						_lock.unlock();
					});
				} else {
					temp.put(false, eventId + " isn't offered by the City yet.");
				}
			});
		} else {
			temp.put(false, eventId + " isn't offered by the City yet.");
		}

		if (temp.containsKey(true)) {
			return new SimpleEntry<Boolean, String>(true, "event Dropped.");
		} else {
			return new SimpleEntry<Boolean, String>(false, temp.get(false));
		}
	}

	public SimpleEntry<Boolean, String> swapEvent(String customerId, String neweventId, String oldeventId,
			String newEventType, String oldEventType) {
		boolean status = true;
		String msg = null;
		String eventType = null;
		SimpleEntry<Boolean, String> result2;

		/* VALIDATIONS */

		Map<String, ArrayList<String>> customerSchedule = getBookingSchedule(customerId);

		City oldeventCity = City.valueOf(oldeventId.substring(0, 3).toUpperCase());
		City neweventCity = City.valueOf(neweventId.substring(0, 3).toUpperCase());

		List<String> Cityevents = new ArrayList<>();
		List<String> outOfCityevents = new ArrayList<>();
		customerSchedule.forEach((et, events) -> {
			events.forEach((event) -> {
				City c = City.valueOf(event.substring(0, 3).toUpperCase());
				if (c == this.City)
					Cityevents.add(event);
				else
					outOfCityevents.add(event);
			});
		});

		if (!Cityevents.contains(oldeventId) && !outOfCityevents.contains(oldeventId)) {
			// check if student is enrolled in oldevent or not
			status = false;
			msg = customerId + " is not enrolled in " + oldeventId;
		} else if (Cityevents.contains(neweventId) || outOfCityevents.contains(neweventId)) {
			// check if the student is already enrolled in neweventId
			status = false;
			msg = customerId + " is already enrolled in " + neweventId;
		} else if (neweventCity != this.City && oldeventCity == this.City && outOfCityevents.size() >= 3) {
			status = false;
			msg = customerId + " is already enrolled in " + Constants.MAX_CROSS_EVENTS + " out-of-City events.";
		}

		if (!status) {
			return new SimpleEntry<Boolean, String>(status, msg);
		}

		// // get the oldeventId eventType
		// outer: for (String e2 : customerSchedule.keySet()) {
		// for (String c : customerSchedule.get(e2)) {
		// if (c.equalsIgnoreCase(oldeventId)) {
		// eventType = e2;
		// break outer;
		// }
		// }
		// }

		if (neweventCity == City) {
			// enrolling in this City, dropping elective or this City event
			// check if new event is offered or not

			_lock.lock(); // Acquire lock
			result2 = checkEventAvailability(neweventId, newEventType);

			if (result2.getKey()) {
				// drop other City event
				boolean temp = cancelEvent(customerId, oldeventId, oldEventType);

				if (temp) {
					result2 = enrollmentForThisCity(customerId, neweventId, newEventType);

					if (result2.getKey()) {
						status = true;
						msg = Constants.OP_SWAP_EVENT + " successfully";
					} else {
						// ROLLBACK
						bookEvent(customerId, oldeventId, oldEventType);
						status = true;
						msg = Constants.OP_SWAP_EVENT + " successfully";
					}
				} else {
					status = result2.getKey();
					msg = result2.getValue();
				}
			} else {
				status = result2.getKey();
				msg = result2.getValue();
			}
			_lock.unlock();
			// finish

		} else {
			// dropping this c event, enrolling in elective

			HashMap<String, String> data = new HashMap<>();
			data.put(Constants.CUSTOMER_ID, customerId);
			data.put(Constants.NEW_EVENT_ID, neweventId);
			data.put(Constants.OLD_EVENT_ID, oldeventId);
			data.put(Constants.OLD_EVENT_CITY, City.toString());
			data.put("oldEventType", oldEventType);
			data.put("newEventType", newEventType);

			result2 = (SimpleEntry<Boolean, String>) Utility
					.byteArrayToObject(udpCommunication(neweventCity, data, Constants.OP_SWAP_EVENT));
			status = result2.getKey();
			msg = result2.getValue();
		}

		LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_SWAP_EVENT,
				Arrays.asList(customerId, neweventId, oldeventId), status, msg));

		return new SimpleEntry<Boolean, String>(status, msg);
	}


	public void udpServer() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(City.getUdpPort());
			byte[] buffer = new byte[1000];// to stored the received data from the client.
			// server is always in listening mode.
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				// Server waits for the request to come
				socket.receive(request); // request received

	            byte[] response = processRequest(request, socket);
	            if(response == null)
	            	continue; //will reply to Front end manually

				DatagramPacket reply = new DatagramPacket(response, response.length, request.getAddress(),
						request.getPort()); // reply packet ready
				socket.send(reply); // reply sent
			}
		} catch (SocketException e) {
			LOGGER.severe("SocketException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.severe("IOException : " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (socket != null)
				socket.close();
		}
	}

	private byte[] processRequest(DatagramPacket request, DatagramSocket socket) {
		
		String stringData = new String(request.getData(), 0, request.getLength());
		if (stringData.startsWith("SEQUENCER&")) {
			processSequencerRequest(stringData.replace("SEQUENCER&", ""), socket);
			return null;
		}

		else
			return processUDPRequest(request.getData());
	}
	

	private byte[] processUDPRequest(byte[] data) {
		
		byte[] response = null;
		HashMap<String, Object> request = (HashMap<String, Object>) Utility.byteArrayToObject(data);

		for (String key : request.keySet()) {

			LOGGER.info("Received UDP Socket call for method[" + key + "] with parameters[" + request.get(key) + "]");
			switch (key) {
			case Constants.OP_LIST_EVENT_AVAILABILITY:
				String eventType = (String) request.get(key);
				response = Utility.objectToByteArray(listEventAvailabilityForThisServer(eventType));
				break;
			case Constants.OP_BOOK_EVENT:
				HashMap<String, String> info = (HashMap<String, String>) request.get(key);
				response = Utility.objectToByteArray(enrollmentForThisCity(info.get(Constants.CUSTOMER_ID),
						info.get(Constants.EVENT_ID), info.get(Constants.EVENT_TYPE)));
				break;
			case Constants.OP_GET_BOOKING_SCHEDULE:
				String customerId = (String) request.get(key);
				response = Utility.objectToByteArray(getBookingScheduleThisServer(customerId));
				break;
			case Constants.OP_CANCEL_EVENT:
				info = (HashMap<String, String>) request.get(key);
				response = Utility.objectToByteArray(
						cancelEventOnThisServer(info.get(Constants.CUSTOMER_ID), info.get(Constants.EVENT_ID)));
				break;
			case Constants.OP_SWAP_EVENT:
				info = (HashMap<String, String>) request.get(key);
				response = Utility.objectToByteArray(atomicSwapOnCurrentServer(info.get(Constants.CUSTOMER_ID),
						info.get(Constants.NEW_EVENT_ID), info.get(Constants.OLD_EVENT_ID),
						info.get(Constants.OLD_EVENT_CITY), info.get(Constants.EVENT_TYPE)));
				break;
				
			case Constants.OP_GETSTATE:
				response = getState();
				break;
			case Constants.OP_SETSTATE:
				HashMap<String, HashMap<String, HashMap<String, Object>>> temp = (HashMap<String, HashMap<String, HashMap<String, Object>>>) request.get(key);
				setState(temp);
				response = String.valueOf(true).getBytes();
				break;
			case Constants.OP_ISALIVE:
				LOGGER.info("\n\n\n\n GOT IT ALIVE REQUEST\n\n\n\n");
				response = String.valueOf(true).getBytes();
				break;
			}
		}

		return response;
	}
	
	private void processSequencerRequest(String receivedRequest, DatagramSocket socket) {
		
		String[] temp = receivedRequest.split("&", 2);
		//int sequenceNumber = Integer.parseInt(temp[0]);
		String request[] = temp[1].trim().split("&");
		byte[] response = new byte[1000];
		
		switch(request[0]){
		
			case "addEvent":
				boolean temp1 = addEvent(request[1], request[2], request[3], Integer.parseInt(request[4]));
				response = UDPUtilities.objectToByteArray(temp1);
				break;
				
			case "removeEvent":
				response = UDPUtilities.objectToByteArray(removeEvent(request[1], request[2], request[3]));
				break;
				
			case "listEventAvailability":
				response = UDPUtilities.objectToByteArray(listEventAvailability(request[1], request[2]));
				break;
				
			case "bookEvent":
				response = UDPUtilities.objectToByteArray(bookEvent(request[1], request[2], request[3]));
				break;
				
			case "getBookingSchedule":
				response = UDPUtilities.objectToByteArray(getBookingSchedule(request[1]));
				break;
				
			case "cancelEvent":
				response = UDPUtilities.objectToByteArray(cancelEvent(request[1], request[2], request[3]));
				break;
				
			case "swapevent":
				response = UDPUtilities.objectToByteArray(swapEvent(request[1], request[2], request[3], request[4], request[5]));
				break;
		
		}
		
		try {
			
			InetAddress frontendIP = InetAddress.getByName(Config.getStringConfig("FRONTEND_IP"));
			DatagramPacket replyPacket = new DatagramPacket(response, response.length, frontendIP, Config.getConfig("FRONTEND_PORT"));
	        socket.send(replyPacket);
	        
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

	private SimpleEntry<Boolean, String> atomicSwapOnCurrentServer(String customerId, String neweventId,
			String oldeventId, String oldeventCity, String eventType) {

		SimpleEntry<Boolean, String> result;
		boolean status;
		String msg;

		try {
			// Locking
			_lock.lock();

			result = checkEventAvailability(neweventId, eventType);
			if (result.getKey()) {
				HashMap<String, String> data = new HashMap<>();
				data.put(Constants.CUSTOMER_ID, customerId);
				data.put(Constants.EVENT_ID, oldeventId);
				result = (SimpleEntry<Boolean, String>) Utility.byteArrayToObject(
						udpCommunication(City.valueOf(oldeventCity), data, Constants.OP_CANCEL_EVENT));

				if (result.getKey()) {
					result = enrollmentForThisCity(customerId, neweventId, eventType);

					if (result.getKey()) {
						status = true;
						msg = Constants.OP_SWAP_EVENT + " successfully";
					} else {
						// ROLLBACK
						bookEvent(customerId, oldeventId, eventType);
						status = false;
						msg = Constants.OP_SWAP_EVENT + " unsuccessful";
					}
				} else {
					status = result.getKey();
					msg = result.getValue();
				}
			} else {
				status = result.getKey();
				msg = result.getValue();
			}
			return new SimpleEntry<Boolean, String>(status, msg);
		} finally {
			// Releasing the lock
			_lock.unlock();
		}
	}

	private SimpleEntry<Boolean, String> checkEventAvailability(String eventId, String eventType) {

		boolean status = true;
		String msg = "";
		if (CityDatabase.containsKey(eventType)) {
			HashMap<String, HashMap<String, Object>> events = CityDatabase.get(eventType);

			if (events.containsKey(eventId)) {
				HashMap<String, Object> eventDetails = events.get(eventId);

				if (((Integer) eventDetails.get(Constants.CAPACITY)
						- (Integer) eventDetails.get(Constants.CUSTOMERS_ENROLLED)) > 0) {
					status = true;

				} else {
					status = false;
					msg = eventId + " is already full.";
				}
			} else {
				status = false;
				msg = eventId + " is not offered in " + eventType + " eventType.";
			}
		} else {
			status = false;
			msg = "No events available for " + eventType + " eventType.";
		}

		return new SimpleEntry<Boolean, String>(status, msg);
	}

	private byte[] udpCommunication(City City, Object info, String method) {

		LOGGER.info("Making UDP Socket Call to " + City + " Server for method : " + method);

		// UDP SOCKET CALL AS CLIENT
		HashMap<String, Object> data = new HashMap<>();
		byte[] response = null;
		data.put(method, info);
		DatagramSocket datagramSocket = null;
		try {
			datagramSocket = new DatagramSocket();
			byte[] message = Utility.objectToByteArray(data);
			InetAddress remoteUdpHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(message, message.length, remoteUdpHost, City.getUdpPort());
			datagramSocket.send(request);
			byte[] buffer = new byte[65556];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			datagramSocket.receive(reply);
			response = reply.getData();

		} catch (SocketException e) {
			LOGGER.severe("SocketException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.severe("IOException : " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (datagramSocket != null)
				datagramSocket.close();
		}

		return response;
	}
	
	public byte[] getInternalState() {
		return utils.Utility.deepCopyInstance4State(CityDatabase);
	}

	public void setInternalState(byte[] data) {
		this.CityDatabase = (HashMap<String, HashMap<String, HashMap<String, Object>>>) UDPUtilities
				.byteArrayToObject(data);
	}

	public byte[] getState() {
		return utils.Utility.deepCopyInstance3State(CityDatabase);
	}

	public void setState(HashMap<String, HashMap<String, HashMap<String, Object>>> data) {
		this.CityDatabase = data;
	}
	
}

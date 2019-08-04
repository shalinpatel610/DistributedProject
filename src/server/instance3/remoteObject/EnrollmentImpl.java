package server.instance3.remoteObject;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import server.instance3.logging.MyLogger;
import server.instance3.util.City;
import server.instance3.util.EventType;
import server.instance3.util.Utils;
import utils.Config;
import utils.Constants;
import utils.UDPUtilities;
import utils.Utility;

public class EnrollmentImpl implements EnrollmentInterface {

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private City City;

	private ReentrantLock rl;

	// in-memory database
	private HashMap<String, HashMap<String, HashMap<String, Object>>> cityDatabase;

	/**
	 * Constructor
	 * 
	 * @param city
	 */
	public EnrollmentImpl(String city, String logFile) {
		this.City = City.valueOf(city);
		cityDatabase = new HashMap<>();
		this.rl = new ReentrantLock(true); // fair reentrant lock
		setupLogging(logFile);
	}

	public boolean addEvent(String managerId, String eventId, String eventType, int capacity) throws RemoteException {
		boolean status = false;
		String msg = Constants.EMPTYSTRING;

		// Acquire Lock
		rl.lock();

		if (cityDatabase.containsKey(eventType)) {
			HashMap<String, HashMap<String, Object>> events = cityDatabase.get(eventType);

			if (events.containsKey(eventId)) {
				status = false;
				msg = "Event already exists for " + eventType + " eventType.";
			} else {
				synchronized (this) {
					HashMap<String, Object> eventDetails = new HashMap<>();
					eventDetails.put(Constants.CAPACITY, capacity);
					eventDetails.put(Constants.CUSTOMERS_ENROLLED, 0);
					eventDetails.put(Constants.CUSTOMER_IDS, new HashSet<String>());
					events.put(eventId, eventDetails);
				}
				status = true;
				msg = eventId + " Added.";
			}

		} else {
			// eventType doesn't exists
			HashMap<String, Object> eventDetails = new HashMap<>();
			eventDetails.put(Constants.CAPACITY, capacity);
			eventDetails.put(Constants.CUSTOMERS_ENROLLED, 0);
			eventDetails.put(Constants.CUSTOMER_IDS, new HashSet<String>());
			HashMap<String, HashMap<String, Object>> events = new HashMap<>();
			events.put(eventId, eventDetails);

			// synchronizing the write operation to the in-memory database
			synchronized (this) {
				this.cityDatabase.put(eventType, events);
			}
			status = true;
			msg = eventId + " Added.";
		}

		rl.unlock();
		LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_ADD_EVENT,
				Arrays.asList(managerId, eventId, eventType, capacity), status, msg));

		return status;
	}

	public boolean removeEvent(String managerId, String eventId, String eventType) throws RemoteException {

		boolean status = false;
		String msg = Constants.EMPTYSTRING;
		if (cityDatabase.containsKey(eventType)) {
			HashMap<String, HashMap<String, Object>> events = cityDatabase.get(eventType);
			rl.lock();
			if (events.containsKey(eventId)) {
				synchronized (this) {
					events.remove(eventId);
				}
				status = true;
				msg = eventId + " removed";
				rl.unlock();
			} else {
				status = false;
				msg = eventType + " eventType doesn't have this event yet.";
			}
		} else {
			status = false;
			msg = eventType + " eventType doesn't have any event yet.";
		}

		LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_REMOVE_EVENT,
				Arrays.asList(managerId, eventId, eventType), status, msg));

		return status;
	}


	public HashMap<String, Integer> listEventAvailability(String managerId, String eventType) throws RemoteException {

		HashMap<String, Integer> result = new HashMap<>();
		System.out.println("List event Availability called");
		result.putAll(listEventAvailabilityForThisServer(eventType));

		// inquire different Citys
		for (City c : City.values()) {
			if (c != this.City) {
				result.putAll((HashMap<String, Integer>) Utils
						.byteArrayToObject(udpCommunication(c, eventType, Constants.OP_LIST_EVENT_AVAILABILITY)));
			}
		}

		LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_LIST_EVENT_AVAILABILITY,
				Arrays.asList(managerId, eventType), result != null, result));

		return result;
	}


	private HashMap<String, Integer> listEventAvailabilityForThisServer(String eventType) {
		HashMap<String, Integer> result = new HashMap<>();
		// get events from the current City
		if (cityDatabase.containsKey(eventType)) {
			cityDatabase.get(eventType)
					.forEach((event, eventDetails) -> result.put(event, (Integer) eventDetails.get(Constants.CAPACITY)
							- (Integer) eventDetails.get(Constants.CUSTOMERS_ENROLLED)));
		}

		return result;
	}


	@Override
	public SimpleEntry<Boolean, String> bookEvent(String customerId, String eventId, String eventType)
			throws RemoteException {

		boolean status = true;
		String msg = null;
		SimpleEntry<Boolean, String> result = null;

		HashMap<String, ArrayList<String>> customerSchedule = getBookingSchedule(customerId);

		List<String> CityEvents = new ArrayList<>();
		List<String> outOfCityEvents = new ArrayList<>();
		customerSchedule.forEach((ET, events) -> {
			events.forEach((event) -> {
				City c = City.valueOf(event.substring(0, 3).toUpperCase());
				if (c == this.City)
					CityEvents.add(event);
				else
					outOfCityEvents.add(event);
			});
		});
		City courseCity = City.valueOf(eventId.substring(0, 3).toUpperCase());
		// enroll in this City only
		if (City == courseCity) {

			// customer already booked this event
			if (CityEvents.contains(eventId)) {
				status = false;
				msg = customerId + " is already booked in " + eventId + ".";
			}
			if (status) {
				result = enrollmentForThisCity(customerId, eventId, eventType);
			}

		} else {

			if (outOfCityEvents.size() >= Constants.MAX_CROSS_EVENTS) {
				status = false;
				msg = customerId + " is already booked in " + Constants.MAX_CROSS_EVENTS + " out-of-City events.";
			} else {
				// enquire respective City
				for (City c : City.values()) {
					if (c == courseCity) {
						HashMap<String, String> data = new HashMap<>();
						data.put(Constants.CUSTOMER_ID, customerId);
						data.put(Constants.EVENT_ID, eventId);
						data.put(Constants.EVENT_TYPE, eventType);

						result = (SimpleEntry<Boolean, String>) Utils
								.byteArrayToObject(udpCommunication(courseCity, data, Constants.OP_BOOK_EVENT));
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

	private SimpleEntry<Boolean, String> enrollmentForThisCity(String customerId, String eventId, String eventType) {
		boolean status;
		String msg;
		if (cityDatabase.containsKey(eventType)) {
			HashMap<String, HashMap<String, Object>> events = cityDatabase.get(eventType);

			if (events.containsKey(eventId)) {
				HashMap<String, Object> eventDetails = events.get(eventId);
				rl.lock();
				if (((Integer) eventDetails.get(Constants.CAPACITY)
						- (Integer) eventDetails.get(Constants.CUSTOMERS_ENROLLED)) > 0) {

					synchronized (this) {
						status = ((HashSet<String>) eventDetails.get(Constants.CUSTOMER_IDS)).add(customerId);
						if (status) {
							eventDetails.put(Constants.CUSTOMERS_ENROLLED,
									(Integer) eventDetails.get(Constants.CUSTOMERS_ENROLLED) + 1);
							status = true;
							msg = "Booking Successful.";
						} else {
							status = false;
							msg = customerId + " is already booked in " + eventId + ".";
						}
					}
				} else {
					status = false;
					msg = eventId + " is full.";
				}
				rl.unlock();
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

	public HashMap<String, ArrayList<String>> getBookingSchedule(String customerId) throws RemoteException {
		HashMap<String, ArrayList<String>> schedule = new HashMap<>();
		schedule.putAll(getEventScheduleThisServer(customerId));

		// inquire different Citys
		for (City c : City.values()) {
			if (c != this.City) {

				HashMap<String, ArrayList<String>> citySchedule = (HashMap<String, ArrayList<String>>) Utils
						.byteArrayToObject(udpCommunication(c, customerId, Constants.OP_GET_BOOKING_SCHEDULE));

				for (String eventType : citySchedule.keySet()) {
					if (schedule.containsKey(eventType)) {
						schedule.get(eventType).addAll(citySchedule.get(eventType));
					} else {
						schedule.put(eventType, citySchedule.get(eventType));
					}
				}
			}
		}
		LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_GET_BOOKING_SCHEDULE, Arrays.asList(customerId),
				schedule != null, schedule));
		return schedule;
	}



	private HashMap<String, ArrayList<String>> getEventScheduleThisServer(String customerId) {
		HashMap<String, ArrayList<String>> schedule = new HashMap<>();
		cityDatabase.forEach((eventType, events) -> {
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
		return schedule;
	}


	public boolean cancelEvent(String customerId, String eventId, String eventType) throws RemoteException {

		City eventCity = City.valueOf(eventId.substring(0, 3).toUpperCase());
		boolean result;
		if (this.City == eventCity) {
			result = dropEventOnThisServer(customerId, eventId);
		} else {
			HashMap<String, String> data = new HashMap<>();
			data.put(Constants.CUSTOMER_ID, customerId);
			data.put(Constants.EVENT_ID, eventId);
			data.put(Constants.EVENT_TYPE, eventType);
			result = (boolean) Utils
					.byteArrayToObject(udpCommunication(eventCity, data, Constants.OP_CANCEL_EVENT));
		}

		LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_CANCEL_EVENT, Arrays.asList(customerId, eventId, eventType), result
				));
		return result;
	}

	private boolean dropEventOnThisServer(String customerId, String eventId) {
		final Map<Boolean, String> temp = new HashMap<>();
		if (cityDatabase.size() > 0) {
			cityDatabase.forEach((eventType, events) -> {
				if (events.containsKey(eventId)) {
					events.forEach((event, eventDetails) -> {
						rl.lock();
							if (event.equals(eventId)) {
								boolean status = ((HashSet<String>) eventDetails.get(Constants.CUSTOMER_IDS))
										.remove(customerId);
								if (status) {
									eventDetails.put(Constants.CUSTOMERS_ENROLLED,
											((Integer) eventDetails.get(Constants.CUSTOMERS_ENROLLED) - 1));
									temp.put(true, "success");
								} else {
									temp.put(false, customerId + " isn't booked in " + eventId + ".");
								}
							}
							rl.unlock();
					});
				} else {
					temp.put(false, eventId + " isn't offered by the City yet.");
				}
			});
		} else {
			temp.put(false, eventId + " isn't offered by the City yet.");
		}

		if (temp.containsKey(true)) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remoteObject.EnrollmentInterface#swapCourse(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public SimpleEntry<Boolean, String> swapEvent(String customerId, String neweventId, String oldeventId,
			String newEventType, String oldEventType) throws RemoteException {
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
		} else if (neweventCity != this.City && oldeventCity == this.City && outOfCityevents.size() >= 2) {
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

			rl.lock(); // Acquire lock
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
			rl.unlock();
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

			result2 = (SimpleEntry<Boolean, String>) Utils
					.byteArrayToObject(udpCommunication(neweventCity, data, Constants.OP_SWAP_EVENT));
			status = result2.getKey();
			msg = result2.getValue();
		}

		LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_SWAP_EVENT,
				Arrays.asList(customerId, neweventId, oldeventId), status, msg));

		return new SimpleEntry<Boolean, String>(status, msg);
	}

	/**
	 * UDP Server for Inter-Department communication
	 */
	public void UDPServer() {
		UDPServer(City.getUdpPort());
	}

	public void UDPServer(int portNo) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(portNo);
			byte[] buffer = new byte[1000];// to stored the received data from the client.
			LOGGER.info(this.City + " UDP Server Started at port " + portNo + " ............");
			// non-terminating loop as the server is always in listening mode.
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				// Server waits for the request to come
				socket.receive(request); // request received

				byte[] response = processRequest(request, socket);
				if (response == null)
					continue; // will reply to Front end manually

				DatagramPacket reply = new DatagramPacket(response, response.length, request.getAddress(),
						request.getPort());// reply packet ready
				socket.send(reply);// reply sent
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

	private void processSequencerRequest(String receivedRequest, DatagramSocket socket) {

		String[] temp = receivedRequest.split("&", 2);
		// int sequenceNumber = Integer.parseInt(temp[0]);
		String request[] = temp[1].trim().split("&");
		byte[] response = new byte[1000];
		try {
			switch (request[0]) {

			case Constants.OP_ADD_EVENT:

				response = UDPUtilities
						.objectToByteArray(addEvent(request[1], request[2], request[3], Integer.parseInt(request[4])));

				break;

			case Constants.OP_REMOVE_EVENT:
				response = UDPUtilities.objectToByteArray(removeEvent(request[1], request[2], request[3]));
				break;

			case Constants.OP_LIST_EVENT_AVAILABILITY:
				response = UDPUtilities.objectToByteArray(listEventAvailability(request[1], request[2]));
				break;

			case Constants.OP_BOOK_EVENT:
				response = UDPUtilities.objectToByteArray(bookEvent(request[1], request[2], request[3]));
				break;

			case Constants.OP_GET_BOOKING_SCHEDULE:
				response = UDPUtilities.objectToByteArray(getBookingSchedule(request[1]));
				break;

			case Constants.OP_CANCEL_EVENT:
				response = UDPUtilities.objectToByteArray(cancelEvent(request[1], request[2], request[3]));
				break;

			case Constants.OP_SWAP_EVENT:
				response = UDPUtilities.objectToByteArray(swapEvent(request[1], request[2], request[3], request[4], request[5]));
				break;

			}
		} catch (NumberFormatException | RemoteException e) {
			e.printStackTrace();
		}

		try {

			InetAddress frontendIP = InetAddress.getByName(Config.getStringConfig("FRONTEND_IP"));
			DatagramPacket replyPacket = new DatagramPacket(response, response.length, frontendIP,
					Config.getConfig("FRONTEND_PORT"));
			socket.send(replyPacket);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Handles the UDP request for information
	 * 
	 * @param data
	 * @return
	 */
	@SuppressWarnings(value = { "unchecked" })
	private byte[] processUDPRequest(byte[] data) {

		byte[] response = null;
		HashMap<String, Object> request = (HashMap<String, Object>) Utils.byteArrayToObject(data);

		for (String key : request.keySet()) {

			LOGGER.info("Received UDP Socket call for method[" + key + "] with parameters[" + request.get(key) + "]");
			switch (key) {
			case Constants.OP_LIST_EVENT_AVAILABILITY:
				String eventType = (String) request.get(key);
				response = Utils.objectToByteArray(listEventAvailabilityForThisServer(eventType));
				break;
			case Constants.OP_BOOK_EVENT:
				HashMap<String, String> info = (HashMap<String, String>) request.get(key);
				response = Utils.objectToByteArray(enrollmentForThisCity(info.get(Constants.CUSTOMER_ID),
						info.get(Constants.EVENT_ID), info.get(Constants.EVENT_TYPE)));
				break;
			case Constants.OP_GET_BOOKING_SCHEDULE:
				String customerId = (String) request.get(key);
				response = Utils.objectToByteArray(getEventScheduleThisServer(customerId));
				break;
			case Constants.OP_CANCEL_EVENT:
				info = (HashMap<String, String>) request.get(key);
				response = Utils.objectToByteArray(
						dropEventOnThisServer(info.get(Constants.CUSTOMER_ID), info.get(Constants.EVENT_ID)));
				break;
			case Constants.OP_SWAP_EVENT:
				info = (HashMap<String, String>) request.get(key);
				response = Utils.objectToByteArray(atomicSwapOnThisServer(info.get(Constants.CUSTOMER_ID),
						info.get(Constants.NEW_EVENT_ID), info.get(Constants.OLD_EVENT_CITY),
						info.get(Constants.OLD_EVENT_CITY), info.get(Constants.EVENT_TYPE)));
				break;
			case Constants.OP_GETSTATE:
				response = getInternalState();
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
			default:
				LOGGER.info("UDP Requeset not understood");
				break;

			}
		}

		return response;
	}

	private SimpleEntry<Boolean, String> atomicSwapOnThisServer(String customerId, String neweventId,
			String oldeventId, String oldEventCity, String eventType) {

		SimpleEntry<Boolean, String> result;
		boolean status;
		String msg;

		try {
			// Acquire LOCK
			rl.lock();

			result = checkEventAvailability(neweventId, eventType);
			if (result.getKey()) {
				HashMap<String, String> data = new HashMap<>();
				data.put(Constants.CUSTOMER_ID, customerId);
				data.put(Constants.EVENT_ID, oldeventId);
				result = (SimpleEntry<Boolean, String>) Utils.byteArrayToObject(
						udpCommunication(City.valueOf(oldEventCity), data, Constants.OP_CANCEL_EVENT));

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
		} catch (RemoteException e) {
			status = false;
			msg = Constants.OP_SWAP_EVENT + " unsuccessful";
			return new SimpleEntry<Boolean, String>(status, msg);
		} finally {
			// Release the lock
			rl.unlock();
		}
	}

	private SimpleEntry<Boolean, String> checkEventAvailability(String eventId, String eventType) {

		boolean status = true;
		String msg = Constants.EMPTYSTRING;
		if (cityDatabase.containsKey(eventType)) {
			HashMap<String, HashMap<String, Object>> events = cityDatabase.get(eventType);

			if (events.containsKey(eventId)) {
				HashMap<String, Object> courseDetails = events.get(eventId);

				if (((Integer) courseDetails.get(Constants.CAPACITY)
						- (Integer) courseDetails.get(Constants.CUSTOMERS_ENROLLED)) > 0) {
					status = true;

				} else {
					status = false;
					msg = eventId + " is full.";
				}
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

	/**
	 * Creates & sends the UDP request
	 * 
	 * @param city
	 * @param info
	 * @param method
	 * @return
	 */
	private byte[] udpCommunication(City city, Object info, String method) {

		LOGGER.info("Making UPD Socket Call to " + city + " Server for method : " + method);

		// UDP SOCKET CALL AS CLIENT
		HashMap<String, Object> data = new HashMap<>();
		byte[] response = null;
		data.put(method, info);
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			byte[] message = Utils.objectToByteArray(data);
			InetAddress remoteUdpHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(message, message.length, remoteUdpHost, city.getUdpPort());
			socket.send(request);
			byte[] buffer = new byte[65556];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			socket.receive(reply);
			response = reply.getData();

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

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see server.instance3.remoteObject.EnrollmentInterface#getState()
	 */
	@Override
	public byte[] getInternalState() {
		return Utility.deepCopyInstance3State(cityDatabase);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see server.instance3.remoteObject.EnrollmentInterface#setState(byte[])
	 */
	@Override
	public void setState(HashMap<String, HashMap<String, HashMap<String, Object>>> data) {
		this.cityDatabase = data;		
	}
	/**
	 * Logging setup for COMP server
	 * 
	 * @throws IOException
	 */
	private static void setupLogging(String fileName) {
		try {
			File files = new File(String.format(Constants.SERVER_LOG_DIRECTORY,"Instance3"));
			if (!files.exists())
				files.mkdirs();
			files = new File(String.format(Constants.SERVER_LOG_DIRECTORY,"Instance3") + fileName);
			if (!files.exists())
				files.createNewFile();
			MyLogger.setup(files.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

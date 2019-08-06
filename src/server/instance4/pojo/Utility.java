package server.instance4.pojo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractMap.SimpleEntry;

public class Utility {

	public static SimpleEntry<Boolean, String> validateUser(final String id, final UserType userRole, final City city) {
		String c, role, value;
		if (id.length() != 8)
			return new SimpleEntry<Boolean, String>(false, "Seems to be an invalid id(length not equal to 9).");

		c = id.substring(0, 3).toUpperCase();
		role = id.substring(3, 4).toUpperCase();
		value = id.substring(4);

		// validate City
		if (!cityMatch(c))
			return new SimpleEntry<Boolean, String>(false, "The City('" + c + "') isn't recognized.");
		else if (city != null && city != City.valueOf(c))
			return new SimpleEntry<Boolean, String>(false, "You are not authorized for this city('" + c + "').");
		else if (!roleMatch(role))
			return new SimpleEntry<Boolean, String>(false, "The role('" + role + "') isn't correct.");
		else if (role != null && userRole != UserType.fromString(role)) {
			return new SimpleEntry<Boolean, String>(false, "This operation is invalid for a Manager('" + role + "').");
		}

		try {
			Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			return new SimpleEntry<Boolean, String>(false, "The id('" + value + "') isn't correct.");
		}

		return new SimpleEntry<Boolean, String>(true, "valid");
	}

	public static SimpleEntry<Boolean, String> validateEvent(final String eventId) {
		return validateEvent(eventId, null);

	}

	public static SimpleEntry<Boolean, String> validateEvent(final String eventId, City city) {

		if (eventId.length() != 10)
			return new SimpleEntry<Boolean, String>(false, "Seems to be an invalid event(length not equal to 10).");
		String c, time, value;

		c = eventId.substring(0, 3).toUpperCase();
		time = eventId.substring(3, 4).toUpperCase();
		value = eventId.substring(4);

		if (!Utility.cityMatch(c))
			return new SimpleEntry<Boolean, String>(false, "The city('" + c + "') isn't recognized.");
		else if (city != null && city != City.valueOf(c))
			return new SimpleEntry<Boolean, String>(false, "You are not authorized for this city('" + c + "').");
		try {
			Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			return new SimpleEntry<Boolean, String>(false, "Event id('" + value + "') isn't valid.");
		}

		return new SimpleEntry<Boolean, String>(true, "valid");
	}

	public static SimpleEntry<Boolean, String> validateEventType(String eventType) {
		boolean status = (EventType.isValidEventType(eventType) != null) ? true : false;
		String msg = null;
		if (!status)
			msg = eventType + " isn't valid eventType.";
		return new SimpleEntry<Boolean, String>(status, msg);
	}

	public static byte[] objectToByteArray(Object obj) {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(byteOut);
			out.writeObject(obj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return byteOut.toByteArray();
	}

	public static Object byteArrayToObject(byte[] data) {
		ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
		Object result = null;
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(byteIn);
			result = (Object) in.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}


	public static boolean cityMatch(final String c) {
		return c.matches("(?i)MTL|TOR|OTW");
	}

	public static boolean roleMatch(final String role) {
		return role.matches("(?i)M|C");
	}
}

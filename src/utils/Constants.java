package utils;

public class Constants {
	
	public static final String OP_HARDWARE_CRASH = "hardwareCrash";
	public static final String OP_SOFTWARE_CRASH = "softwareCrash";
	public static final String OP_ISALIVE = "isAlive";
	public static final String OP_GETSTATE = "getState";
	public static final String OP_SETSTATE = "setState";
	
	
	public static final String MANAGER_LOG_DIRECTORY = "./Logs/%s/ClientLogs/";
	public static final String CUSTOMER_LOG_DIRECTORY = "./Logs/%s/ClientLogs/";
	public static final String SERVER_LOG_DIRECTORY = "./Logs/%s/ServerLogs/";
	public static final String RM_LOG_DIRECTORY = "./Logs/RM/";
	public static final String UNDERSCORE = "_";
	public static final String EMPTYSTRING = "";

	public static final String CAPACITY = "capacity";
	public static final String CUSTOMERS_ENROLLED = "customersEnrolled";
	public static final String CUSTOMER_IDS = "customerIds";
	public static final String CUSTOMER_ID = "customerId";
	public static final String EVENT_ID = "eventId";
	public static final String EVENT_TYPE = "eventType";
	public static final String LOG_MSG = "METHOD[%s]; PARAMETERS%s; STATUS[%s]; SERVER_MESSAGE[%s]";
	public static final String OP_ADD_EVENT = "addEvent";
	public static final String OP_REMOVE_EVENT = "removeEvent";
	public static final String OP_LIST_EVENT_AVAILABILITY = "listEventAvailability";
	public static final String OP_BOOK_EVENT = "bookEvent";
	public static final String OP_GET_BOOKING_SCHEDULE = "getBookingSchedule";
	public static final String OP_CANCEL_EVENT = "cancelEvent";
	public static final String OP_SWAP_EVENT = "swapEvent";
	public static final String NEW_EVENT_ID = "newEventId";
	public static final String OLD_EVENT_ID = "oldEventId";
	public static final String OLD_EVENT_CITY = "oldEventCity";

	public static int MAX_CROSS_EVENTS = 3;

}

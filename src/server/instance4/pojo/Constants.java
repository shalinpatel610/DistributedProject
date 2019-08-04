package server.instance4.pojo;

public final class Constants {

	public static final String OP_HARDWARE_CRASH = "hardwareCrash";
	public static final String OP_ISALIVE = "isAlive";
	public static final String OP_GETSTATE = "getState";
	public static final String OP_SETSTATE = "setState";
	
	public static final String MANAGER_LOGS_DIRECTORY = "src/main/resources/logs/MANAGER/";
	public static final String CUSTOMER_LOGS_DIRECTORY = "src/main/resources/logs/CUSTOMER/";
	public static final String SERVER_LOGS_DIRECTORY = "src/main/resources/logs/server/";
	public static final String BASE_URL = "http://localhost:8080/";
	public static final String BASE_URL_MTL = "http://localhost:8080/";
	public static final String BASE_URL_OTW = "http://localhost:8181/";
	public static final String BASE_URL_TOR = "http://localhost:8282/";
	public static final String WSDL = "?wsdl";
	public static final String UNDERSCORE = "_";
	public static final String EMPTY_STRING = "";
	public static final String CAPACITY = "capacity";
	public static final String CUSTOMERS_ENROLLED = "CUSTOMERsEnrolled";
	public static final String CUSTOMER_IDS = "CUSTOMERIds";
	public static final String CUSTOMER_ID = "CUSTOMERId";
	public static final String EVENT_ID = "EVENTId";
	public static final String NEW_EVENT_ID = "newEVENTId";
	public static final String OLD_EVENT_ID = "oldEVENTId";
	public static final String OLD_EVENT_CITY = "oldEVENTCITY";
	public static final String EVENT_TYPE = "EVENT_TYPE";
	public static final String LOG_MSG = "METHOD[%s]; PARAMETERS%s; STATUS[%s]; SERVER_MESSAGE[%s]";
	public static final String OPERATION_ADD_EVENT = "addEVENT";
	public static final String OPERATION_REMOVE_EVENT = "removeEVENT";
	public static final String OPERATION_LIST_EVENT_AVAILABILITY = "listEVENTAvailability";
	public static final String OPERATION_BOOK_EVENT = "enrolEVENT";
	public static final String OPERATION_GET_BOOKING_SCHEDULE = "getBookingSchedule";
	public static final String OPERATION_CANCEL_EVENT = "cancelEvent";
	public static final String OPERATION_SWAP_EVENT = "swapEVENT";
	public static final int MAX_ELECTIVE_EVENTS = 2;
	public static final int MAX_EVENTS_TAKEN_BY_CUSTOMER = 3;
	public static final String MTL = "MTL";
	public static final String OTW = "OTW";
	public static final String TOR = "TOR";
}

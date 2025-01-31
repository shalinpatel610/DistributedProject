package utils.corbaInterface;



public interface IDEMSOperations 
{

  boolean addEvent (String managerId, String eventId, String eventType, int capacity);
  boolean removeEvent (String managerId, String eventId, String eventType);
  org.omg.CORBA.Any listEventAvailability (String managerId, String eventType);

  org.omg.CORBA.Any bookEvent (String customerId, String eventId, String eventType);
  org.omg.CORBA.Any getBookingSchedule (String customerId);
  boolean cancelEvent (String customerId, String eventId, String eventType);
  org.omg.CORBA.Any swapEvent (String customerId, String neweventId, String oldeventId, String newEventType, String oldEventType);
}

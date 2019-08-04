package server.instance1_1.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {

    private String eventID, eventType;
    private int bookingCapacity;
    private ArrayList<String> customerID;

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getBookingCapacity() {
        return bookingCapacity;
    }

    public void setBookingCapacity(int bookingCapacity) {
        this.bookingCapacity = bookingCapacity;
    }

    public ArrayList<String> getCustomerID() {
        return customerID;
    }

    public void setCustomerID(ArrayList<String> customerID) {
        this.customerID = customerID;
    }
}

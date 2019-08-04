package server.instance4.InterfaceImplementation;

import server.instance4.Model.Event;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MontrealInterface {

    private HashMap<String, HashMap<String, Event>> mtlDatabase = new HashMap<>();

    private int torontoServerPort = 8000;
    private int ottawaServerPort = 8001;

    public static final Logger LOGGER = Logger.getLogger("Servers");

    static {
        FileHandler fh;
        try {
            fh = new FileHandler("src/server/instance4/Logs/" + "MTL" + "LogFile.log");
            LOGGER.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MontrealInterface() {
        super();
        //mtlDatabase = new HashMap<>();
    }
    
    public boolean bookEvent(String customerID, String eventID, String eventType) {
        if (eventID.contains("TOR")){
            String result = connectServer(torontoServerPort,"book",customerID,eventID,eventType);
            return result.equals("true");
        }else if (eventID.contains("OTW")){
            String result = connectServer(ottawaServerPort,"book",customerID,eventID,eventType);
            return result.equals("true");
        } else {
            if (mtlDatabase.containsKey(eventType)){
                if (mtlDatabase.get(eventType).containsKey(eventID)) {
                    if (mtlDatabase.get(eventType).get(eventID).getBookingCapacity() > 0) {
                        mtlDatabase.get(eventType).get(eventID).setBookingCapacity(mtlDatabase.get(eventType).get(eventID).getBookingCapacity() - 1);
                        if (mtlDatabase.get(eventType).get(eventID).getCustomerID().size() > 0) {
                            ArrayList<String> cus = mtlDatabase.get(eventType).get(eventID).getCustomerID();
                            cus.add(customerID);
                            mtlDatabase.get(eventType).get(eventID).setCustomerID(cus);
                        } else {
                            ArrayList<String> cus = new ArrayList<>();
                            cus.add(customerID);
                            mtlDatabase.get(eventType).get(eventID).setCustomerID(cus);
                        }
                        LOGGER.log(Level.INFO, "Event booked successfully - Montreal Server - " + eventID + " - " + eventType + " - " + customerID);
                        return true;
                    } else if (mtlDatabase.get(eventType).get(eventID).getBookingCapacity() == 0) {
                        LOGGER.log(Level.INFO, "Event not booked - Montreal Server - Booking Capacity Reached - " + eventID + " - " + eventType + " - " + customerID);
                        return false;
                    }
                }else{
                    LOGGER.log(Level.INFO, "Event not booked/found - Montreal Server - " + eventID + " - " + eventType + " - " + customerID);
                    return false;
                }

            }
        }
        return false;
    }

    
    public boolean cancelEvent(String customerID, String eventID, String eventType) {
        if (eventID.contains("TOR")){
            String result = connectServer(torontoServerPort,"cancel",customerID,eventID,eventType);
            return result.equals("true");
        }else if (eventID.contains("OTW")){
            String result = connectServer(ottawaServerPort,"cancel",customerID,eventID,eventType);
            return result.equals("true");
        } else {
            if (mtlDatabase.containsKey(eventType)) {
                //System.out.println(mtlDatabase.get(eventType).get(eventID).getBookingCapacity());
                mtlDatabase.get(eventType).get(eventID).getCustomerID().remove(customerID);
                mtlDatabase.get(eventType).get(eventID).setBookingCapacity(mtlDatabase.get(eventType).get(eventID).getBookingCapacity() + 1);
                LOGGER.log(Level.INFO, "Event canceled successfully - Montreal Server - " + eventID + " - " + eventType + " - " + customerID);
                return true;
            } else {
                LOGGER.log(Level.INFO, "Event not canceled - Montreal Server - " + eventID + " - " + eventType + " - " + customerID);
                return false;
            }
        }
    }

    
    public String getBookingSchedule(String customerID) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object value : mtlDatabase.values()) {
            HashMap<String, Event> temp = (HashMap<String, Event>) value;
            for (Object ev : temp.values()) {
                Event event = (Event) ev;
                if (event.getCustomerID().contains(customerID)){
                    stringBuilder.append(event.getEventID() + " -- " + event.getEventType() + "\n");
                }
            }
        }

        stringBuilder.append(connectServer(torontoServerPort,"schedule",customerID,"random","random"));
        stringBuilder.append(connectServer(ottawaServerPort,"schedule",customerID,"random","random"));

        LOGGER.log(Level.INFO, "Booking schedule - Montreal Server" + stringBuilder.toString());

        return stringBuilder.toString();
    }

    
    public boolean addEvent(String managerID, String eventID, String eventType, int bookingCapacity) {
        if (mtlDatabase.containsKey(eventType)){
            if (mtlDatabase.get(eventType).containsKey(eventID)) {
                mtlDatabase.get(eventType).get(eventID).setBookingCapacity(mtlDatabase.get(eventType).get(eventID).getBookingCapacity() + bookingCapacity);
                LOGGER.log(Level.INFO, "Event added successfully - Montreal Server - " + eventID + " - " + eventType + " - " + bookingCapacity);
                return true;
            } else{
                if (eventID.contains("MTL")) {
                    HashMap<String, Event> inner = new HashMap<>();
                    Event event = new Event();
                    event.setEventID(eventID);
                    event.setEventType(eventType);
                    ArrayList<String> cus = new ArrayList<>();
                    event.setCustomerID(cus);
                    event.setBookingCapacity(bookingCapacity);
                    inner.put(eventID, event);
                    mtlDatabase.get(eventType).put(eventID, event);
                    LOGGER.log(Level.INFO, "Event added successfully - Montreal Server - " + eventID + " - " + eventType + " - " + bookingCapacity);
                    return true;
                }else{
                    LOGGER.log(Level.INFO, "Event not added - Montreal Server - " + eventID + " - " + eventType + " - " + bookingCapacity);
                    return false;
                }
            }
        } else {
            if (eventID.contains("MTL")) {
                HashMap<String, Event> inner = new HashMap<>();
                Event event = new Event();
                event.setEventID(eventID);
                event.setEventType(eventType);
                ArrayList<String> cus = new ArrayList<>();
                event.setCustomerID(cus);
                event.setBookingCapacity(bookingCapacity);
                inner.put(eventID, event);
                mtlDatabase.put(eventType, inner);
                LOGGER.log(Level.INFO, "Event added successfully - Montreal Server - " + eventID + " - " + eventType + " - " + bookingCapacity);
                return true;
            }else{
                LOGGER.log(Level.INFO, "Event not added - Montreal Server - " + eventID + " - " + eventType + " - " + bookingCapacity);
                return false;
            }
        }
    }

    
    public boolean removeEvent(String eventID, String eventType) {
        if (mtlDatabase.containsKey(eventType)){
            mtlDatabase.get(eventType).remove(eventID);
            LOGGER.log(Level.INFO, "Event removed successfully - Montreal Server - " + eventID + " - " + eventType);
            return true;
        }
        LOGGER.log(Level.INFO, "Event not removed - Montreal Server - " + eventID + " - " + eventType);
        return false;
    }

    
    public String listEventAvailability(String eventType) {
        ArrayList<String> output = new ArrayList<>();
        //System.out.println("size"+mtlDatabase.size());
        for (Map.Entry<String, HashMap<String, Event>> entry: mtlDatabase.entrySet()) {
            if (entry.getKey().equals(eventType)) {
                HashMap<String, Event> temp = (HashMap<String, Event>) entry.getValue();
                for (Object ev : temp.values()) {
                    Event event = (Event) ev;
                    output.add(event.getEventID() + " -- " + event.getBookingCapacity());
                }
            }
        }

        String result = connectServer(torontoServerPort, "availability", "random", "random", eventType);
        result += connectServer(ottawaServerPort, "availability", "random", "random", eventType);
        result += output.toString();

        LOGGER.log(Level.INFO, "Availability - Montreal Server - " + result);

        return result;
    }

    
    public boolean swapEvent(String customerID, String eventID, String eventType, String oldEventID, String oldEventType) {
        String resultSchedule = getBookingSchedule(customerID);
        if (resultSchedule.contains(oldEventID)){
            String resultAvailability = listEventAvailability(eventType);
            if (resultAvailability.contains(eventID)){
                if (eventID.contains("MTL")){
                    if (Integer.parseInt(countAvailability(eventType, eventID)) > 0){
                        System.out.println("Event has sufficient capacity - " + Integer.parseInt(countAvailability(eventType, eventID)));

                        if (bookEvent(customerID, eventID, eventType)){
                            System.out.println("Event Swapped Event Booked Successfully");
                            if (cancelEvent(customerID, oldEventID, oldEventType)){
                                System.out.println("Event Swapped Event Canceled Successfully");
                                return true;
                            } else {
                                System.out.println("Event Swapped Event Not Canceled Successfully, Cancel new booked event");
                                if (cancelEvent(customerID, eventID, eventType)){
                                    System.out.println("Event Swapped New Event Canceled Successfully");
                                }
                                return false;
                            }
                        } else{
                            System.out.println("Event Swapped Event Not Booked Successfully");
                            return false;
                        }

                    } else{
                        System.out.println("Event has sufficient capacity - " + Integer.parseInt(countAvailability(eventType, eventID)));
                        return false;
                    }
                } else if (eventID.contains("TOR")){
                    String resultAvailabilityCount = connectServer(torontoServerPort, "availabilityCount", "random", eventID, eventType);
                    if (Integer.parseInt(resultAvailabilityCount) > 0){
                        System.out.println("Event has sufficient capacity - " + Integer.parseInt(resultAvailabilityCount));

                        if (bookEvent(customerID, eventID, eventType)){
                            System.out.println("Event Swapped Event Booked Successfully");
                            if (cancelEvent(customerID, oldEventID, oldEventType)){
                                System.out.println("Event Swapped Event Canceled Successfully");
                                return true;
                            } else {
                                System.out.println("Event Swapped Event Not Canceled Successfully, Cancel new booked event");
                                if (cancelEvent(customerID, eventID, eventType)){
                                    System.out.println("Event Swapped New Event Canceled Successfully");
                                }
                                return false;
                            }
                        } else{
                            System.out.println("Event Swapped Event Not Booked Successfully");
                            return false;
                        }

                    } else{
                        System.out.println("Event have not sufficient capacity - " + Integer.parseInt(resultAvailabilityCount));
                        return false;
                    }
                } else if (eventID.contains("OTW")){
                    String resultAvailabilityCount = connectServer(ottawaServerPort, "availabilityCount", "random", eventID, eventType);
                    if (Integer.parseInt(resultAvailabilityCount) > 0){
                        System.out.println("Event has sufficient capacity - " + Integer.parseInt(resultAvailabilityCount));

                        if (bookEvent(customerID, eventID, eventType)){
                            System.out.println("Event Swapped Event Booked Successfully");
                            if (cancelEvent(customerID, oldEventID, oldEventType)){
                                System.out.println("Event Swapped Event Canceled Successfully");
                                return true;
                            } else {
                                System.out.println("Event Swapped Event Not Canceled Successfully, Cancel new booked event");
                                if (cancelEvent(customerID, eventID, eventType)){
                                    System.out.println("Event Swapped New Event Canceled Successfully");
                                }
                                return false;
                            }
                        } else {
                            System.out.println("Event Swapped Event Not Booked Successfully");
                            return false;
                        }

                    } else{
                        System.out.println("Event have not sufficient capacity - " + Integer.parseInt(resultAvailabilityCount));
                        return false;
                    }
                } else{
                    return false;
                }
            } else{
                System.out.println("New EventId is not in not available");
                return false;
            }
        } else {
            System.out.println("Customer Not Booked for Old Event");
            return false;
        }
    }

    public String countAvailability(String eventType, String eventID){
        //System.out.println("size"+mtlDatabase.size());
        for (Map.Entry<String, HashMap<String, Event>> entry: mtlDatabase.entrySet()) {
            if (entry.getKey().equals(eventType)) {
                HashMap<String, Event> temp = (HashMap<String, Event>) entry.getValue();
                for (Object ev : temp.values()) {
                    Event event = (Event) ev;
                    if (event.getEventID().equals(eventID)){
                        return String.valueOf(event.getBookingCapacity());
                    }
                }
            }
        }
        return "0";
    }

    private static String connectServer(int serverPort, String function, String customerID, String eventID, String eventType){
        DatagramSocket aSocket = null;
        String result ="";
        String dataFromClient = function+"&"+customerID+"&"+eventID+"&"+eventType;
        try {
            aSocket = new DatagramSocket();
            byte[] message = dataFromClient.getBytes();
            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(message, dataFromClient.length(), aHost, serverPort);
            aSocket.send(request);

            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            aSocket.receive(reply);
            result = new String(reply.getData());
            String[] parts = result.split("&");
            result = parts[0];
        } catch (Exception e) {
            System.out.println("Socket: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
        return result;
    }

    public int count(String customerID, String eventID){
        int total = 0;
        String last = eventID.substring(eventID.length() - 4);
        //System.out.println("Last"+last);
        for (Object value : mtlDatabase.values()) {
            HashMap<String, Event> temp = (HashMap<String, Event>) value;
            for (Object ev : temp.values()) {
                Event event = (Event) ev;
                //System.out.println("Last event"+event.getEventID().substring(event.getEventID().length() - 4));
                if (event.getEventID().substring(event.getEventID().length() - 4).equals(last)) {
                    if (event.getCustomerID().contains(customerID)) {
                        total += 1;
                    }
                }
            }
        }
        return total;
    }

    public boolean book(String customerID, String eventID, String eventType) {
        int total = 0;
        if (customerID.contains("TOR")){
            total += Integer.parseInt(connectServer(ottawaServerPort,"count", customerID, eventID,"random"));
        } else if (customerID.contains("OTW")){
            total += Integer.parseInt(connectServer(torontoServerPort,"count", customerID, eventID,"random"));
        }

        total += count(customerID, eventID);
        //System.out.println("total"+total);

        if (total < 3) {

            if (mtlDatabase.containsKey(eventType)) {
                //System.out.println(mtlDatabase.get(eventType).get(eventID).getBookingCapacity());
                if (mtlDatabase.get(eventType).get(eventID).getBookingCapacity() > 0) {
                    mtlDatabase.get(eventType).get(eventID).setBookingCapacity(mtlDatabase.get(eventType).get(eventID).getBookingCapacity() - 1);
                    if (mtlDatabase.get(eventType).get(eventID).getCustomerID().size() > 0) {
                        ArrayList<String> cus = mtlDatabase.get(eventType).get(eventID).getCustomerID();
                        cus.add(customerID);
                        mtlDatabase.get(eventType).get(eventID).setCustomerID(cus);
                    } else {
                        ArrayList<String> cus = new ArrayList<>();
                        cus.add(customerID);
                        mtlDatabase.get(eventType).get(eventID).setCustomerID(cus);
                    }
                    return true;
                } else if (mtlDatabase.get(eventType).get(eventID).getBookingCapacity() == 0){
                    LOGGER.log(Level.INFO, "Event not booked - Montreal Server - Booking Capacity Reached - " + eventID + " - " + eventType + " - " + customerID);
                    return false;
                } else {
                    return false;
                }

            } else{
                return false;
            }

        } else {
            LOGGER.log(Level.INFO, "Cannot book event - Montreal Server - Number of allowed outside booking done");
            return false;
        }
    }

    public boolean cancel(String customerID, String eventID, String eventType) {
        if (mtlDatabase.containsKey(eventType)) {
            //System.out.println(mtlDatabase.get(eventType).get(eventID).getBookingCapacity());
            mtlDatabase.get(eventType).get(eventID).getCustomerID().remove(customerID);
            mtlDatabase.get(eventType).get(eventID).setBookingCapacity(mtlDatabase.get(eventType).get(eventID).getBookingCapacity() + 1);
            return true;
        } else {
            return false;
        }
    }

    public String schedule(String customerID) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object value : mtlDatabase.values()) {
            HashMap<String, Event> temp = (HashMap<String, Event>) value;
            for (Object ev : temp.values()) {
                Event event = (Event) ev;
                if (event.getCustomerID().contains(customerID)){
                    stringBuilder.append(event.getEventID() + " -- " + event.getEventType() + "\n");
                }
            }
        }

        return stringBuilder.toString();
    }

    public String availability(String eventType) {
        ArrayList<String> output = new ArrayList<>();
        for (Map.Entry<String, HashMap<String, Event>> entry: mtlDatabase.entrySet()) {
            if (entry.getKey().equals(eventType)) {
                HashMap<String, Event> temp = (HashMap<String, Event>) entry.getValue();
                for (Object ev : temp.values()) {
                    Event event = (Event) ev;
                    output.add(event.getEventID() + " -- " + event.getBookingCapacity());
                }
            }
        }
        return output.toString();
    }

}

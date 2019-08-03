package server.instance1.Server;

import server.instance1.InterfaceImplementation.TorontoInterface;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class TorontoServer {

    public static TorontoInterface torontoInterface;

    public static void main(String[] args) throws Exception {

        try {
            System.out.println("Toronto Server Started...");
            torontoInterface = new TorontoInterface();

            System.out.println("Toronto Server ready and waiting ...");
            Runnable task = () -> {
                receive(torontoInterface);
            };
            Thread thread = new Thread(task);
            thread.start();

        }

        catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("Toronto Server Exiting ...");
    }

    public static void receive(TorontoInterface obj) {
        DatagramSocket aSocket = null;
        String sendingResult = "";
        try {
            aSocket = new DatagramSocket(2002);
            byte[] buffer = new byte[1000];
            System.out.println("Toronto Server Started - 2002");
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String sentence = new String( request.getData(), 0,
                        request.getLength() );
                String[] parts = sentence.split(";");
                String function = parts[0];
                String customerID = parts[1];
                String eventID = parts[2];
                String eventType = parts[3];
                if (function.equals("book")){
                    boolean result = obj.book(customerID, eventID, eventType);
                    sendingResult = Boolean.toString(result);
                    sendingResult= sendingResult+";";
                }else if (function.equals("cancel")){
                    boolean result = obj.cancel(customerID, eventID, eventType);
                    sendingResult = Boolean.toString(result);
                    sendingResult= sendingResult+";";
                } else if (function.equals("schedule")){
                    String result = obj.schedule(customerID);
                    sendingResult = result;
                    sendingResult= sendingResult+";";
                } else if (function.equals("availability")){
                    String result = obj.availability(eventType);
                    sendingResult = result;
                    sendingResult= sendingResult+";";
                } else if (function.equals("availabilityCount")){
                    String result = obj.countAvailability(eventType, eventID);
                    sendingResult = result;
                    sendingResult= sendingResult+";";
                } else if (function.equals("count")){
                    int result = obj.count(customerID, eventID);
                    sendingResult = String.valueOf(result);
                    sendingResult= sendingResult+";";
                }
                byte[] sendData = sendingResult.getBytes();
                DatagramPacket reply = new DatagramPacket(sendData, sendingResult.length(), request.getAddress(),
                        request.getPort());
                aSocket.send(reply);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Socket: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }

}

package server.instance4;

import server.instance4.InterfaceImplementation.MontrealInterface;
import server.instance4.InterfaceImplementation.OttawaInterface;
import server.instance4.InterfaceImplementation.TorontoInterface;
import server.instance4.Server.MontrealServer;
import server.instance4.Server.OttawaServer;
import server.instance4.Server.TorontoServer;

public class Instance4Server {

    public static MontrealInterface montrealInterface;
    public static OttawaInterface ottawaInterface;
    public static TorontoInterface torontoInterface;

    public static void main(String[] args) {

        try {

            MontrealServer.main(null);
            OttawaServer.main(null);
            TorontoServer.main(null);

            //new Thread(() -> { (new MontrealServer()).receive(montrealInterface); }).start();
            //new Thread(() -> { (new OttawaServer()).receive(ottawaInterface); }).start();
            //new Thread(() -> { (new TorontoServer()).receive(torontoInterface); }).start();

            System.out.println("Insance 4 Server initated");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

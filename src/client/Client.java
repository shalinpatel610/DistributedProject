package client;

import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import client.controller.Controller;
import client.controller.data.Cache;
import utils.corbaInterface.IDEMS;
import utils.corbaInterface.IDEMSHelper;

public class Client {	

	public static void main(String[] args) throws InvalidName, NotFound, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName {
		
		Cache cache = Cache.getInstance();
		
		System.out.print("Enter Customer/Manager Id: ");
		cache.id = new Scanner(System.in).nextLine().toUpperCase();
		
		if(!cache.checkValidId())
			return;
		
		ORB orb = ORB.init(args, null);
	    org.omg.CORBA.Object objRef =   orb.resolve_initial_references("NameService");
	    NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	    cache.dems = (IDEMS) IDEMSHelper.narrow(ncRef.resolve_str(cache.getProvince()));
		
		promptUserAction();
		
	}
	
	private static void promptUserAction() {
		
		System.out.println("");
		Cache cache = Cache.getInstance();
		int input;
		
		do {
			
			if(cache.getClientType() == Cache.ClientType.MANAGER) {
				System.out.println("1. Add EVENT");
				System.out.println("2. Remove EVENT");
				System.out.println("3. List EVENT Availability");
				
				System.out.println("\nStudent Actions");
				System.out.println("4. Book an EVENT");
				System.out.println("5. Get EVENT Schedule");
				System.out.println("6. Cancel an EVENT");
				System.out.println("7. Swap EVENT");
			}
			
			else {
				System.out.println("4. Book an EVENT");
				System.out.println("5. Get EVENT Schedule");
				System.out.println("6. Cancel an EVENT");
				System.out.println("7. Swap EVENT");
			}
			
			System.out.println("8. Exit");
			System.out.print("\nSelect a action: ");
			
			input = new Scanner(System.in).nextInt();
			performAction(input);
			
			System.out.println("-----------------------------------------------------------------------------------------");
		} while(input != 8);
	}
	
	private static void performAction(int action) {
		
		Cache cache = Cache.getInstance();
		
		switch(action) {
		
			case 1:
				Controller.addEvent();
				break;
			
			case 2:	
				Controller.removeEvent();
				break;
				
			case 3:
				Controller.listEventAvailability();
				break;
				
			case 4:
				Controller.bookEvent();
				break;
				
			case 5:
				Controller.getBookingSchedule();
				break;

			case 6:
				Controller.cancelEvent();
				break;

			case 7:
				Controller.swapEvent();
				break;
		}

	}

}
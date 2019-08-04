package test;

import java.util.AbstractMap.SimpleEntry;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import client.controller.data.Cache;
import utils.corbaInterface.IDEMS;
import utils.corbaInterface.IDEMSHelper;


public class MultiThread {

	public static void main(String[] args) throws InvalidName, NotFound, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName {

		Cache cache = Cache.getInstance();
		cache.id = "MTLM1234";
		if(!cache.checkValidId())
			return;
	
		ORB orb = ORB.init(args, null);
	    org.omg.CORBA.Object objRef =   orb.resolve_initial_references("NameService");
	    NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	    cache.dems = (IDEMS) IDEMSHelper.narrow(ncRef.resolve_str(cache.getProvince()));
	    cache.dems.addEvent(cache.id, "MTLA123456", "SEMINAR", 2);
	    cache.dems.addEvent(cache.id, "MTLE123456", "SEMINAR", 2);
	    cache.dems.addEvent(cache.id, "MTLA123456", "CONFERENCES", 2);
	    cache.dems.addEvent(cache.id, "MTLA123456", "TRADESHOW", 2);
		Runnable task1 = () -> {
			Any any = cache.dems.bookEvent("MTLC1234", "MTLA123456", "SEMINAR");
			SimpleEntry<Boolean, String> result = (SimpleEntry<Boolean, String>) any.extract_Value();
			System.out.println("Enrolment result: " + result);
		};
		Runnable task2 = () -> {
			Any any = cache.dems.bookEvent("MTLC2345", "MTLA123456", "SEMINAR");
			SimpleEntry<Boolean, String> result = (SimpleEntry<Boolean, String>) any.extract_Value();
			System.out.println("Enrolment result: " + result);
		};

		Runnable task3 = () -> {
			Any any = cache.dems.bookEvent("MTLC3456", "MTLA123456", "SEMINAR");
			SimpleEntry<Boolean, String> result = (SimpleEntry<Boolean, String>) any.extract_Value();
			System.out.println("Enrolment result: " + result);
		};

		Runnable task4 = () -> {
			Any any = cache.dems.swapEvent("MTLC3456", "MTLA123456", "MTLE123456", "SEMINAR", "SEMINAR");
			SimpleEntry<Boolean, String> result = (SimpleEntry<Boolean, String>) any.extract_Value();
			System.out.println("Swapcourse result: " + result);
		};
			
		task1.run();
		task2.run();
		task3.run();
		task4.run();
			
	  
	    
	}
}

package utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;


public class Utility {

	
	public static byte[] deepCopyQueue(Queue<Integer> queue){
		
		Queue<Integer> copy = new PriorityQueue<>();
		for(int elem : queue) {
			copy.add(elem);
		}
		return UDPUtilities.objectToByteArray(copy);
	}

	/**
	 * @param cityDatabase
	 * @return
	 */
	public static byte[] deepCopyInstance3State(HashMap<String, HashMap<String, HashMap<String, Object>>> cityDatabase) {
		
		
		Map<String, HashMap<String, HashMap<String, Object>>> copy = new HashMap<>();
		HashMap<String,HashMap<String,Object>> newValue1 = new HashMap<>();
		
		for(String key1 : cityDatabase.keySet()) {
			HashMap<String,HashMap<String,Object>> value1 = cityDatabase.get(key1);
			for(String key2 : value1.keySet()) {
				
				HashMap<String,Object> value2 = value1.get(key2);
				HashMap<String,Object> newValue2 = new HashMap<>();
				newValue2.put(Constants.CAPACITY, value2.get(Constants.CAPACITY));
				newValue2.put(Constants.CUSTOMERS_ENROLLED, value2.get(Constants.CUSTOMERS_ENROLLED));
				HashSet<String> set = new HashSet<>();
				for(String value : (HashSet<String>)value2.get(Constants.CUSTOMER_IDS)) {
					set.add(new String(value));
				}
				newValue2.put(Constants.CUSTOMER_IDS,set);
				newValue1.put(new String(key2), newValue2);
			}
			copy.put(new String(key1), newValue1);
			
		}
		
		return UDPUtilities.objectToByteArray(copy);
	}
	
	

	/**
	 * @param cityDatabase
	 * @return
	 */
	public static byte[] deepCopyInstance4State(HashMap<String, HashMap<String, HashMap<String, Object>>> cityDatabase) {
		return deepCopyInstance3State(cityDatabase);
	}
	
	
	public static int getInstanceNumber(String name) {
		return Integer.parseInt(name.substring(8, 9));
	}
}

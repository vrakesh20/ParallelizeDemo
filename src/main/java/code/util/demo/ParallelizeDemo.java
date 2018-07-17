package code.util.demo;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class ItemToIntegrate {

	private String itemName;

	public ItemToIntegrate(String itemName) {
		this.itemName = itemName;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

}

public class ParallelizeDemo {
	
private static long APIResponseTime = 1; // API Response time in milli seconds
	
	private static int sourceSystemRecordsCount = 20000;
	
	private static HashMap<String, String> map =  new HashMap<String, String>();
	
	public static void updateMap(String threadName, String dataProcessed) {
		String currentValue = map.get(threadName);
		if( currentValue == null) {
			map.put(threadName, dataProcessed);
		}
		else
		{
			map.put(threadName, currentValue+","+dataProcessed);
		}
	}
	
	public static void printMap()
	{
		Map<String, String> sortedMap = new TreeMap<String, String>(map);
		sortedMap.forEach((key,value) -> System.out.println(key + "\t\t\t: " + value));
	}

	public static ArrayList<ItemToIntegrate> fetchDataFromSourceSystem() {
		// replace this code with the logic to pull out information from Source System
		ArrayList<ItemToIntegrate> list = new ArrayList<ItemToIntegrate>();
		for (int i = 1; i <= sourceSystemRecordsCount; i++) {
			list.add(new ItemToIntegrate(("Item" + i)));
		}
		return list;
	}

	public static void doExternalAPICall(String threadName, int currentPositionInThread, ItemToIntegrate item) {
		
		try {
			// Here implement your custom logic like post to Coupa or any other external application
			// mimicing time taken for network/API call using Thread.sleep() function
			Thread.sleep(APIResponseTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void doParallelProcess(final ArrayList<ItemToIntegrate> itemsToIntegrate, int numThreads, int stepSize) {
		map.clear();
		long startTime = System.currentTimeMillis();
		System.out.println("Test Description\t\t: " + stepSize + " as step size & " + numThreads + " Threads");
		try {
			
			ExecutorService service = Executors.newFixedThreadPool(numThreads);
			
			for (int i = 0; i < itemsToIntegrate.size(); i += stepSize) {
				final int from = i;
				final int to = from + stepSize;
				// System.out.println("i is " + i);
				service.submit(new Callable<String>() {
					public String call() {
						for (int j = from; j < to; j++) {
							// System.out.println("(i,j)=(" + Thread.currentThread().getId() + "," + j + ")");
							if(j==from)
							{
								// System.out.println("Thread " + Thread.currentThread().getName() + " is processing items from " + from + " to " + to);
								updateMap(Thread.currentThread().getName(),"{"+from + " " + to+"}");
							}
							ItemToIntegrate item = itemsToIntegrate.get(j);
							doExternalAPICall(Thread.currentThread().getName(), j, item);
						}
						return "SUCCESS";
					}
				});
			}

			service.shutdown();
			while (!service.isTerminated()) {
				// System.out.println("Waiting for threads to complete");
				service.awaitTermination(10, TimeUnit.SECONDS);
			}

			// System.out.println("Executing All Threads Complete");
		} catch (Exception e2) {
		}
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    System.out.println("Time Taken\t\t\t: " + elapsedTime + " milli seconds");
	    printMap();
	    System.out.println();
	}

	public static void main(String[] args) {
		
		ArrayList<ItemToIntegrate> itemsToIntegrate = fetchDataFromSourceSystem();
		int stepSize = 1000;
		int cpuCount = Runtime.getRuntime().availableProcessors(); // you can reduce this value if required
		
		System.out.println("Items in Source System\t\t: " + itemsToIntegrate.size());
		System.out.println("Target System APIResponseTime\t: " + APIResponseTime + " milli seconds");
		System.out.println("Number of CPUs\t\t\t: " + cpuCount);
		System.out.println();
		
		doParallelProcess(itemsToIntegrate, 1, 1000);
		doParallelProcess(itemsToIntegrate, 2, 1000);
		doParallelProcess(itemsToIntegrate, 4, 1000);
		doParallelProcess(itemsToIntegrate, 8, 1000);
	}

}

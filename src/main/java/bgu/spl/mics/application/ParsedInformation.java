/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import java.util.ArrayList;

/**
 * A final class used to store initial store services, resources and inventory information.
 * Parsed JSON information will be stored in this class and will be retrieved using its getter methods.
 */
final class ParsedInformation {

	/**
	 * Retrieves needed amount of services to run concurrently.
	 * @return the amount of services as parsed from JSON.
	 */
	int getServiceCount()
    {
    	final int timeService = 1;
    	return timeService+services.getSelling()+services.getInventoryService()+
    			services.getLogistics()+services.getResourcesService()+services.getCustomers().size();
    }

    //Following classes, methods and fields are formatted as in JSON file structure in order to store information.
	private BookInventoryInfo[] initialInventory;
	private ArrayList<InitialResources> initialResources;
	private InitialServicesParameters services;

	public class InitialResources {
		private DeliveryVehicle[] vehicles;

		DeliveryVehicle[] getVehicles()
		{
			return vehicles;
		}
	}

	BookInventoryInfo[] getInitialInventory() {
		return initialInventory;
	}

	ArrayList<InitialResources> getInitialResources() {
		return initialResources;
	}

	InitialServicesParameters getServices() {
		return services;
	}

    class InitialServicesParameters {
    	private  TimeServiceArguments time;
		private int selling;
    	private int inventoryService;
    	private int logistics;
    	private int resourcesService;
        private ArrayList<Customer> customers;

    	class TimeServiceArguments{
    		private int speed;
    		private int duration;

			int getSpeed() {
				return speed;
			}

			int getDuration() {
				return duration;
			}
    	}

    	TimeServiceArguments getTime() {
			return time;
		}

		int getSelling() {
			return selling;
		}

		int getInventoryService() {
			return inventoryService;
		}

		int getLogistics() {
			return logistics;
		}

		int getResourcesService() {
			return resourcesService;
		}

		ArrayList<Customer> getCustomers() {
			return customers;
		}
    }
}
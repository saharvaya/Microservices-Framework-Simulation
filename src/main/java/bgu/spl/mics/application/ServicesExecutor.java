/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.services.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class with the goal of initializing services and objects,
 * execute all services and save object serialized files when all working service threads are terminated.
 */
class ServicesExecutor {

    //Fields
    private ExecutorService threadPool; //Responsible of executing all services threads
    private ParsedInformation info; // Reference to deserialized initial JSON information

    ServicesExecutor(ParsedInformation initialInformation)
    {
        this.info = initialInformation;
    }

    /**
     * Executes program by initializing objects and services until all threads finish running
     */
    void execute()
    {
        initializeBookInventory(); // Initialize inventory
		initializeResources(); //Initialize resources
        executeMicroServices(initializeServices(), new TimeService(info.getServices().getTime().getSpeed(), info.getServices().getTime().getDuration(), info.getServiceCount())); //Initialize and execute all services
        // Terminate all services gracefully after they finish their tasks
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }
    }

    /**
     * Initializes the resource holder with all vehicles available
     */
    private void initializeResources() {
        ResourcesHolder.getInstance().load(info.getInitialResources().get(0).getVehicles());
    }

    /**
     * Initialized Book Info Inventory with all books available
     */
    private void initializeBookInventory() {
        Inventory.getInstance().load(info.getInitialInventory());
    }

    /**
     * Initialized thread pool and constructs all needed services for current session.
     * @return a list of all services to be initialized and executed.
     */
    private List<MicroService> initializeServices()
    {
        // Start a new executor service fixed thread pool in the size of all needed running services in current session.
        threadPool = Executors.newFixedThreadPool(info.getServiceCount());
        List<MicroService> activeServices = new ArrayList<>();

        //Construct and add to the active service list all needed services
        for(int i=1; i<=info.getServices().getSelling(); i++)
            activeServices.add(new SellingService(i));
        for(int i=1; i<=info.getServices().getInventoryService(); i++)
            activeServices.add(new InventoryService(i));
        for(int i=1; i<=info.getServices().getResourcesService(); i++)
            activeServices.add(new ResourceService(i));
        for(int i=1; i<=info.getServices().getLogistics(); i++)
            activeServices.add(new LogisticsService(i));

        for(Customer customer :info.getServices().getCustomers())
            activeServices.add(new APIService(customer, customer.getOrderSchedule()));
        return activeServices;
    }

    private void executeMicroServices(List<MicroService> services, TimeService timeService) {
        //Execute time service first, timer will start only after all services have been initialized using broadcasts to notify the timer for each service initialization.
        threadPool.execute(timeService);
        while(!timeService.isInitialized()) {
            synchronized(timeService.getTimerLOCK()) {
                try {
                    //Wait for time service to get initialized
                    timeService.getTimerLOCK().wait();
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //Execute rest of the system services.
        for (MicroService service : services) {
            threadPool.execute(service);
        }
    }

    /**
     * Creates serialized output files in given argument paths.
     * @param customerMapFilePath path to print customer HashMap file to
     * @param bookInventoryMapFilePath path to print book info list file to
     * @param receiptsListFilePath path to print register receipts list file to
     * @param moneyRegisterFilePath path to print money register object file to
     */
    void createOutputFiles(String customerMapFilePath, String bookInventoryMapFilePath, String receiptsListFilePath, String moneyRegisterFilePath) {
        //Create customers HashMap and print to serialized file
        HashMap<Integer, Customer> customersMap = new HashMap<>();
        for(Customer customer : info.getServices().getCustomers())
        {
            customersMap.put(customer.getId(), customer);
        }
        printObjectToFile(customerMapFilePath, customersMap);
        //Print serialized money register receipts list to file
        MoneyRegister.getInstance().printOrderReceipts(receiptsListFilePath);
        //Print current inventory list to serialized file
        Inventory.getInstance().printInventoryToFile(bookInventoryMapFilePath);
        //Print  serialized money register object to file
        printObjectToFile(moneyRegisterFilePath, MoneyRegister.getInstance());
    }

    /**
     * Helper method to print serialized objects to file
     * @param filename path to save serialized object in
     * @param obj object to serialize
     */
    private void printObjectToFile(String filename, Object obj){
        try{
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(obj);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            System.out.println("Failed to save serialized object");
            i.printStackTrace();
        }
    }
}

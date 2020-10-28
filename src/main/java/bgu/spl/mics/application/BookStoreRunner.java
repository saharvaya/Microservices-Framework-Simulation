package bgu.spl.mics.application;

import java.io.FileNotFoundException;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {

	public static void main(String[] args) {
        String jsonConfigurationFile = args[0]; // args[0] is the JSON file path
        JsonParser jsonParser = new JsonParser();
        try {
            ServicesExecutor executor = new ServicesExecutor(jsonParser.parse(jsonConfigurationFile)); // Send parsed JSON services and inventory information to Services executor
            executor.execute();
            executor.createOutputFiles(args[1], args[2], args[3], args[4]); // Once the program completed running, output requested serialized files on given argument paths.
        }
        catch(FileNotFoundException e)
        {
            System.out.println("JSON file not found");
        }
    }
}

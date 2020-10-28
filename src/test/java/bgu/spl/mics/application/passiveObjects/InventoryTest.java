/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.passiveObjects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Random;

import static org.junit.Assert.*;

public class InventoryTest {

    private Inventory inventory;

    @Before
    public void setUp() throws Exception {
        inventory = Inventory.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        BookInventoryInfo[] resetInventory = new BookInventoryInfo[0];
        inventory.load(resetInventory);
    }

    @Test
    public void getInstance() {
        assertNotNull("Get Inventory singleton instance should not result in null.", Inventory.getInstance());
    }

    @Test
    public void load() {
        //Initialize empty books info array
        BookInventoryInfo[] testBooks = new BookInventoryInfo[10];
        inventory.load(testBooks);
        assertEquals(OrderResult.NOT_IN_STOCK, inventory.take("Unavailable Test Book")); //Loaded empty book array, should result in NOT_IN STOCK

        //Initialize test books info
        boolean failed_to_take_loaded_book = false;
        Random rand = new Random();
        for(int i =0; i<testBooks.length; i++)
        {
            testBooks[i] = new BookInventoryInfo("Test Book "+i, rand.nextInt(100), i);
        }
        inventory.load(testBooks);
        //Since loaded array of initialized books should return SUCCESSFULLY_TAKEN for each book loaded to inventory.
        for(BookInventoryInfo testBook : testBooks)
        {
            if(inventory.take(testBook.getBookTitle()) != OrderResult.SUCCESSFULLY_TAKEN)
            {
                failed_to_take_loaded_book = true;
                break;
            }
        }
        assertFalse(failed_to_take_loaded_book);
    }

    @Test
    public void take() {
        //Initialize empty books info array
        BookInventoryInfo[] testBooks = new BookInventoryInfo[10];
        inventory.load(testBooks);
        assertEquals(OrderResult.NOT_IN_STOCK, inventory.take("Unavailable Test Book")); //Try to take a book that does not exist, should result in NOT_IN STOCK

        //Initialize test books info and amount to load from each book
        int book_amount_to_load = 2;
        boolean failed_to_take_loaded_book = false;
        for(int i =0; i<testBooks.length; i++)
        {
            testBooks[i] = new BookInventoryInfo("Test Book "+i,book_amount_to_load, i);
        }
        inventory.load(testBooks);

        //Try to take unavailable book after inventory is loaded
        assertEquals(OrderResult.NOT_IN_STOCK, inventory.take("Unavailable Test Book"));

        //For each book try to take the loaded amount of that book, should return SUCCESSFULLY_TAKEN for each book in the amount loaded to inventory.
        for(int i=0; i<book_amount_to_load; i++) {
            for (BookInventoryInfo testBook : testBooks) {
                if (inventory.take(testBook.getBookTitle()) != OrderResult.SUCCESSFULLY_TAKEN) {
                    failed_to_take_loaded_book = true;
                    break;
                }
            }
        }
        assertFalse(failed_to_take_loaded_book);


        //Book inventory should be empty, all the book from the amount loaded were taken, should return NOT_IN_STOCK for each book in the amount loaded to inventory.
        int unavailable_book_amount = 0;
        for (BookInventoryInfo testBook : testBooks) {
            if (inventory.take(testBook.getBookTitle()) != OrderResult.SUCCESSFULLY_TAKEN) {
                unavailable_book_amount++;
            }
        }
        assertEquals(testBooks.length, unavailable_book_amount);
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        //Initialize empty books info array
        BookInventoryInfo[] testBooks = new BookInventoryInfo[10];
        inventory.load(testBooks);
        assertEquals(-1, inventory.checkAvailabiltyAndGetPrice("Unavailable Test Book")); // No books in the inventory should return -1 since book is not available

        //Initialize test books info and amount to load from each book
        for(int i =0; i<testBooks.length; i++)
        {
            testBooks[i] = new BookInventoryInfo("Test Book "+i,1, i); //Each book is priced ad its i index
        }
        inventory.load(testBooks);

        //Check each book loaded returns its matching price and is available.
        int curr_price = 0;
        boolean price_match = true;
        for (BookInventoryInfo testBook : testBooks) {
            if (inventory.checkAvailabiltyAndGetPrice(testBook.getBookTitle()) != curr_price) {
                price_match = false;
                break;
            }
            curr_price++;
        }
        assertTrue(price_match);

        //Take all books from inventory and check they are now unavailable
        for (BookInventoryInfo testBook : testBooks) {
            inventory.take(testBook.getBookTitle());
            int bookPrice = inventory.checkAvailabiltyAndGetPrice(testBook.getBookTitle());
            assertEquals(-1, bookPrice);
        }
    }

    @Test
    public void printInventoryToFile() {
        String test_filename = "test_inventory_print";
        //Load book inventory and print inventory to file.
        BookInventoryInfo[] testBooks = new BookInventoryInfo[10];
        Random rand = new Random();
        for(int i =0; i<testBooks.length; i++)
        {
            testBooks[i] = new BookInventoryInfo("Test Book "+i,rand.nextInt(100), i);
        }
        inventory.load(testBooks);
        inventory.printInventoryToFile(test_filename);

        //Read printed inventory HashMap from file,
        boolean failed_to_read_file = false;
        HashMap<String, Integer> TestMap = null;
        try
        {
            FileInputStream fis = new FileInputStream(test_filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            TestMap = (HashMap<String, Integer>) ois.readObject();
            ois.close();
            fis.close();
        }catch(Exception e)
        {
            failed_to_read_file = true;
            return;
        }
        assertFalse(failed_to_read_file); // Assert no exception caught during reading the file.
        assertNotNull(TestMap); // Assert the current test map read from file and is not null.

        //Check read HashMap correctly from file
        for(BookInventoryInfo testBook : testBooks){
            boolean check_same = false;
           if(TestMap.containsKey(testBook.getBookTitle()) && TestMap.containsValue(testBook.getAmountInInventory()))
           {
               check_same = true;
           }
           assertTrue(check_same);
        }
    }
}
/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {

    Future<DeliveryVehicle> deliveryVehicleFuture;
    Future<Integer> integerFuture;
    Future<Boolean> booleanFuture;
    Future<String> stringFuture;

    @Before
    public void setUp() throws Exception {
        deliveryVehicleFuture=new Future<>();
        integerFuture=new Future<>();
        booleanFuture=new Future<>();
        stringFuture=new Future<>();
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Tests get method return correct result after being resolved.
     * Tests for unresolved Future types are not included since it is a Blocking method.
     */
    @Test
    public void get() {
        //Checks A Integer Future gets assigned value after resolved.
        Integer randomInteger = new Random().nextInt(10);
        integerFuture.resolve(randomInteger);
        assertEquals(randomInteger,integerFuture.get());
        //Checks A Boolean Future gets assigned value after resolved.
        booleanFuture.resolve(new Boolean(true));
        assertEquals(true,booleanFuture.get());
        booleanFuture=new Future<>();
        booleanFuture.resolve(new Boolean(false));
        assertEquals(false,booleanFuture.get());
        //Checks A String Future gets assigned value after resolved.
        String test = "Test String";
        stringFuture.resolve(test);
        assertEquals(test,stringFuture.get());
        //Checks A DeliveryVehicle Object Future gets assigned value after resolved.
        DeliveryVehicle testVehicle = new DeliveryVehicle(1234,200);
        deliveryVehicleFuture.resolve(testVehicle);
        assertEquals(testVehicle.getClass().getName() ,deliveryVehicleFuture.get().getClass().getName());
    }

    /**
     * Tests resolve method resolves different Future types to correct result
     */
    @Test
    public void resolve() {
        //Checks A Integer Future gets assigned value after resolved.
        Integer randomInteger = new Random().nextInt(10);
        integerFuture.resolve(randomInteger);
        assertEquals(randomInteger,integerFuture.get());
        //Checks A Boolean Future gets assigned value after resolved.
        booleanFuture.resolve(new Boolean(true));
        assertEquals(true,booleanFuture.get());
        booleanFuture=new Future<>();
        booleanFuture.resolve(new Boolean(false));
        assertEquals(false,booleanFuture.get());
        //Checks A String Future gets assigned value after resolved.
        String test = "Test String";
        stringFuture.resolve(test);
        assertEquals(test,stringFuture.get());
        //Checks A DeliveryVehicle Object Future gets assigned value after resolved.
        DeliveryVehicle testVehicle = new DeliveryVehicle(1234,200);
        deliveryVehicleFuture.resolve(testVehicle);
        assertEquals(testVehicle.getClass().getName() ,deliveryVehicleFuture.get().getClass().getName());
    }

    /**
     * Checks isDone method returns correct resolved boolean value before and after being resolved.
     */
    @Test
    public void isDone() {
        //Check before being resolved
        assertFalse("Expected False since "+ integerFuture.toString() + " is not resolved.", integerFuture.isDone());
        assertFalse("Expected False since "+ booleanFuture.toString() + " is not resolved.", booleanFuture.isDone());
        assertFalse("Expected False since "+ stringFuture.toString() + " is not resolved.", stringFuture.isDone());
        assertFalse("Expected False since "+ deliveryVehicleFuture.toString() + " is not resolved.", deliveryVehicleFuture.isDone());

        //Check all futures isDOne return true after being resolved
        integerFuture.resolve(new Random().nextInt());
        booleanFuture.resolve(true);
        stringFuture.resolve("Test");
        deliveryVehicleFuture.resolve(new DeliveryVehicle(1234,200));
        assertTrue("Expected True since "+ integerFuture.toString() + " is now resolved.", integerFuture.isDone());
        assertTrue("Expected True since "+ booleanFuture.toString() + " is now resolved.", booleanFuture.isDone());
        assertTrue("Expected True since "+ stringFuture.toString() + " is now resolved.", stringFuture.isDone());
        assertTrue("Expected True since "+ deliveryVehicleFuture.toString() + " is now resolved.", deliveryVehicleFuture.isDone());
    }

    /**
     * Checks the get method returns null after certain time if the Future Object was not resolved during that time frame.
     * Checks the get method returns correct result after a certain time after being resolved.
     *
     */
    @Test
    public void get1() {
        //Test Future Objects return null since they are not being resolved in given time frame.
        assertNull(integerFuture.toString()+ " Did not resolve on time, hence null is expected",integerFuture.get(1, TimeUnit.SECONDS));
        assertNull(booleanFuture.toString() + " Did not resolve on time, hence null is expected",booleanFuture.get(1, TimeUnit.SECONDS));
        assertNull(stringFuture.toString() + " Did not resolve on time, hence null is expected",stringFuture.get(1, TimeUnit.SECONDS));
        assertNull(deliveryVehicleFuture.toString() + " Did not resolve on time, hence null is expected",deliveryVehicleFuture.get(1, TimeUnit.SECONDS));
        //Resolve Future object and expect to get results in given time frame.
        Integer randomInteger = new Random().nextInt(10);
        DeliveryVehicle testVehicle = new DeliveryVehicle(1234,200);
        integerFuture.resolve(randomInteger);
        booleanFuture.resolve(true);
        stringFuture.resolve("Test");
        deliveryVehicleFuture.resolve(testVehicle);
        assertEquals(randomInteger, integerFuture.get(1,TimeUnit.NANOSECONDS));
        assertEquals(true, booleanFuture.get(1,TimeUnit.NANOSECONDS));
        assertEquals("Test", stringFuture.get(1,TimeUnit.NANOSECONDS));
        assertEquals(testVehicle, deliveryVehicleFuture.get(1,TimeUnit.NANOSECONDS));
    }
}
package ar.edu.itba.pod.server;

import ar.edu.itba.pod.callbacks.FlightEventsCallbackHandler;
import ar.edu.itba.pod.models.RunwayCategory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AirportManagementImplConcurrencyTest {

    private AirportManagementImpl airportManagement;

    private final ExecutorService pool = Executors.newFixedThreadPool(10);


    private final Runnable addRunwayDifferentNameSameCategory = () -> {
        try {
            airportManagement.addRunway(UUID.randomUUID().toString(), RunwayCategory.A);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    };

    private final Runnable addRunwayDifferentNameRandomCategory = () -> {
        try {
            airportManagement.addRunway(UUID.randomUUID().toString(), RunwayCategory.randomCategory());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    };

    private final Runnable addRunwaySameName = () -> {
        try {
            airportManagement.addRunway("Runway", RunwayCategory.A);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    };

    private final Runnable addRandomFlight = () -> {
        try {
            airportManagement.requireRunway(getRandomNumber(0, 10000), "Test", "Aerolineas Argentinas", RunwayCategory.A);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    };

    private final Runnable addExampleFlight = () -> {
        try {
            airportManagement.requireRunway(123, "Test", "Aerolineas Argentinas", RunwayCategory.A);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    };

    private final Runnable registerForFlightDifferentAirlineSameCode = () -> {
        try {
            airportManagement.registerForFlight(UUID.randomUUID().toString(), 123, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    };

    private final Runnable registerForFlightSameAirlineSameFlightCode = () -> {
        try {
            airportManagement.registerForFlight("Aerolineas Argentinas", 123, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    };

    @BeforeAll
    public void initialSetup(){
        airportManagement = new AirportManagementImpl();
    }

    @BeforeEach
    public void resetInstance(){
        airportManagement = new AirportManagementImpl();
    }

    @Test
    public void addRunwayConcurrencyTest_addMultipleRunwaysDifferentNames_ShouldAddAll() throws InterruptedException {
        for(int i=0; i<10000; i++){
            pool.submit(addRunwayDifferentNameSameCategory);
        }
        pool.shutdown();
        pool.awaitTermination(1000, TimeUnit.SECONDS);
        assertEquals(10000L, airportManagement.getRunwaysQuantity());
    }

    @Test
    public void addRunwayConcurrencyTest_addMultipleRunwaysSameName_ShouldAddOne() throws InterruptedException {
        for(int i=0; i<10000; i++){
            pool.submit(addRunwaySameName);
        }
        pool.shutdown();
        pool.awaitTermination(1000, TimeUnit.SECONDS);
        assertEquals(1L, airportManagement.getRunwaysQuantity());
    }

    @Test
    public void addRunwayInMiddleOfReorderingFlights_ShouldWaitUntilReorderEnds() throws InterruptedException {
        //First add a lot of runways
        for(int i=0; i<100; i++){
            pool.submit(addRunwayDifferentNameRandomCategory);
        }
        pool.shutdown();
        pool.awaitTermination(1000, TimeUnit.SECONDS);
        //Then require runway for many flights
        ExecutorService newPool = Executors.newFixedThreadPool(10);
        for(int i=0; i<300; i++){
            newPool.submit(addRandomFlight);
        }
        newPool.shutdown();
        newPool.awaitTermination(1000, TimeUnit.SECONDS);


        //Now test reorder in parallel with adding a new runway
        ExecutorService otherNewPool = Executors.newFixedThreadPool(10);
        otherNewPool.submit(
        new Thread(() -> {
            try {
                airportManagement.reorderFlightsForTest();
            } catch (RemoteException | InterruptedException e) {
                e.printStackTrace();
            }
        }));
        otherNewPool.submit(
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                airportManagement.addRunway("NewOne", RunwayCategory.A);
            } catch (RemoteException | InterruptedException e) {
                e.printStackTrace();
            }
        }));
        otherNewPool.shutdown();
        otherNewPool.awaitTermination(1000, TimeUnit.SECONDS);
        assertEquals(0, airportManagement.getQueueForRunway("NewOne").size());
    }

    private int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    @Test
    public void registerForSameAirlineSameFlight_ShouldAddOne () throws InterruptedException {
        //Add Runway
        ExecutorService newPool = Executors.newFixedThreadPool(10);
        newPool.submit(addRunwaySameName);
        newPool.shutdown();
        newPool.awaitTermination(1000, TimeUnit.SECONDS);

        //Add Flight into runway
        ExecutorService otherNewPool = Executors.newFixedThreadPool(10);
        otherNewPool.submit(addExampleFlight);
        otherNewPool.shutdown();
        otherNewPool.awaitTermination(1000, TimeUnit.SECONDS);

        //Register for flight
        for(int i=0; i<10000; i++){
            pool.submit(registerForFlightSameAirlineSameFlightCode);
        }
        pool.shutdown();
        pool.awaitTermination(1000, TimeUnit.SECONDS);
        assertEquals(1L, airportManagement.getRegisterQuantityForFlight(123));
    }

    @Test
    public void registerForDifferentAirlineSameFlight_ShouldAddAll () throws InterruptedException {
        //Add Runway
        ExecutorService newPool = Executors.newFixedThreadPool(10);
        newPool.submit(addRunwaySameName);
        newPool.shutdown();
        newPool.awaitTermination(1000, TimeUnit.SECONDS);

        //Add Flight into runway
        ExecutorService otherNewPool = Executors.newFixedThreadPool(10);
        otherNewPool.submit(addExampleFlight);
        otherNewPool.shutdown();
        otherNewPool.awaitTermination(1000, TimeUnit.SECONDS);

        //Register for flight
        for(int i=0; i<10000; i++){
            pool.submit(registerForFlightDifferentAirlineSameCode);
        }
        pool.shutdown();
        pool.awaitTermination(1000, TimeUnit.SECONDS);
        assertEquals(10000L, airportManagement.getRegisterQuantityForFlight(123));
    }

}

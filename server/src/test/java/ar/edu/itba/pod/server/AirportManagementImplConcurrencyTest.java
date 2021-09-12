package ar.edu.itba.pod.server;

import ar.edu.itba.pod.models.RunwayCategory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.rmi.RemoteException;
import java.util.Collection;
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


    private final Runnable addRunway = () -> {
        try {
            airportManagement.addRunway(UUID.randomUUID().toString(), RunwayCategory.A);
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
    public void concurrencyTest() throws InterruptedException {
        Collection<Callable<Object>> callables = Stream.of(addRunway, addRunway, addRunway, addRunway, addRunway).map(Executors::callable).collect(Collectors.toList());
        pool.invokeAll(callables);
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("a");
    }

    @Test
    public void concurrencyTest2() throws InterruptedException {
        for(int i=0; i<10000; i++){
            pool.submit(addRunway);
        }
//        Collection<Callable<Object>> callables = Stream.of(addRunway, addRunway, addRunway, addRunway, addRunway).map(Executors::callable).collect(Collectors.toList());
//        pool.invokeAll(callables);
        pool.shutdown();
        pool.awaitTermination(1000, TimeUnit.SECONDS);
        assertEquals(10000L, airportManagement.getRunwaysQuantity());
    }


}

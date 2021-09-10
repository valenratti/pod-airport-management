package ar.edu.itba.pod.server;

import ar.edu.itba.pod.exceptions.DuplicateRunwayException;
import ar.edu.itba.pod.models.RunwayCategory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AirportManagementImplTest {

    private AirportManagementImpl airportManagement;

    @BeforeAll
    public void initialSetup(){
        airportManagement = new AirportManagementImpl();
    }

    @BeforeEach
    public void resetInstance(){
        airportManagement = new AirportManagementImpl();
    }

    @Test
    public void afterCreateARunway_ShouldBeOpen() throws RemoteException {
        airportManagement.addRunway("My Runway", RunwayCategory.A);
        assertTrue(airportManagement.getRunwayStatus("My Runway"));
    }

    @Test
    public void afterCreateARunwayThatExists_ShouldThrowException() throws RemoteException {
        airportManagement.addRunway("MyRunwayException", RunwayCategory.A);
        assertThrows(DuplicateRunwayException.class, () -> airportManagement.addRunway("MyRunwayException", RunwayCategory.A));
    }

    @Test
    public void openRunwayThatIsAlreadyOpen_ShouldThrowException() throws RemoteException {
        airportManagement.addRunway("My Runway", RunwayCategory.A);
        assertThrows(IllegalStateException.class, () -> airportManagement.openRunway("My Runway"));
    }

    @Test
    public void closeRunwayThatIsAlreadyClosed_ShouldThrowException() throws RemoteException {
        airportManagement.addRunway("My Runway", RunwayCategory.A);
        airportManagement.closeRunway("My Runway");

        assertThrows(IllegalStateException.class, () -> airportManagement.openRunway("My Runway"));
    }

    @Test
    public void closeRunwayThatIsOpen() throws RemoteException {
        airportManagement.addRunway("My Runway", RunwayCategory.A);
        airportManagement.closeRunway("My Runway");
        assertFalse(airportManagement.getRunwayStatus("My Runway"));
    }

    @Test
    public void givenTwoRunwaysWithMinimunCategory_ShouldTakeOneWithLessSize() throws RemoteException {
        airportManagement.addRunway("My Runway A", RunwayCategory.A);
        airportManagement.addRunway("My Runway B", RunwayCategory.B);
        airportManagement.requireRunway(150, "Destination Airport", "Testing Airline", RunwayCategory.A);
        airportManagement.requireRunway(151, "Destination Airport", "Testing Airline", RunwayCategory.A);
        assertEquals(1, airportManagement.getQueueForRunway("My Runway A").size());
        assertEquals(1, airportManagement.getQueueForRunway("My Runway B").size());
    }

    @Test
    public void givenTwoRunwaysWithMinimunCategoryAndSameSize_ShouldTakeOneWithFirstOrderByName() throws RemoteException {
        airportManagement.addRunway("My Runway A", RunwayCategory.A);
        airportManagement.addRunway("My Runway B", RunwayCategory.B);
        airportManagement.requireRunway(150, "Destination Airport", "Testing Airline", RunwayCategory.A);
        airportManagement.requireRunway(151, "Destination Airport", "Testing Airline", RunwayCategory.A);
        airportManagement.requireRunway(152, "Destination Airport", "Testing Airline", RunwayCategory.A);
        assertEquals(2, airportManagement.getQueueForRunway("My Runway A").size());
        assertEquals(1, airportManagement.getQueueForRunway("My Runway B").size());
    }




}

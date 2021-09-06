package ar.edu.itba.pod.callbacks;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FlightEventsCallbackHandler extends Remote {
    void displayFlightEvents() throws RemoteException;
    void flightAssignedToRunway(int flightId, String runwayName, int flightsAhead) throws RemoteException;
    void flightChangedPositionInQueue(int flightId, String runwayName, int flightsAhead) throws RemoteException;
    void flightDepartured(int flightId, String runwayName) throws RemoteException;
}

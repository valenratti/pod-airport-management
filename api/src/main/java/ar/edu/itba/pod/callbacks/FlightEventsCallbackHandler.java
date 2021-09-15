package ar.edu.itba.pod.callbacks;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FlightEventsCallbackHandler extends Remote, Serializable {
    void displayFlightEvents() throws RemoteException;

    /**
     * Function to be executed when the flight is assigned to a runaway
     * @param flightId
     * @param runwayName
     * @param flightsAhead
     * @throws RemoteException
     */
    void flightAssignedToRunway(int flightId, String runwayName, int flightsAhead, String destination) throws RemoteException;

    /**
     * Function to be executed when the suscribed flight changes it's position in the current queue.
     * @param flightId
     * @param runwayName
     * @param flightsAhead
     * @throws RemoteException
     */
    void flightChangedPositionInQueue(int flightId, String runwayName, int flightsAhead, String destination) throws RemoteException;

    /**
     * Function to be executed when the flight departures.
     * @param flightId
     * @param runwayName
     * @throws RemoteException
     */
    void flightDepartured(int flightId, String runwayName, String destination) throws RemoteException;
}

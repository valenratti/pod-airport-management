package ar.edu.itba.pod.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class FlightEventsCallbackHandlerImpl extends UnicastRemoteObject implements ar.edu.itba.pod.callbacks.FlightEventsCallbackHandler {
    protected FlightEventsCallbackHandlerImpl() throws RemoteException {
    }

    @Override
    public void displayFlightEvents() throws RemoteException {

    }

    @Override
    public void flightAssignedToRunway(int flightId, String runwayName, int flightsAhead, String destination) throws RemoteException {
        String msg  = "Flight " + flightId + " with destiny " + destination + " was assigned to runway " +
                runwayName + " and there are " + flightsAhead + " flights waiting ahead";

        System.out.println(msg);
    }

    @Override
    public void flightChangedPositionInQueue(int flightId, String runwayName, int flightsAhead, String destination) throws RemoteException {
        String msg = "A flight departed from runway " + runwayName + ". Flight " + flightId + " with destiny " + destination  +
                " has " + flightsAhead + " flights waiting ahead";

        System.out.println(msg);
    }

    @Override
    public void flightDepartured(int flightId, String runwayName, String destination) throws RemoteException {
        String msg = "Flight " + flightId + " with destiny " + destination + " departed on runway " + runwayName;

        System.out.println(msg);
    }
}

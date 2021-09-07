package ar.edu.itba.pod.client;

import java.rmi.RemoteException;

public class FlightEventsCallbackHandlerImpl implements ar.edu.itba.pod.callbacks.FlightEventsCallbackHandler {
    @Override
    public void displayFlightEvents() throws RemoteException {

    }

    @Override
    public void flightAssignedToRunway(int flightId, String runwayName, int flightsAhead) throws RemoteException {
        String msg  = "Flight " + flightId + "with destiny " + "destiny" + " was assigned to runway " +
                runwayName + " and there are " + flightsAhead + " flights waiting ahead";

        System.out.println(msg);
    }

    @Override
    public void flightChangedPositionInQueue(int flightId, String runwayName, int flightsAhead) throws RemoteException {
        String msg = "A flight departed from runway " + runwayName + ". Flight " + flightId + "with destiny " + "destiny"  +
                " has " + flightsAhead + " flights waiting ahead";

        System.out.println(msg);
    }

    @Override
    public void flightDepartured(int flightId, String runwayName) throws RemoteException {
        String msg = "Flight " + flightId + "with destiny " + "destiny" + " departed on runway " + runwayName;

        System.out.println(msg);
    }
}

package ar.edu.itba.pod.server;

import java.rmi.Remote;

public interface FlightEventsCallbackHandler extends Remote {
    void displayFlightEvents();
}

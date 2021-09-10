package ar.edu.itba.pod.services;

import ar.edu.itba.pod.callbacks.FlightEventsCallbackHandler;
import ar.edu.itba.pod.exceptions.FlightNotFromAirlineException;
import ar.edu.itba.pod.exceptions.FlightNotInQueueException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FlightTrackingService extends Remote {
    /**
     * Registers a new flight and assigns to runway
     * if possible
     * @param airlineName
     * @param flightCode
     * @param callbackHandler
     * @throws RemoteException
     * @throws FlightNotFromAirlineException
     * @throws FlightNotInQueueException
     */
    void registerForFlight(String airlineName, int flightCode, FlightEventsCallbackHandler callbackHandler) throws RemoteException, FlightNotFromAirlineException, FlightNotInQueueException;
}

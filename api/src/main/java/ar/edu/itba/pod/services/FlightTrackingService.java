package ar.edu.itba.pod.services;

import ar.edu.itba.pod.callbacks.FlightEventsCallbackHandler;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FlightTrackingService extends Remote {
    void registerForFlight(int flightCode, FlightEventsCallbackHandler callbackHandler) throws RemoteException;
}

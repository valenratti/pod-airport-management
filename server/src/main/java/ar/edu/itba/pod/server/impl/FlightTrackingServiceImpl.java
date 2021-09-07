package ar.edu.itba.pod.server.impl;

import ar.edu.itba.pod.callbacks.FlightEventsCallbackHandler;
import ar.edu.itba.pod.exceptions.FlightNotFromAirlineException;
import ar.edu.itba.pod.exceptions.FlightNotInQueueException;
import ar.edu.itba.pod.server.AirportDataManagement;
import ar.edu.itba.pod.services.FlightTrackingService;

import java.rmi.RemoteException;

public class FlightTrackingServiceImpl implements FlightTrackingService {

    private final AirportDataManagement airportDataManagement;

    public FlightTrackingServiceImpl(){
        this.airportDataManagement = AirportDataManagement.getInstance();
    }

    @Override
    public void registerForFlight(String airlineName, int flightCode, FlightEventsCallbackHandler callbackHandler) throws RemoteException, FlightNotFromAirlineException, FlightNotInQueueException {
        airportDataManagement.subscribeToFlight(airlineName,flightCode,callbackHandler);
    }
}

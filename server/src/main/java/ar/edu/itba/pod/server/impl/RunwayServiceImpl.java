package ar.edu.itba.pod.server.impl;

import ar.edu.itba.pod.models.RunwayCategory;
import ar.edu.itba.pod.server.AirportDataManagement;
import ar.edu.itba.pod.server.model.Flight;
import ar.edu.itba.pod.services.RunwayService;

import java.rmi.RemoteException;

public class RunwayServiceImpl implements RunwayService {

    private final AirportDataManagement airportDataManagement;

    public RunwayServiceImpl(){
        this.airportDataManagement = AirportDataManagement.getInstance();
    }

    @Override
    public void requireRunway(int flightCode, String destinationAirport, String airlineName, RunwayCategory minCategory) throws RemoteException {
        final Flight flight = new Flight(flightCode, destinationAirport, airlineName, minCategory);
        airportDataManagement.assignFlightToRunwayIfPossible(flight);
    }
}

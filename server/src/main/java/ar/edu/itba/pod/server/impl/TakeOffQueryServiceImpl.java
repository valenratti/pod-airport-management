package ar.edu.itba.pod.server.impl;

import ar.edu.itba.pod.models.FlightDetailsDTO;
import ar.edu.itba.pod.server.AirportDataManagement;
import ar.edu.itba.pod.services.TakeOffQueryService;

import java.util.List;

public class TakeOffQueryServiceImpl implements TakeOffQueryService {

    private final AirportDataManagement airportDataManagement;

    public TakeOffQueryServiceImpl(){
        this.airportDataManagement = AirportDataManagement.getInstance();
    }

    @Override
    public List<FlightDetailsDTO> getAllTakeoffs() {
        return airportDataManagement.getAllFlightsDepartures();
    }

    @Override
    public List<FlightDetailsDTO> getTakeoffsByRunway(String runway) {
        return airportDataManagement.getAllFlightsDeparturesByRunway(runway);
    }

    @Override
    public List<FlightDetailsDTO> getTakeoffsByAirline(String airline) {
        return airportDataManagement.getAllFlightsDeparturesByAirline(airline);
    }
}

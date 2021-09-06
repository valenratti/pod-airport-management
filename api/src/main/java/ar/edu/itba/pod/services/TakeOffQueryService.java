package ar.edu.itba.pod.services;

import ar.edu.itba.pod.models.FlightDetailsDTO;

import java.rmi.Remote;
import java.util.List;

public interface TakeOffQueryService extends Remote {
    List<FlightDetailsDTO> getAllTakeoffs();
    List<FlightDetailsDTO> getTakeoffsByRunway(String runway);
    List<FlightDetailsDTO> getTakeoffsByAirline(String airline);
}

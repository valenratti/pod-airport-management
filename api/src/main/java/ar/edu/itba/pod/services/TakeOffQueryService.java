package ar.edu.itba.pod.services;

import ar.edu.itba.pod.models.FlightDetailsDTO;

import java.rmi.Remote;
import java.util.List;

public interface TakeOffQueryService extends Remote {
    /**
     * Returns all historical departures
     * @return
     */
    List<FlightDetailsDTO> getAllTakeoffs();

    /**
     * Returns all historical departures in a certain runway
     * @param runway
     * @return
     */
    List<FlightDetailsDTO> getTakeoffsByRunway(String runway);

    /**
     * Returns all historical departures in a certain airline
     * @param airline
     * @return
     */
    List<FlightDetailsDTO> getTakeoffsByAirline(String airline);
}

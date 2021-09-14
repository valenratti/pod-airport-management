package ar.edu.itba.pod.services;

import ar.edu.itba.pod.models.FlightDetailsDTO;

import java.rmi.Remote;
import java.util.*;

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

    /**
     * Returns the quantity of runways created (open and closed)
     * @return
     */
    public long getRunwaysQuantity();

    /**
     * Returns the queantity of flights created
     * @return
     */
    public long getFlightsQuantity();

    /**
     * Returns the quantity of registers for a particular flight
     * @param flightCode
     * @return
     */
    public long getRegisterQuantityForFlight(int flightCode);
}

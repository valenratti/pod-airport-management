package ar.edu.itba.pod.models;

import java.io.Serializable;

public class FlightDetailsDTO implements Serializable {
    private int flightId;
    private String destinationAirportCode;
    private String airlineName;
    private RunwayCategory category;
    private int takeOffCounter;
    private String runwayName;
    private RunwayCategory runwayCategory;
    private boolean isOpen;

    public FlightDetailsDTO(int flightId, String destinationAirportCode, String airlineName, RunwayCategory category, int takeOffCounter, String runwayName, RunwayCategory runwayCategory, boolean isOpen) {
        this.flightId = flightId;
        this.destinationAirportCode = destinationAirportCode;
        this.airlineName = airlineName;
        this.category = category;
        this.takeOffCounter = takeOffCounter;
        this.runwayName = runwayName;
        this.runwayCategory = runwayCategory;
        this.isOpen = isOpen;
    }

    public int getFlightId() {
        return flightId;
    }

    public String getDestinationAirportCode() {
        return destinationAirportCode;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public RunwayCategory getCategory() {
        return category;
    }

    public int getTakeOffCounter() {
        return takeOffCounter;
    }

    public String getRunwayName() {
        return runwayName;
    }

    public RunwayCategory getRunwayCategory() {
        return runwayCategory;
    }

    public boolean isOpen() {
        return isOpen;
    }
}

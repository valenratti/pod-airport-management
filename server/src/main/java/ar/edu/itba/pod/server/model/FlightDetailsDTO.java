package ar.edu.itba.pod.server.model;

public class FlightDetailsDTO {
    private int flightId;
    private String destinationAirportCode;
    private String airlineName;
    private RunawayCategory category;
    private int takeOffCounter;
    private String runawayName;
    private RunawayCategory runawayCategory;
    private boolean isOpen;

    public int getFlightId() {
        return flightId;
    }

    public String getDestinationAirportCode() {
        return destinationAirportCode;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public RunawayCategory getCategory() {
        return category;
    }

    public int getTakeOffCounter() {
        return takeOffCounter;
    }

    public String getRunawayName() {
        return runawayName;
    }

    public RunawayCategory getRunawayCategory() {
        return runawayCategory;
    }

    public boolean isOpen() {
        return isOpen;
    }
}

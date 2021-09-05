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

    public FlightDetailsDTO(int flightId, String destinationAirportCode, String airlineName, RunawayCategory category, int takeOffCounter, String runawayName, RunawayCategory runawayCategory, boolean isOpen) {
        this.flightId = flightId;
        this.destinationAirportCode = destinationAirportCode;
        this.airlineName = airlineName;
        this.category = category;
        this.takeOffCounter = takeOffCounter;
        this.runawayName = runawayName;
        this.runawayCategory = runawayCategory;
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

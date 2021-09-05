package ar.edu.itba.pod.server.model;

public class Flight {
    private int id;
    private String destinationAirportCode;
    private String airlineName;
    private RunawayCategory category;
    private int takeOffCounter;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDestinationAirportCode() {
        return destinationAirportCode;
    }

    public void setDestinationAirportCode(String destinationAirportCode) {
        this.destinationAirportCode = destinationAirportCode;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public RunawayCategory getCategory() {
        return category;
    }

    public void setCategory(RunawayCategory category) {
        this.category = category;
    }

    public int getTakeOffCounter() {
        return takeOffCounter;
    }

    public void setTakeOffCounter(int takeOffCounter) {
        this.takeOffCounter = takeOffCounter;
    }
}

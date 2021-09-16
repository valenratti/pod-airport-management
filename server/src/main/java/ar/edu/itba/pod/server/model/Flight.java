package ar.edu.itba.pod.server.model;

import ar.edu.itba.pod.models.RunwayCategory;

import java.util.Objects;

public class Flight {
    private final int id;
    private final String destinationAirportCode;
    private final String airlineName;
    private final RunwayCategory category;
    private int takeOffCounter;
    private Runway currentRunway;

    public Flight(int id, String destinationAirportCode, String airlineName, RunwayCategory category) {
        this.id = id;
        this.destinationAirportCode = destinationAirportCode;
        this.airlineName = airlineName;
        this.category = category;
        this.takeOffCounter = 0;
    }


    public int getId() {
        return id;
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

    public void setTakeOffCounter(int takeOffCounter) {
        this.takeOffCounter = takeOffCounter;
    }

    public Runway getCurrentRunway() {
        return currentRunway;
    }

    public void setCurrentRunway(Runway currentRunway) {
        this.currentRunway = currentRunway;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return getId() == flight.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

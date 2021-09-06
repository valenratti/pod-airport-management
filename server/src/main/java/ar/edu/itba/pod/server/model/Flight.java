package ar.edu.itba.pod.server.model;

import ar.edu.itba.pod.models.RunawayCategory;

import java.util.Objects;

public class Flight {
    private final int id;
    private final String destinationAirportCode;
    private final String airlineName;
    private final RunawayCategory category;
    private int takeOffCounter;

    public Flight(int id, String destinationAirportCode, String airlineName, RunawayCategory category) {
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


    public RunawayCategory getCategory() {
        return category;
    }

    public int getTakeOffCounter() {
        return takeOffCounter;
    }

    public void setTakeOffCounter(int takeOffCounter) {
        this.takeOffCounter = takeOffCounter;
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

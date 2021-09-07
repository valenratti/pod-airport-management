package ar.edu.itba.pod.exceptions;

public class FlightNotFromAirlineException extends RuntimeException{
    public FlightNotFromAirlineException(int flightCode, String airlineName){
        super("Flight " + flightCode + " does not belong to " + airlineName);
    }
}

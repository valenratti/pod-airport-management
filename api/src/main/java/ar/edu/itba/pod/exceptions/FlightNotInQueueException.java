package ar.edu.itba.pod.exceptions;

public class FlightNotInQueueException extends RuntimeException{
    public FlightNotInQueueException(int flight){
        super("Flight " + flight + " is not in queue");
    }
}

package ar.edu.itba.pod.exceptions;

public class DuplicatedFlightException extends RuntimeException{

    public DuplicatedFlightException(String code) {
        super("Flight with code + " + code + "exists");
    }
}

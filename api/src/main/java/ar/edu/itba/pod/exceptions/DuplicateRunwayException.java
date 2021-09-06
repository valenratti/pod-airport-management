package ar.edu.itba.pod.exceptions;

public class DuplicateRunwayException extends RuntimeException{
    public DuplicateRunwayException(String name){
        super("Runway: " + name + " exists.");
    }
}

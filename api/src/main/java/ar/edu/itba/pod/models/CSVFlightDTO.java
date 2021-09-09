package ar.edu.itba.pod.models;

import java.io.Serializable;

public class CSVFlightDTO implements Serializable {
    private int id;
    private String destinationAirportCode;
    private String airlineName;
    private RunwayCategory category;

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

    @Override
    public String toString() {
        return "CSVFlightDTO{" +
                "id=" + id +
                ", destinationAirportCode='" + destinationAirportCode + '\'' +
                ", airlineName='" + airlineName + '\'' +
                ", category=" + category +
                '}';
    }

    public static CSVFlightDTO toCsvFlightDTO(String line){
        String[] fields = line.split(";");
        CSVFlightDTO flightDTO = new CSVFlightDTO();

        flightDTO.id = Integer.parseInt(fields[0]);
        flightDTO.destinationAirportCode = fields[1];
        flightDTO.airlineName = fields[2];
        flightDTO.category = RunwayCategory.valueOf(fields[3]);

        return flightDTO;
    }
}

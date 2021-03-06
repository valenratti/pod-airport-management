package ar.edu.itba.pod.client;

import ar.edu.itba.pod.models.FlightDetailsDTO;
import ar.edu.itba.pod.services.TakeOffQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.List;

public class QueryClient {
    private static final Logger logger = LoggerFactory.getLogger(QueryClient.class);

    public static void main(String[] args) throws IOException, NotBoundException {
        String serverAddress = System.getProperty("serverAddress");
        if (Utils.isNullOrEmpty(serverAddress)) {
            logger.error("You must provide a server address");
            return;
        }

        String outPath = System.getProperty("outPath");
        if (Utils.isNullOrEmpty(outPath))
            logger.error("You must provide a path");
        else {
            TakeOffQueryService service = (TakeOffQueryService) Naming.lookup("//" + serverAddress + "/query");

            String airlineName = System.getProperty("airline");
            String runwayName = System.getProperty("runway");
            boolean emptyAirline = Utils.isNullOrEmpty(airlineName), emptyRunway = Utils.isNullOrEmpty(runwayName);

            if (!emptyAirline && !emptyRunway) {
                logger.error("You can't provide both runway and airline");
                return;
            }

            // Si no se indica ninguno de los dos se resuelve la consulta 1.
            // Si se indico la aerolinea se resuelve la consulta 2.
            // Si se indico la pista se resuelve la consulta 3.
            List<FlightDetailsDTO> flights = (emptyAirline && emptyRunway) ? service.getAllTakeoffs() :
                    (emptyAirline ? service.getTakeoffsByRunway(runwayName) : service.getTakeoffsByAirline(airlineName));

            if (flights.isEmpty())
                logger.info("No matches found. File not created");
            else {
                List<String> dataLines = new ArrayList<>();
                dataLines.add("TakeOffOrders;RunwayName;FlightCode;DestinyAirport;AirlineName");

                flights.forEach(f -> dataLines.add(flightDetailsToCsvLine(f)));

                Path path = Paths.get(outPath);
                Files.write(path, dataLines);
                logger.info("Query results available in file: " + outPath);
            }
        }
    }

    private static String flightDetailsToCsvLine(FlightDetailsDTO fd) {
        String[] aux = new String[] {String.valueOf(fd.getTakeOffCounter()), fd.getRunwayName(), String.valueOf(fd.getFlightId()), fd.getDestinationAirportCode(), fd.getAirlineName()};
        return String.join(";", aux);
    }
}

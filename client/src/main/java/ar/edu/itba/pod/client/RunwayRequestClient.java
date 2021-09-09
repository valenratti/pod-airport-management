package ar.edu.itba.pod.client;

import ar.edu.itba.pod.models.CSVFlightDTO;
import ar.edu.itba.pod.services.RunwayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class RunwayRequestClient {
    private static final Logger logger = LoggerFactory.getLogger(RunwayRequestClient.class);

    public static void main(String[] args) {
        logger.info("Starting runway request client...");
        //TODO: RMI
        RunwayService service = null;
        String filePath = System.getProperty("inPath");
        if(filePath == null || filePath.isEmpty())
            logger.error("You must provide a runway request CSV file");
        else{
            try{
                List<String> fileLines = Files.readAllLines(Paths.get(filePath));
                List<CSVFlightDTO> csvFlightDTOList = fileLines.stream()
                        .skip(1)
                        .map(CSVFlightDTO::toCsvFlightDTO)
                        .collect(Collectors.toList());
                csvFlightDTOList.forEach(System.out::println);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }
}

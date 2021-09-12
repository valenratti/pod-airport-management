package ar.edu.itba.pod.models;

import java.io.Serializable;
import java.util.Random;


public enum RunwayCategory implements Serializable, Comparable<RunwayCategory> {
    A, B, C, D, E, F;

    public static RunwayCategory randomCategory(){
        int rand = getRandomNumber(0, 6);
        for(RunwayCategory runwayCategory : RunwayCategory.values()){
            if(runwayCategory.ordinal() == rand)
                return runwayCategory;
        }
        return A;
    }

    private static int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }
}

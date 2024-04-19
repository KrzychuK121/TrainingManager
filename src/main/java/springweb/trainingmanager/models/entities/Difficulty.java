package springweb.trainingmanager.models.entities;

public enum Difficulty {
    FOR_BEGINNERS,
    MEDIUM,
    ADVANCED;

    public static String getEnumDesc(Difficulty difficulty){
        String toReturn = "";

        switch(difficulty){
            case FOR_BEGINNERS -> toReturn = "dla początkujących";
            case MEDIUM -> toReturn = "dla średniozaawansowanych";
            case ADVANCED -> toReturn = "dla zaawansowanych";
        }

        return toReturn;
    }
}

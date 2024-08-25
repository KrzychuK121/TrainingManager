package springweb.training_manager.models.entities;

public enum Difficulty {
    FOR_BEGINNERS("dla początkujących"),
    MEDIUM("dla średniozaawansowanych"),
    ADVANCED("dla zaawansowanych");

    private final String display;

    private Difficulty(String display){
        this.display = display;
    }

    public static String getEnumDesc(Difficulty difficulty){
        if(difficulty == null)
            return "brak";
        return difficulty.display;
    }
}

package springweb.training_manager.models.entities;

public enum BodyPart {
    TRAPS("mięsień czworoboczny"),
    DELTS("mięsień naramienny"),
    CHEST("klatka piersiowa"),
    BACK("plecy"),
    TRICEPS("triceps"),
    BICEPS("biceps"),
    FOREARMS("mięsnie przedramion"),
    UPPER_ABS("górny ABS"),
    LOWER_BACK("dolne plecy"),
    LOWER_ABS("dolny ABS"),
    GLUTES("pośladki"),
    QUADS("mięsień czworogłowy"),
    HAMS("mięśnie ścięgna udowego"),
    CALVES("łydki");

    private final String display;

    private BodyPart(String display){
        this.display = display;
    }

    public static String getBodyDesc(BodyPart bodyPart){
        if(bodyPart == null)
            return "brak";
        return bodyPart.display;
    }
}

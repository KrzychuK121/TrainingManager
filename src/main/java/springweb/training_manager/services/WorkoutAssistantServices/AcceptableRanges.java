package springweb.training_manager.services.WorkoutAssistantServices;

// rounds range 1-4
// reps range 6-30
// time 50% range
// weight 25% range
public enum AcceptableRanges {
    ROUNDS(1, 4),
    REPETITIONS(6, 30),
    TIME(0.5f, 1.5f),
    WEIGHT(0.25f, 1.25f);

    public final float LOWER_LIMIT;
    public final float UPPER_LIMIT;

    AcceptableRanges(
        float lowerLimit,
        float upperLimit
    ) {
        this.LOWER_LIMIT = lowerLimit;
        this.UPPER_LIMIT = upperLimit;
    }
}

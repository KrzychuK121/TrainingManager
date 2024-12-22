package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import springweb.training_manager.models.entities.BodyPart;

import java.util.stream.Stream;

import static db.migration.V51__insert_cardio_trainings.insertWholeTrainingData;

public class V54__insert_other_body_parts_exercises extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        var wholeTrapsWorkouts = getWholeTrapsWorkouts();
        var wholeDeltsWorkouts = getWholeDeltsWorkouts();
        var wholeChestWorkouts = getWholeChestWorkouts();
        var wholeBackWorkouts = getWholeBackWorkouts();
        var wholeTricepsWorkouts = getWholeTricepsWorkouts();
        var wholeBicepsWorkouts = getWholeBicepsWorkouts();
        var wholeForearmsWorkouts = getWholeForearmsWorkouts();
        var wholeUpperAbsWorkouts = getWholeUpperAbsWorkouts();
        var wholeLowerBackWorkouts = getWholeLowerBackWorkouts();
        var wholeLowerAbsWorkouts = getWholeLowerAbsWorkouts();
        var wholeGlutesWorkouts = getWholeGlutesWorkouts();
        var wholeQuadsWorkouts = getWholeQuadsWorkouts();
        var wholeHamsWorkouts = getWholeHamsWorkouts();
        var wholeCalvesWorkouts = getWholeCalvesWorkouts();

        var wholeNewTrainingsData = Stream.of(
                wholeTrapsWorkouts,
                wholeDeltsWorkouts,
                wholeChestWorkouts,
                wholeBackWorkouts,
                wholeTricepsWorkouts,
                wholeBicepsWorkouts,
                wholeForearmsWorkouts,
                wholeUpperAbsWorkouts,
                wholeLowerBackWorkouts,
                wholeLowerAbsWorkouts,
                wholeGlutesWorkouts,
                wholeQuadsWorkouts,
                wholeHamsWorkouts,
                wholeCalvesWorkouts
            )
            .flatMap(Stream::of)
            .toArray(WholeTrainingData_V51[]::new);
        
        insertWholeTrainingData(
            context,
            wholeNewTrainingsData
        );
    }

    private static WholeTrainingData_V51[] getWholeTrapsWorkouts() {
        var trapsWorkout1 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening dla mięśnia czworobocznego, wzmacniający górną część pleców",
                "Trening czworoboczny - zestaw 1",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Szrugsy z hantlami",
                    "Szrugsy",
                    BodyPart.TRAPS,
                    10,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    20
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Podciąganie sztangi wzdłuż tułowia",
                    "Podciąganie sztangi",
                    BodyPart.TRAPS,
                    8,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    4,
                    null,
                    30
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Rozpiętki w opadzie tułowia",
                    "Rozpiętki",
                    BodyPart.TRAPS,
                    7,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    10
                )
            )
        );

        var trapsWorkout2 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening dla mięśnia czworobocznego, zwiększający siłę i wytrzymałość",
                "Trening czworoboczny - zestaw 2",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wiosłowanie hantlami w opadzie tułowia",
                    "Wiosłowanie",
                    BodyPart.TRAPS,
                    9,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    25
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Podciąganie sztangi na wyciągu",
                    "Podciąganie na wyciągu",
                    BodyPart.TRAPS,
                    7,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    4,
                    null,
                    40
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Unoszenie barków z talerzem",
                    "Unoszenie barków",
                    BodyPart.TRAPS,
                    6,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    15
                )
            )
        );

        var trapsWorkout3 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening funkcjonalny dla mięśnia czworobocznego",
                "Trening czworoboczny - zestaw 3",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Martwy ciąg klasyczny",
                    "Martwy ciąg",
                    BodyPart.TRAPS,
                    15,
                    null
                ),
                new ExerciseParameters_V51(
                    8,
                    4,
                    null,
                    50
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Unoszenie ramion w bok z gumą oporową",
                    "Unoszenie ramion",
                    BodyPart.TRAPS,
                    5,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Unoszenie łokci w górę z kettlebell",
                    "Unoszenie łokci",
                    BodyPart.TRAPS,
                    6,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    4,
                    null,
                    15
                )
            )
        );

        return new WholeTrainingData_V51[]{
            trapsWorkout1,
            trapsWorkout2,
            trapsWorkout3
        };
    }

    private static WholeTrainingData_V51[] getWholeDeltsWorkouts() {
        var deltsTraining1 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening siłowy skoncentrowany na mięśniu naramiennym",
                "Trening na barki - siła",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wyciskanie sztangi nad głowę",
                    "Military Press",
                    BodyPart.DELTS,
                    12,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    3,
                    null,
                    40
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Unoszenie hantli bokiem",
                    "Lateral Raise",
                    BodyPart.DELTS,
                    10,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    4,
                    null,
                    10
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Arnold Press",
                    "Arnold Press",
                    BodyPart.DELTS,
                    12,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    3,
                    null,
                    15
                )
            )
        );

        var deltsTraining2 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening wytrzymałościowy dla mięśnia naramiennego",
                "Trening na barki - wytrzymałość",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wyciskanie hantli w siadzie",
                    "Seated Dumbbell Press",
                    BodyPart.DELTS,
                    10,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    15
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Unoszenie ramion z kettlebell",
                    "Front Raise",
                    BodyPart.DELTS,
                    8,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    8
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Odwodzenie ramion w opadzie tułowia",
                    "Reverse Fly",
                    BodyPart.DELTS,
                    10,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    5
                )
            )
        );

        var deltsTraining3 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na stabilizację mięśnia naramiennego",
                "Trening na barki - stabilizacja",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Unoszenie ramion w pozycji plank",
                    "Plank Arm Raise",
                    BodyPart.DELTS,
                    6,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wyciskanie hantli jednorącz",
                    "Single Arm Dumbbell Press",
                    BodyPart.DELTS,
                    10,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    4,
                    null,
                    12
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Ćwiczenie z gumą na rotację zewnętrzną",
                    "External Rotation with Band",
                    BodyPart.DELTS,
                    8,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    0
                )
            )
        );
        return new WholeTrainingData_V51[]{
            deltsTraining1,
            deltsTraining2,
            deltsTraining3
        };
    }

    private static WholeTrainingData_V51[] getWholeChestWorkouts() {
        var chestTraining1 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening siłowy na klatkę piersiową",
                "Trening na klatkę - siła",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wyciskanie sztangi na ławce płaskiej",
                    "Bench Press",
                    BodyPart.CHEST,
                    20,
                    null
                ),
                new ExerciseParameters_V51(
                    8,
                    4,
                    null,
                    60
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wyciskanie hantli na ławce skośnej",
                    "Incline Dumbbell Press",
                    BodyPart.CHEST,
                    18,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    3,
                    null,
                    20
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Rozpiętki na ławce poziomej z hantlami",
                    "Flat Bench Dumbbell Fly",
                    BodyPart.CHEST,
                    15,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    12
                )
            )
        );

        var chestTraining2 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening wytrzymałościowy dla klatki piersiowej",
                "Trening na klatkę - wytrzymałość",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Pompki klasyczne",
                    "Push-Ups",
                    BodyPart.CHEST,
                    5,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Pompki na poręczach (dipy)",
                    "Chest Dips",
                    BodyPart.CHEST,
                    10,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Rozpiętki na bramie",
                    "Cable Fly",
                    BodyPart.CHEST,
                    15,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    10
                )
            )
        );

        var chestTraining3 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na stabilizację klatki piersiowej",
                "Trening na klatkę - stabilizacja",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Pompki z unoszeniem ramion na piłce",
                    "Push-Ups with Arm Raise on Ball",
                    BodyPart.CHEST,
                    8,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Rozpiętki na TRX",
                    "TRX Fly",
                    BodyPart.CHEST,
                    12,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Izometryczne utrzymanie w pompkach",
                    "Isometric Push-Up Hold",
                    BodyPart.CHEST,
                    8,
                    null
                ),
                new ExerciseParameters_V51(
                    1,
                    3,
                    "00:30:00",
                    0
                )
            )
        );

        return new WholeTrainingData_V51[]{
            chestTraining1,
            chestTraining2,
            chestTraining3
        };
    }

    private static WholeTrainingData_V51[] getWholeBackWorkouts() {
        var backTraining1 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na plecy - siła",
                "Trening na plecy - siła",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Martwy ciąg",
                    "Deadlift",
                    BodyPart.BACK,
                    40,
                    null
                ),
                new ExerciseParameters_V51(
                    6,
                    4,
                    null,
                    100
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wiosłowanie sztangą",
                    "Barbell Row",
                    BodyPart.BACK,
                    30,
                    null
                ),
                new ExerciseParameters_V51(
                    8,
                    4,
                    null,
                    60
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Podciąganie na drążku",
                    "Pull-Ups",
                    BodyPart.BACK,
                    20,
                    null
                ),
                new ExerciseParameters_V51(
                    5,
                    4,
                    null,
                    0
                )
            )
        );

        var backTraining2 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na plecy - wytrzymałość",
                "Trening na plecy - wytrzymałość",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wiosłowanie na maszynie",
                    "Machine Row",
                    BodyPart.BACK,
                    20,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    3,
                    null,
                    40
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Ściąganie linki wyciągu górnego",
                    "Lat Pulldown",
                    BodyPart.BACK,
                    15,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    30
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wiosłowanie w opadzie",
                    "Bent Over Row",
                    BodyPart.BACK,
                    25,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    50
                )
            )
        );

        var backTraining3 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na plecy - stabilizacja",
                "Trening na plecy - stabilizacja",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Plank z rotacją",
                    "Plank with Rotation",
                    BodyPart.BACK,
                    10,
                    null
                ),
                new ExerciseParameters_V51(
                    1,
                    3,
                    "00:30:00",
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Superman",
                    "Superman",
                    BodyPart.BACK,
                    8,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Martwy ciąg na jednej nodze",
                    "Single Leg Deadlift",
                    BodyPart.BACK,
                    15,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    3,
                    null,
                    25
                )
            )
        );


        return new WholeTrainingData_V51[]{
            backTraining1,
            backTraining2,
            backTraining3,
        };
    }

    private static WholeTrainingData_V51[] getWholeTricepsWorkouts() {
        var tricepsTraining1 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na triceps - siła",
                "Trening na triceps - siła",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wyciskanie francuskie",
                    "French Press",
                    BodyPart.TRICEPS,
                    25,
                    null
                ),
                new ExerciseParameters_V51(
                    6,
                    4,
                    null,
                    40
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Prostowanie ramion na wyciągu",
                    "Triceps Pushdown",
                    BodyPart.TRICEPS,
                    20,
                    null
                ),
                new ExerciseParameters_V51(
                    8,
                    4,
                    null,
                    30
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Dipy",
                    "Dips",
                    BodyPart.TRICEPS,
                    15,
                    null
                ),
                new ExerciseParameters_V51(
                    5,
                    4,
                    null,
                    0
                )
            )
        );

        var tricepsTraining2 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na triceps - wytrzymałość",
                "Trening na triceps - wytrzymałość",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Prostowanie ramion na wyciągu z linką",
                    "Rope Pushdown",
                    BodyPart.TRICEPS,
                    12,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    25
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wyciskanie hantli w leżeniu",
                    "Dumbbell Triceps Press",
                    BodyPart.TRICEPS,
                    18,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    3,
                    null,
                    25
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Dipy między ławkami",
                    "Bench Dips",
                    BodyPart.TRICEPS,
                    10,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    0
                )
            )
        );

        var tricepsTraining3 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na triceps - stabilizacja",
                "Trening na triceps - stabilizacja",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wyciskanie francuskie jednorącz",
                    "Single Arm French Press",
                    BodyPart.TRICEPS,
                    10,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    20
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wyciskanie hantli w leżeniu",
                    "Dumbbell Triceps Extension",
                    BodyPart.TRICEPS,
                    15,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    3,
                    null,
                    15
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wyciskanie na triceps na wyciągu",
                    "Triceps Cable Extension",
                    BodyPart.TRICEPS,
                    8,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    35
                )
            )
        );


        return new WholeTrainingData_V51[]{
            tricepsTraining1,
            tricepsTraining2,
            tricepsTraining3
        };
    }

    private static WholeTrainingData_V51[] getWholeBicepsWorkouts() {
        var bicepsTraining1 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na biceps - siła",
                "Trening na biceps - siła",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wyciskanie sztangi stojąc",
                    "Barbell Curl",
                    BodyPart.BICEPS,
                    30,
                    null
                ),
                new ExerciseParameters_V51(
                    6,
                    4,
                    null,
                    40
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Uginanie ramion z hantlami",
                    "Dumbbell Curl",
                    BodyPart.BICEPS,
                    20,
                    null
                ),
                new ExerciseParameters_V51(
                    8,
                    4,
                    null,
                    25
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Modlitewnik",
                    "Preacher Curl",
                    BodyPart.BICEPS,
                    25,
                    null
                ),
                new ExerciseParameters_V51(
                    6,
                    4,
                    null,
                    35
                )
            )
        );

        var bicepsTraining2 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na biceps - wytrzymałość",
                "Trening na biceps - wytrzymałość",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wyciskanie sztangi stojąc",
                    "Barbell Curl",
                    BodyPart.BICEPS,
                    20,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    3,
                    null,
                    35
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Uginanie ramion z hantlami",
                    "Dumbbell Curl",
                    BodyPart.BICEPS,
                    18,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    25
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Uginanie ramion na maszynie",
                    "Machine Curl",
                    BodyPart.BICEPS,
                    22,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    30
                )
            )
        );

        var bicepsTraining3 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na biceps - stabilizacja",
                "Trening na biceps - stabilizacja",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Uginanie ramion z hantlami",
                    "Alternating Dumbbell Curl",
                    BodyPart.BICEPS,
                    15,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    20
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wyciskanie sztangi",
                    "Standing Barbell Curl",
                    BodyPart.BICEPS,
                    18,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    3,
                    null,
                    25
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Bicep Curl z linką",
                    "Cable Bicep Curl",
                    BodyPart.BICEPS,
                    10,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    20
                )
            )
        );

        return new WholeTrainingData_V51[]{
            bicepsTraining1,
            bicepsTraining2,
            bicepsTraining3
        };
    }

    private static WholeTrainingData_V51[] getWholeForearmsWorkouts() {
        var forearmsTraining1 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na przedramiona - siła",
                "Trening na przedramiona - siła",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Zginanie nadgarstków ze sztangą",
                    "Barbell Wrist Curl",
                    BodyPart.FOREARMS,
                    30,
                    null
                ),
                new ExerciseParameters_V51(
                    6,
                    4,
                    null,
                    40
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Zginanie nadgarstków z hantlami",
                    "Dumbbell Wrist Curl",
                    BodyPart.FOREARMS,
                    25,
                    null
                ),
                new ExerciseParameters_V51(
                    8,
                    4,
                    null,
                    30
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wzmacnianie nadgarstków na maszynie",
                    "Wrist Curl Machine",
                    BodyPart.FOREARMS,
                    20,
                    null
                ),
                new ExerciseParameters_V51(
                    6,
                    4,
                    null,
                    35
                )
            )
        );

        var forearmsTraining2 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na przedramiona - wytrzymałość",
                "Trening na przedramiona - wytrzymałość",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Zginanie nadgarstków ze sztangą",
                    "Barbell Wrist Curl",
                    BodyPart.FOREARMS,
                    20,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    30
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Zginanie nadgarstków z hantlami",
                    "Dumbbell Wrist Curl",
                    BodyPart.FOREARMS,
                    18,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    25
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Ściskanie ręki - chwyt",
                    "Hand Gripper",
                    BodyPart.FOREARMS,
                    15,
                    null
                ),
                new ExerciseParameters_V51(
                    20,
                    3,
                    null,
                    10
                )
            )
        );

        var forearmsTraining3 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na przedramiona - stabilizacja",
                "Trening na przedramiona - stabilizacja",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Zginanie nadgarstków z hantlami",
                    "Dumbbell Wrist Curl",
                    BodyPart.FOREARMS,
                    18,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    20
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Zginanie nadgarstków na maszynie",
                    "Machine Wrist Curl",
                    BodyPart.FOREARMS,
                    25,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    3,
                    null,
                    30
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wzmacnianie chwytu na drążku",
                    "Dead Hang",
                    BodyPart.FOREARMS,
                    10,
                    null
                ),
                new ExerciseParameters_V51(
                    30,
                    3,
                    null,
                    0
                )
            )
        );

        return new WholeTrainingData_V51[]{
            forearmsTraining1,
            forearmsTraining2,
            forearmsTraining3
        };
    }

    private static WholeTrainingData_V51[] getWholeUpperAbsWorkouts() {
        var upperAbsTraining1 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na górny ABS - siła",
                "Trening na górny ABS - siła",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wznosy nóg w leżeniu",
                    "Leg Raises",
                    BodyPart.UPPER_ABS,
                    25,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Unoszenie tułowia w leżeniu",
                    "Crunches",
                    BodyPart.UPPER_ABS,
                    30,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Skłony boczne",
                    "Side Crunches",
                    BodyPart.UPPER_ABS,
                    20,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    4,
                    null,
                    0
                )
            )
        );

        var upperAbsTraining2 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na górny ABS - wytrzymałość",
                "Trening na górny ABS - wytrzymałość",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Unoszenie nóg w leżeniu",
                    "Leg Raises",
                    BodyPart.UPPER_ABS,
                    20,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Brzuszki na piłce",
                    "Ball Crunches",
                    BodyPart.UPPER_ABS,
                    15,
                    null
                ),
                new ExerciseParameters_V51(
                    20,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Deska - plank",
                    "Plank",
                    BodyPart.UPPER_ABS,
                    10,
                    null
                ),
                new ExerciseParameters_V51(
                    30,
                    3,
                    null,
                    0
                )
            )
        );

        var upperAbsTraining3 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na górny ABS - stabilizacja",
                "Trening na górny ABS - stabilizacja",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Brzuszki z obciążeniem",
                    "Weighted Crunches",
                    BodyPart.UPPER_ABS,
                    30,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    4,
                    null,
                    10
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Unoszenie nóg na poręczach",
                    "Hanging Leg Raises",
                    BodyPart.UPPER_ABS,
                    25,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wznosy tułowia w leżeniu",
                    "Sit-ups",
                    BodyPart.UPPER_ABS,
                    30,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    4,
                    null,
                    0
                )
            )
        );

        return new WholeTrainingData_V51[]{
            upperAbsTraining1,
            upperAbsTraining2,
            upperAbsTraining3
        };
    }

    private static WholeTrainingData_V51[] getWholeLowerBackWorkouts() {
        var lowerBackTraining1 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na dolne plecy - siła",
                "Trening na dolne plecy - siła",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Martwy ciąg klasyczny",
                    "Conventional Deadlift",
                    BodyPart.LOWER_BACK,
                    50,
                    null
                ),
                new ExerciseParameters_V51(
                    8,
                    4,
                    null,
                    100
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Hip Thrust",
                    "Hip Thrust",
                    BodyPart.LOWER_BACK,
                    30,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    4,
                    null,
                    50
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Przyciąganie sztangi w opadzie",
                    "Barbell Row",
                    BodyPart.LOWER_BACK,
                    40,
                    null
                ),
                new ExerciseParameters_V51(
                    8,
                    4,
                    null,
                    60
                )
            )
        );

        var lowerBackTraining2 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na dolne plecy - wytrzymałość",
                "Trening na dolne plecy - wytrzymałość",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wznosy bioder",
                    "Hip Raises",
                    BodyPart.LOWER_BACK,
                    25,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Rumunskie martwe ciągi",
                    "Romanian Deadlift",
                    BodyPart.LOWER_BACK,
                    35,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    40
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Superman",
                    "Superman",
                    BodyPart.LOWER_BACK,
                    20,
                    null
                ),
                new ExerciseParameters_V51(
                    20,
                    3,
                    null,
                    0
                )
            )
        );

        var lowerBackTraining3 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na dolne plecy - stabilizacja",
                "Trening na dolne plecy - stabilizacja",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Deska boczna",
                    "Side Plank",
                    BodyPart.LOWER_BACK,
                    15,
                    null
                ),
                new ExerciseParameters_V51(
                    30,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Unoszenie nóg w zwisie",
                    "Hanging Leg Raises",
                    BodyPart.LOWER_BACK,
                    25,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Martwy ciąg na jednej nodze",
                    "Single Leg Deadlift",
                    BodyPart.LOWER_BACK,
                    30,
                    null
                ),
                new ExerciseParameters_V51(
                    8,
                    4,
                    null,
                    20
                )
            )
        );

        return new WholeTrainingData_V51[]{
            lowerBackTraining1,
            lowerBackTraining2,
            lowerBackTraining3
        };
    }

    private static WholeTrainingData_V51[] getWholeLowerAbsWorkouts() {
        var lowerAbsTraining1 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na dolny ABS - siła",
                "Trening na dolny ABS - siła",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Unoszenie nóg w leżeniu",
                    "Leg Raises",
                    BodyPart.LOWER_ABS,
                    30,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Scyzoryki",
                    "V-Ups",
                    BodyPart.LOWER_ABS,
                    25,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wznosy nóg w zwisie",
                    "Hanging Leg Raises",
                    BodyPart.LOWER_ABS,
                    40,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    3,
                    null,
                    0
                )
            )
        );

        var lowerAbsTraining2 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na dolny ABS - wytrzymałość",
                "Trening na dolny ABS - wytrzymałość",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Deska z unoszeniem nóg",
                    "Plank with Leg Raises",
                    BodyPart.LOWER_ABS,
                    20,
                    null
                ),
                new ExerciseParameters_V51(
                    30,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Unoszenie nóg w podporze",
                    "Knee Raises",
                    BodyPart.LOWER_ABS,
                    25,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Bicycle Crunches",
                    "Bicycle Crunches",
                    BodyPart.LOWER_ABS,
                    30,
                    null
                ),
                new ExerciseParameters_V51(
                    20,
                    3,
                    null,
                    0
                )
            )
        );

        var lowerAbsTraining3 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na dolny ABS - stabilizacja",
                "Trening na dolny ABS - stabilizacja",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Deska",
                    "Plank",
                    BodyPart.LOWER_ABS,
                    15,
                    null
                ),
                new ExerciseParameters_V51(
                    60,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Russian Twists",
                    "Russian Twists",
                    BodyPart.LOWER_ABS,
                    25,
                    null
                ),
                new ExerciseParameters_V51(
                    20,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Mountain Climbers",
                    "Mountain Climbers",
                    BodyPart.LOWER_ABS,
                    30,
                    null
                ),
                new ExerciseParameters_V51(
                    30,
                    3,
                    null,
                    0
                )
            )
        );

        return new WholeTrainingData_V51[]{
            lowerAbsTraining1,
            lowerAbsTraining2,
            lowerAbsTraining3
        };
    }

    private static WholeTrainingData_V51[] getWholeGlutesWorkouts() {
        var glutesTraining1 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na pośladki - siła",
                "Trening na pośladki - siła",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Przysiady ze sztangą",
                    "Barbell Squats",
                    BodyPart.GLUTES,
                    50,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Martwy ciąg",
                    "Deadlift",
                    BodyPart.GLUTES,
                    60,
                    null
                ),
                new ExerciseParameters_V51(
                    8,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wykroki",
                    "Lunges",
                    BodyPart.GLUTES,
                    40,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    0
                )
            )
        );

        var glutesTraining2 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na pośladki - wytrzymałość",
                "Trening na pośladki - wytrzymałość",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Hip Thrust",
                    "Hip Thrust",
                    BodyPart.GLUTES,
                    50,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Przysiady bułgarskie",
                    "Bulgarian Split Squats",
                    BodyPart.GLUTES,
                    40,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Unoszenie nóg w leżeniu",
                    "Leg Raises",
                    BodyPart.GLUTES,
                    30,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    0
                )
            )
        );

        var glutesTraining3 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na pośladki - stabilizacja",
                "Trening na pośladki - stabilizacja",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Deska z unoszeniem nóg",
                    "Plank with Leg Raises",
                    BodyPart.GLUTES,
                    20,
                    null
                ),
                new ExerciseParameters_V51(
                    30,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Glute Bridge",
                    "Glute Bridge",
                    BodyPart.GLUTES,
                    35,
                    null
                ),
                new ExerciseParameters_V51(
                    20,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Kickback",
                    "Kickback",
                    BodyPart.GLUTES,
                    25,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    0
                )
            )
        );

        return new WholeTrainingData_V51[]{
            glutesTraining1,
            glutesTraining2,
            glutesTraining3
        };
    }

    private static WholeTrainingData_V51[] getWholeQuadsWorkouts() {
        var quadsTraining1 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na czwórki - siła",
                "Trening na czwórki - siła",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Przysiady ze sztangą",
                    "Barbell Squats",
                    BodyPart.QUADS,
                    60,
                    null
                ),
                new ExerciseParameters_V51(
                    8,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Przysiady hack",
                    "Hack Squats",
                    BodyPart.QUADS,
                    50,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wykroki ze sztangą",
                    "Barbell Lunges",
                    BodyPart.QUADS,
                    40,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    0
                )
            )
        );

        var quadsTraining2 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na czwórki - wytrzymałość",
                "Trening na czwórki - wytrzymałość",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Przysiady ze sztangą na plecach",
                    "Back Squats",
                    BodyPart.QUADS,
                    55,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wykroki chodzone",
                    "Walking Lunges",
                    BodyPart.QUADS,
                    45,
                    null
                ),
                new ExerciseParameters_V51(
                    20,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Maszyna do prostowania nóg",
                    "Leg Extensions",
                    BodyPart.QUADS,
                    30,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    0
                )
            )
        );

        var quadsTraining3 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na czwórki - stabilizacja",
                "Trening na czwórki - stabilizacja",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Przysiady na jednej nodze",
                    "Single-Leg Squats",
                    BodyPart.QUADS,
                    40,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Step-up z obciążeniem",
                    "Weighted Step-Ups",
                    BodyPart.QUADS,
                    35,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Bulgarian Split Squat",
                    "Bulgarian Split Squat",
                    BodyPart.QUADS,
                    50,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    4,
                    null,
                    0
                )
            )
        );

        return new WholeTrainingData_V51[]{
            quadsTraining1,
            quadsTraining2,
            quadsTraining3
        };
    }

    private static WholeTrainingData_V51[] getWholeHamsWorkouts() {
        var hamsTraining1 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na tylne uda - siła",
                "Trening na tylne uda - siła",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Martwy ciąg klasyczny",
                    "Conventional Deadlift",
                    BodyPart.HAMS,
                    100,
                    null
                ),
                new ExerciseParameters_V51(
                    6,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Martwy ciąg na prostych nogach",
                    "Stiff-Legged Deadlift",
                    BodyPart.HAMS,
                    80,
                    null
                ),
                new ExerciseParameters_V51(
                    8,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wznosy bioder z sztangą",
                    "Hip Thrusts",
                    BodyPart.HAMS,
                    70,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    4,
                    null,
                    0
                )
            )
        );

        var hamsTraining2 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na tylne uda - wytrzymałość",
                "Trening na tylne uda - wytrzymałość",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Martwy ciąg sumo",
                    "Sumo Deadlift",
                    BodyPart.HAMS,
                    90,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Przyciąganie nóg w leżeniu",
                    "Leg Curls",
                    BodyPart.HAMS,
                    50,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Prostowanie nóg w maszynie",
                    "Leg Press Machine",
                    BodyPart.HAMS,
                    60,
                    null
                ),
                new ExerciseParameters_V51(
                    20,
                    3,
                    null,
                    0
                )
            )
        );

        var hamsTraining3 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na tylne uda - stabilizacja",
                "Trening na tylne uda - stabilizacja",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Przysiady bułgarskie",
                    "Bulgarian Split Squats",
                    BodyPart.HAMS,
                    60,
                    null
                ),
                new ExerciseParameters_V51(
                    8,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wykroki w chodzie",
                    "Walking Lunges",
                    BodyPart.HAMS,
                    70,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wznosy bioder z hantlami",
                    "Dumbbell Hip Thrusts",
                    BodyPart.HAMS,
                    55,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    3,
                    null,
                    0
                )
            )
        );

        return new WholeTrainingData_V51[]{
            hamsTraining1,
            hamsTraining2,
            hamsTraining3,
        };
    }

    private static WholeTrainingData_V51[] getWholeCalvesWorkouts() {
        var calvesTraining1 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na łydki - siła",
                "Trening na łydki - siła",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wspięcia na palce w staniu (sztanga)",
                    "Barbell Standing Calf Raises",
                    BodyPart.CALVES,
                    120,
                    null
                ),
                new ExerciseParameters_V51(
                    6,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wspięcia na palce w siadzie",
                    "Seated Calf Raises",
                    BodyPart.CALVES,
                    100,
                    null
                ),
                new ExerciseParameters_V51(
                    8,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wspięcia na palce na maszynie",
                    "Machine Calf Raises",
                    BodyPart.CALVES,
                    90,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    4,
                    null,
                    0
                )
            )
        );

        var calvesTraining2 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na łydki - wytrzymałość",
                "Trening na łydki - wytrzymałość",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wspięcia na palce w staniu (maszyna)",
                    "Machine Standing Calf Raises",
                    BodyPart.CALVES,
                    110,
                    null
                ),
                new ExerciseParameters_V51(
                    15,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wspięcia na palce na suwnicy",
                    "Smith Machine Calf Raises",
                    BodyPart.CALVES,
                    95,
                    null
                ),
                new ExerciseParameters_V51(
                    20,
                    3,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wspięcia na palce w siadzie (maszyna)",
                    "Seated Machine Calf Raises",
                    BodyPart.CALVES,
                    80,
                    null
                ),
                new ExerciseParameters_V51(
                    20,
                    3,
                    null,
                    0
                )
            )
        );

        var calvesTraining3 = new WholeTrainingData_V51(
            new Training_V51(
                "Trening na łydki - stabilizacja",
                "Trening na łydki - stabilizacja",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wspięcia na palce na jednej nodze",
                    "Single Leg Calf Raises",
                    BodyPart.CALVES,
                    60,
                    null
                ),
                new ExerciseParameters_V51(
                    12,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wspięcia na palce z hantlami",
                    "Dumbbell Calf Raises",
                    BodyPart.CALVES,
                    70,
                    null
                ),
                new ExerciseParameters_V51(
                    10,
                    4,
                    null,
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Skakanie na skakance",
                    "Jump Rope",
                    BodyPart.CALVES,
                    0,
                    null
                ),
                new ExerciseParameters_V51(
                    20,
                    3,
                    "00:05:00",
                    0
                )
            )
        );

        return new WholeTrainingData_V51[]{
            calvesTraining1,
            calvesTraining2,
            calvesTraining3
        };
    }

}

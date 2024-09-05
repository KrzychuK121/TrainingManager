package springweb.training_manager.controllers.with_views;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springweb.training_manager.models.entities.BodyPart;
import springweb.training_manager.models.entities.Difficulty;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.schemas.RoleSchema;
import springweb.training_manager.models.viewmodels.exercise.ExerciseRead;
import springweb.training_manager.models.viewmodels.exercise.ExerciseWrite;
import springweb.training_manager.models.viewmodels.training.TrainingExercise;
import springweb.training_manager.repositories.for_controllers.TrainingRepository;
import springweb.training_manager.services.ExerciseService;
import springweb.training_manager.services.PageSortService;

import java.net.URI;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/exercise")
public class ExerciseController {
    private final ExerciseService service;
    private final TrainingRepository trainingRepo;
    private static final Logger logger = LoggerFactory.getLogger(ExerciseController.class);

    public ExerciseController(
        final ExerciseService service,
        final TrainingRepository trainingRepo
    ) {
        this.service = service;
        this.trainingRepo = trainingRepo;
    }

    private void setDifficulty(Model model){
        model.addAttribute("difficultyArray", Difficulty.values());
        ArrayList<String> desc = new ArrayList<>(Difficulty.values().length);
        for(Difficulty difficulty : Difficulty.values())
            desc.add(Difficulty.getEnumDesc(difficulty));
        model.addAttribute("difficultyDescArray", desc);
    }

    private void setBodyPartList(Model model){
        model.addAttribute("bodyPartArray", BodyPart.values());
    }

    @ModelAttribute("title")
    String getTitle(){
        return "TrainingM - Ćwiczenia";
    }

    List<ExerciseRead> getExercises(
        Pageable page,
        Model model
    ){
        Page<ExerciseRead> pagedList =  service.getAll(page);
        model.addAttribute("pages", pagedList);
        return pagedList.getContent();
    }

    private void prepTrainingSelect(Model model){
        prepTrainingSelect(model, new  String[]{});
    }

    private void prepTrainingSelect(Model model, String[] selected){
        List<TrainingExercise> trainingSelectList = TrainingExercise.toTrainingExerciseList(trainingRepo.findAll());

        if(selected.length > trainingSelectList.size())
            throw new IllegalStateException("Lista zaznaczonych elementów nie może być większa jak lista wszystkich elementów.");
        model.addAttribute("allTrainings", trainingSelectList);
        if(selected.length != 0){
            List<Integer> selectedInt = new ArrayList<>();
            for(String sel : selected){
                if(sel.isBlank())
                    continue;
                selectedInt.add(Integer.parseInt(sel));
            }
            model.addAttribute("selected", selectedInt);
        }

    }

    @Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
    @GetMapping(
        value = "/create",
        produces = MediaType.TEXT_HTML_VALUE
    )
    public String createView(Model model){
        model.addAttribute("exercise", new ExerciseWrite());
        model.addAttribute("action", "create");
        setDifficulty(model);
        setBodyPartList(model);
        prepTrainingSelect(model);
        return "exercise/save";
    }

    @Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
    @PostMapping(
        value = "/create",
        produces = MediaType.TEXT_HTML_VALUE,
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public String createView(
        @ModelAttribute("exercise") @Valid ExerciseWrite toSave,
        BindingResult result,
        String time,
        String[] trainingIds,
        Model model
    ){

        if(result.hasErrors()){
            model.addAttribute("action", "create");
            setDifficulty(model);
            setBodyPartList(model);
            prepTrainingSelect(model, trainingIds);
            return "exercise/save";
        }

        setTrainingsById(toSave, trainingIds);

        setTime(toSave, time);

        service.create(toSave);
        prepTrainingSelect(model);
        model.addAttribute("action", "create");
        model.addAttribute("exercise", new ExerciseWrite());
        setDifficulty(model);
        setBodyPartList(model);
        model.addAttribute("message", "Utworzono nowe ćwiczenie!");

        return "exercise/save";
    }

    private static void setTime(ExerciseWrite toSave, String time) {
        if(time != null && !time.isEmpty()){
            String[] times = time.split(":");
            LocalTime timeToSave = LocalTime.of(
                0,
                Integer.parseInt(times[0]),
                Integer.parseInt(times[1])
            );

            toSave.setTime(timeToSave);
        }
    }

    private void setTrainingsById(ExerciseWrite toSave, String[] trainingIds) {
        if(trainingIds != null && trainingIds.length != 0){
            List<TrainingExercise> trainingsToSave = new ArrayList<>(trainingIds.length);
            for(String trainingID : trainingIds){
                if(trainingID.isEmpty())
                    continue;
                int id = Integer.parseInt(trainingID);
                Training found = trainingRepo.findById(id).get();
                TrainingExercise viewModel = new TrainingExercise(found, found.getId());
                trainingsToSave.add(viewModel);
            }
            toSave.setTrainings(trainingsToSave);
        }
    }

    @Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String getAllView(
        Pageable page,
        Model model
    ){
        initIndexModel(page, model);
        return "exercise/index";
    }

    private void initIndexModel(Pageable page, Model model) {
        model.addAttribute("exercises", getExercises(page, model));
        PageSortService.setSortModels(page, model, "id");
    }

    // TODO: Make implementation of this method
    /*@GetMapping(
        value = "/api/{userId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<ExerciseRead>> getByUserId(@PathVariable String userId){
        return ResponseEntity.noContent().build();
    }*/

    @Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
    @GetMapping("/edit/{id}")
    public String editView(
        @PathVariable int id,
        Model model
    ){
        ExerciseRead toEdit = null;
        try {
            toEdit = new ExerciseRead(service.getById(id));
        } catch(IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            model.addAttribute("messType", "danger");
            model.addAttribute("mess", "Nie można edytować. " + e.getMessage());
            return "exercise/index";
        }

        String[] selected = getToEditTrainingIds(toEdit);

        prepTrainingSelect(model, selected);
        model.addAttribute("action", "edit/" + id);
        model.addAttribute("exercise", toEdit);
        setDifficulty(model);
        setBodyPartList(model);
        model.addAttribute("id", id);
        return "exercise/save";
    }

    private static String[] getToEditTrainingIds(ExerciseRead toEdit) {
        List<TrainingExercise> toEditList = toEdit.getTrainings();
        String[] selected = new String[toEditList.size()];
        for(int i = 0; i < toEditList.size(); i++){
            selected[i] = toEditList.get(i).getId() + "";
        }
        return selected;
    }


    @Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
    @PostMapping("/edit/{id}")
    public String editView(
        @PathVariable int id,
        @ModelAttribute("exercise") @Valid ExerciseWrite toEdit,
        BindingResult result,
        String time,
        String[] trainingIds,
        Pageable page,
        Model model
    ){
        if(result.hasErrors()){
            model.addAttribute("action", "edit/" + id);
            setDifficulty(model);
            setBodyPartList(model);
            prepTrainingSelect(model, trainingIds);
            return "exercise/save";
        }

        setTrainingsById(toEdit, trainingIds);
        setTime(toEdit, time);

        try {
            service.edit(toEdit, id);
        } catch(IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            model.addAttribute("exercises", getExercises(page, model));
            model.addAttribute("message", "Wystąpił problem przy edycji. " + e.getMessage());
            return "exercise/index";
        }

        setDifficulty(model);
        setBodyPartList(model);
        model.addAttribute("messType", "success");
        model.addAttribute("mess", "Edycja przeszła pomyślnie.");
        initIndexModel(page, model);
        return "exercise/index";
    }

    @Secured(RoleSchema.ROLE_ADMIN)
    @GetMapping(
        value = "/delete/{id}",
        produces = MediaType.TEXT_HTML_VALUE
    )
    public String deleteView(
        @PathVariable int id,
        Pageable page,
        Model model
    ){
        try {
            service.delete(id);
        } catch(IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            model.addAttribute("messType", "danger");
            model.addAttribute("mess", "Nie można usunąć. " + e.getMessage());
            return "exercise/index";
        } finally {
            initIndexModel(page, model);
        }
        model.addAttribute("mess", "Pomyślnie usunięto wiersz.");
        model.addAttribute("messType", "success");
        return "exercise/index";
    }

}

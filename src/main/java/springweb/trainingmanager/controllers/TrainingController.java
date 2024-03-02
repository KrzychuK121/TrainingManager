package springweb.trainingmanager.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springweb.trainingmanager.models.entities.Exercise;
import springweb.trainingmanager.models.entities.Training;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseRead;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseTraining;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseWrite;
import springweb.trainingmanager.models.viewmodels.training.TrainingExercise;
import springweb.trainingmanager.models.viewmodels.training.TrainingRead;
import springweb.trainingmanager.models.viewmodels.training.TrainingWrite;
import springweb.trainingmanager.repositories.forcontrollers.ExerciseRepository;
import springweb.trainingmanager.services.TrainingService;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/training")
public class TrainingController {
    private final TrainingService service;
    private final ExerciseRepository exerciseRepo;
    private static final Logger logger = LoggerFactory.getLogger(TrainingController.class);

    public TrainingController(
        final TrainingService service,
        final ExerciseRepository exerciseRepo
    ) {
        this.service = service;
        this.exerciseRepo = exerciseRepo;
    }

    @ModelAttribute("title")
    String getTitle(){ return "TrainingM - Treningi"; }

    List<TrainingRead> getTrainings(){
        return TrainingRead.toTrainingReadList(service.getAll());
    }

    @PostMapping(
        value = "/api",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    ResponseEntity<TrainingRead> create(@RequestBody @Valid TrainingWrite toCreate){
        Training created = service.create(toCreate);
        var trainingRead = new TrainingRead(created);
        return ResponseEntity.created(
            URI.create("/training/" + created.getId())
        ).body(trainingRead);
    }

    private void prepExerciseSelect(Model model){
        prepExerciseSelect(model, new  String[]{});
    }

    private void prepExerciseSelect(Model model, String[] selected){
        List<ExerciseTraining> exerciseSelectList = ExerciseTraining.toExerciseTrainingList(exerciseRepo.findAll());
        if(selected != null && selected.length != 0 && selected.length != exerciseSelectList.size())
            throw new IllegalStateException("Lista zaznaczonych elementów nie może mieć innej wielkości jak lista wszystkich elementów.");
        model.addAttribute("allExercises", exerciseSelectList);
        if(selected.length != 0){
            List<Integer> selectedInt = new ArrayList<>();
            for(String sel : selected)
                selectedInt.add(Integer.parseInt(sel));
            model.addAttribute("selected", selectedInt);
        }

    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping(
        value = "/create",
        produces = MediaType.TEXT_HTML_VALUE
    )
    String createView(Model model){
        logger.info("Training create get");
        model.addAttribute("training", new TrainingWrite());
        prepExerciseSelect(model);
        return "training/save";
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping(
        value = "/create",
        params = "addExercise",
        produces = MediaType.TEXT_HTML_VALUE
    )
    String addExercise(
        @ModelAttribute("training") TrainingWrite current,
        Model model,
        String[] exerciseIds
    ){
        logger.info("Training create addExercise");
        prepExerciseSelect(model, exerciseIds);
        current.getExercises().add(new ExerciseTraining());
        return "training/save";
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping(
        value = "/create",
        produces = MediaType.TEXT_HTML_VALUE,
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    String createView(
        @ModelAttribute("training") @Valid TrainingWrite toSave,
        BindingResult result,
        Model model,
        String[] exerciseIds
    ){
        if(result.hasErrors()){
            prepExerciseSelect(model, exerciseIds);
            return "training/save";
        }

        setExercisesById(toSave, exerciseIds);

        service.create(toSave);
        prepExerciseSelect(model);
        model.addAttribute("training", new TrainingWrite());
        model.addAttribute("message", "Utworzono nowy trening!");

        return "training/save";
    }

    private void setExercisesById(TrainingWrite toSave, String[] exercisesIds) {
        if(exercisesIds == null || exercisesIds.length == 0)
            return;
        List<ExerciseTraining> exercisesToSave = new ArrayList<>(exercisesIds.length);
        for(String exerciseID : exercisesIds){
            if(exerciseID.isEmpty())
                continue;
            int id = Integer.parseInt(exerciseID);
            Exercise found = exerciseRepo.findById(id).get();
            ExerciseTraining viewmodel = new ExerciseTraining(found);
            exercisesToSave.add(viewmodel);
        }

        List<ExerciseTraining> currExercises = toSave.getExercises();
        currExercises.addAll(exercisesToSave);
        toSave.setExercises(currExercises);
    }

    @GetMapping(
        value = "/api",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    ResponseEntity<List<TrainingRead>> getAll(){
        return ResponseEntity.ok(
            TrainingRead.toTrainingReadList(service.getAll())
        );
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping(
        produces = MediaType.TEXT_HTML_VALUE
    )
    String getAllView(Model model){
        model.addAttribute("trainings", getTrainings());
        return "training/index";
    }

    @GetMapping(
        value = "/api/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    ResponseEntity<Training> getById(@PathVariable int id){
        Training found = null;
        try {
            found = service.getById(id);
        } catch(IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(found);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping(
        value = "/train/{id}",
        produces = MediaType.TEXT_HTML_VALUE
    )
    String train(
        @PathVariable int id,
        Model model
    ){
        Training found = null;
        try {
            found = service.getById(id);
        } catch(IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            model.addAttribute("messType", "danger");
            model.addAttribute("mess", e.getMessage());
            model.addAttribute("trainings", getTrainings());
            return "training/index";
        }

        if(found.getExercises().isEmpty()){
            logger.warn("Wystąpił problem: brak ćwiczeń");
            model.addAttribute("messType", "danger");
            model.addAttribute("mess", "Wybierz trening zawierający ćwiczenia!");
            model.addAttribute("trainings", getTrainings());
            return "training/index";
        }

        model.addAttribute("training", new TrainingRead(found));
        return "training/train";
    }

    @PutMapping(
        value = "/api/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    ResponseEntity<?> edit(
        @RequestBody @Valid TrainingWrite toEdit,
        @PathVariable int id
    ){
        try {
            service.edit(toEdit, id);
        } catch(IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/edit/{id}")
    public String editView(
        @PathVariable int id,
        Model model
    ){
        TrainingRead toEdit = null;
        try {
            toEdit = new TrainingRead(service.getById(id));
        } catch(IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            model.addAttribute("messType", "danger");
            model.addAttribute("mess", "Nie można edytować. " + e.getMessage());
            return "exercise/index";
        }

        prepExerciseSelect(model);
        model.addAttribute("exercise", toEdit);
        model.addAttribute("id", id);
        return "exercise/save";
    }


    @PostMapping("/create/{id}")
    public String editView(
        @PathVariable int id,
        @ModelAttribute("exercise") @Valid TrainingWrite toEdit,
        BindingResult result,
        String[] exerciseIds,
        Model model
    ){
        if(result.hasErrors()){
            prepExerciseSelect(model, exerciseIds);
            return "exercise/save";
        }

        setExercisesById(toEdit, exerciseIds);

        try {
            service.edit(toEdit, id);
        } catch(IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            model.addAttribute("message", "Wystąpił problem przy edycji. " + e.getMessage());
        }

        model.addAttribute("trainings", getTrainings());
        model.addAttribute("messType", "success");
        model.addAttribute("mess", "Edycja przeszła pomyślnie.");
        return "exercise/index";
    }


    @DeleteMapping(
        value = "/api/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable int id){
        try {
            service.delete(id);
        } catch(IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(
        value = "/delete/{id}",
        produces = MediaType.TEXT_HTML_VALUE
    )
    public String deleteView(
        @PathVariable int id,
        Model model
    ){
        try {
            service.delete(id);
        } catch(IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            model.addAttribute("messType", "danger");
            model.addAttribute("mess", "Nie można usunąć. " + e.getMessage());
            return "training/index";
        } finally {
            model.addAttribute("trainings", getTrainings());
        }
        model.addAttribute("mess", "Pomyślnie usunięto wiersz.");
        model.addAttribute("messType", "success");
        return "training/index";
    }

}

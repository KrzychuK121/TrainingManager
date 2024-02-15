package springweb.trainingmanager.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springweb.trainingmanager.models.entities.Training;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseTraining;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseWrite;
import springweb.trainingmanager.models.viewmodels.training.TrainingRead;
import springweb.trainingmanager.models.viewmodels.training.TrainingWrite;
import springweb.trainingmanager.services.ExerciseService;
import springweb.trainingmanager.services.TrainingService;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/training")
public class TrainingController {
    private final ExerciseService exerciseService;
    private final TrainingService service;
    private static final Logger logger = LoggerFactory.getLogger(TrainingController.class);

    public TrainingController(
        final ExerciseService exerciseService,
        final TrainingService service
    ) {
        this.exerciseService = exerciseService;
        this.service = service;
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

        var trainingRead = new TrainingRead(created, created.getId());
        return ResponseEntity.created(
            URI.create("/training/" + created.getId())
        ).body(trainingRead);
    }

    @GetMapping(
        value = "/create",
        produces = MediaType.TEXT_HTML_VALUE
    )
    String createView(Model model){
        logger.info("Training create get");
        model.addAttribute("training", new TrainingWrite());
        return "training/save";
    }

    @PostMapping(
        value = "/create",
        params = "addExercise",
        produces = MediaType.TEXT_HTML_VALUE
    )
    String addExercise(@ModelAttribute("training") TrainingWrite current){
        logger.info("Training create addExercise");
        current.getExercises().add(new ExerciseTraining());
        return "training/save";
    }

    @PostMapping(
        value = "/create",
        produces = MediaType.TEXT_HTML_VALUE,
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    String createView(
        @ModelAttribute("training") @Valid TrainingWrite toSave,
        BindingResult result,
        Model model
    ){
        if(result.hasErrors())
            return "training/save";

        service.create(toSave);
        model.addAttribute("training", new TrainingWrite());
        model.addAttribute("message", "Utworzono nowy trening!");

        return "training/save";
    }

    @GetMapping(
        value = "/api",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    ResponseEntity<List<Training>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

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
            model.addAttribute("errorMessage", e.getMessage());
            return "training/index";
        }

        if(found.getExercises().size() <= 1){
            logger.warn("Wystąpił problem: brak ćwiczeń");
            model.addAttribute("errorMessage", "Wybierz trening zawierający ćwiczenia!");
            model.addAttribute("trainings", getTrainings());
            return "training/index";
        }

        model.addAttribute("training", new TrainingRead(found, id));
        return "training/train";
    }

    @PutMapping(
        value = "/api/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    ResponseEntity<?> edit(
        @RequestBody @Valid Training toEdit,
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


}

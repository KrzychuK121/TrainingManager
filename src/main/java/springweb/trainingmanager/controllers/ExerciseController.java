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
import springweb.trainingmanager.models.entities.Exercise;
import springweb.trainingmanager.models.entities.Training;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseRead;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseWrite;
import springweb.trainingmanager.models.viewmodels.training.TrainingExercise;
import springweb.trainingmanager.repositories.forcontrollers.ExerciseRepository;
import springweb.trainingmanager.repositories.forcontrollers.TrainingRepository;
import springweb.trainingmanager.services.ExerciseService;
import springweb.trainingmanager.services.TrainingService;

import java.net.URI;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/exercise")
public class ExerciseController {
    private final ExerciseService service;
    private static final Logger logger = LoggerFactory.getLogger(ExerciseController.class);

    public ExerciseController(
        final ExerciseService service
    ) {
        this.service = service;
    }

    @ModelAttribute("title")
    String getTitle(){
        return "TrainingM - Ćwiczenia";
    }

    List<ExerciseRead> getExercises(){
        return ExerciseRead.toExerciseReadList(service.getAll());
    }

    @PostMapping(
        value = "/api",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<ExerciseRead> create(@RequestBody @Valid ExerciseWrite toSave){
        Exercise created = service.create(toSave);

        var exerciseRead = new ExerciseRead(created);
        return ResponseEntity.created(
            URI.create("/exercise/" + created.getId())
        ).body(exerciseRead);
    }

    @GetMapping(
        value = "/create",
        produces = MediaType.TEXT_HTML_VALUE
    )
    public String createView(Model model){
        model.addAttribute("exercise", new ExerciseWrite());
        return "exercise/save";
    }

    @PostMapping(
        value = "/create",
        produces = MediaType.TEXT_HTML_VALUE,
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public String createView(
        @ModelAttribute("exercise") @Valid ExerciseWrite toSave,
        BindingResult result,
        String time,
        Model model
    ){
        if(result.hasErrors())
            return "exercise/save";

        if(time != null && !time.isEmpty()){
            String[] times = time.split(":");
            LocalTime timeToSave = LocalTime.of(
                    0,
                    Integer.parseInt(times[0]),
                    Integer.parseInt(times[1])
            );

            toSave.setTime(timeToSave);
        }

        service.create(toSave);
        model.addAttribute("exercise", new ExerciseWrite());
        model.addAttribute("message", "Utworzono nowe ćwiczenie!");

        return "exercise/save";
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String getAllView(Model model){
        model.addAttribute("exercises", getExercises());
        return "exercise/index";
    }

    @GetMapping(
        value = "/api",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<ExerciseRead>> getAll(){
        return ResponseEntity.ok(
            ExerciseRead.toExerciseReadList(
                service.getAll()
            )
        );
    }

    @GetMapping(
        value = "/api/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<Exercise> getById(@PathVariable int id){
        Exercise found = null;
        try {
            found = service.getById(id);
        } catch(IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(found);
    }

    @PutMapping(
        value = "/api/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> edit(
        @RequestBody @Valid ExerciseWrite toEdit,
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

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable int id){
        try{
            service.delete(id);
        } catch(IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }



}

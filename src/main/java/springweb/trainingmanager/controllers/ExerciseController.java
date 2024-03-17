package springweb.trainingmanager.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springweb.trainingmanager.models.entities.Exercise;
import springweb.trainingmanager.models.entities.Training;
import springweb.trainingmanager.models.schemas.RoleSchema;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseRead;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseWrite;
import springweb.trainingmanager.models.viewmodels.training.TrainingExercise;
import springweb.trainingmanager.models.viewmodels.training.TrainingRead;
import springweb.trainingmanager.repositories.forcontrollers.TrainingRepository;
import springweb.trainingmanager.services.ExerciseService;

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
            prepTrainingSelect(model, trainingIds);
            return "exercise/save";
        }

        setTrainingsById(toSave, trainingIds);

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
        prepTrainingSelect(model);
        model.addAttribute("action", "create");
        model.addAttribute("exercise", new ExerciseWrite());
        model.addAttribute("message", "Utworzono nowe ćwiczenie!");

        return "exercise/save";
    }

    private void setTrainingsById(ExerciseWrite toSave, String[] trainingIds) {
        if(trainingIds != null && trainingIds.length != 0){
            List<TrainingExercise> trainingsToSave = new ArrayList<>(trainingIds.length);
            for(String trainingID : trainingIds){
                if(trainingID.isEmpty())
                    continue;
                int id = Integer.parseInt(trainingID);
                Training found = trainingRepo.findById(id).get();
                TrainingExercise viewmodel = new TrainingExercise(found, found.getId());
                trainingsToSave.add(viewmodel);
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
        model.addAttribute("exercises", getExercises(page, model));
        var order = page.getSort().get()
            .findFirst()
            .orElse(
                new Sort.Order(
                    Sort.Direction.ASC,
                    "id"
                )
            );
        model.addAttribute("currOrder", order);
        model.addAttribute("newOrder", order.reverse());
        return "exercise/index";
    }

    @GetMapping(
        value = "/api",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<ExerciseRead>> getAll(
        Pageable page
    ){
        return ResponseEntity.ok(
            service.getAll(page)
            .getContent()
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

    // TODO: Make implementation of this method
    /*@GetMapping(
        value = "/api/{userId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<ExerciseRead>> getByUserId(@PathVariable String userId){
        return ResponseEntity.noContent().build();
    }*/

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
        String[] trainingIds,
        Pageable page,
        Model model
    ){
        if(result.hasErrors()){
            model.addAttribute("action", "edit/" + id);
            prepTrainingSelect(model, trainingIds);
            return "exercise/save";
        }

        setTrainingsById(toEdit, trainingIds);

        try {
            service.edit(toEdit, id);
        } catch(IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            model.addAttribute("message", "Wystąpił problem przy edycji. " + e.getMessage());
        }

        model.addAttribute("exercises", getExercises(page, model));
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
            model.addAttribute("exercises", getExercises(page, model));
        }
        model.addAttribute("mess", "Pomyślnie usunięto wiersz.");
        model.addAttribute("messType", "success");
        return "exercise/index";
    }

}

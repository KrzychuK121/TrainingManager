package springweb.training_manager.controllers.with_views;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.schemas.RoleSchema;
import springweb.training_manager.models.viewmodels.exercise.ExerciseTraining;
import springweb.training_manager.models.viewmodels.exercise_parameters.ExerciseParametersWrite;
import springweb.training_manager.models.viewmodels.training.TrainingRead;
import springweb.training_manager.models.viewmodels.training.TrainingWrite;
import springweb.training_manager.models.viewmodels.training_exercise.CustomTrainingParametersWrite;
import springweb.training_manager.repositories.for_controllers.ExerciseRepository;
import springweb.training_manager.services.PageSortService;
import springweb.training_manager.services.TrainingService;
import springweb.training_manager.services.UserService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/training")
public class TrainingController {
    private final TrainingService service;
    private final ExerciseRepository exerciseRepo;
    private static final Logger logger = LoggerFactory.getLogger(TrainingController.class);

    @ModelAttribute("title")
    String getTitle() {
        return "TrainingM - Treningi";
    }

    List<TrainingRead> getTrainings(
        Authentication auth,
        @PageableDefault(size = 2) Pageable page,
        Model model
    ) {
        String userId = UserService.getUserIdByAuth(auth);
        Page<TrainingRead> pagedList = null;
        if (userId == null) {
            pagedList = service.getPagedAll(page);
            model.addAttribute("pages", pagedList);
        }

        return userId != null ?
            service.getByUserId(userId) :
            pagedList.getContent();
    }

    private void prepExerciseSelect(Model model) {
        prepExerciseSelect(model, new String[]{});
    }

    private void prepExerciseSelect(Model model, String[] selected) {
        List<ExerciseTraining> exerciseSelectList = ExerciseTraining.toExerciseTrainingList(exerciseRepo.findAll());
        if (selected != null && selected.length > exerciseSelectList.size())
            throw new IllegalStateException("Lista zaznaczonych elementów nie może być mniejsza niż lista wszystkich elementów.");
        model.addAttribute("allExercises", exerciseSelectList);
        if (selected != null && selected.length != 0) {
            List<Integer> selectedInt = new ArrayList<>();
            for (String sel : selected) {
                if (sel.isBlank())
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
    String createView(Model model) {
        logger.info("Training create get");
        model.addAttribute("training", new TrainingWrite());
        model.addAttribute("action", "create");
        prepExerciseSelect(model);
        return "training/save";
    }

    @Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
    @PostMapping(
        value = {"/create", "/edit/*"},
        params = "addExercise",
        produces = MediaType.TEXT_HTML_VALUE
    )
    String addExercise(
        @ModelAttribute("training") TrainingWrite current,
        @ModelAttribute("action") String action,
        Model model,
        String[] exerciseIds
    ) {
        logger.info("Training create addExercise with action: " + action);
        prepExerciseSelect(model, exerciseIds);
        current.getExercises()
            .add(
                new CustomTrainingParametersWrite(
                    new ExerciseTraining(
                        new Exercise()
                    ),
                    new ExerciseParametersWrite()
                )
            );
        return "training/save";
    }

    @Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
    @PostMapping(
        value = "/create",
        produces = MediaType.TEXT_HTML_VALUE,
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    String createView(
        @ModelAttribute("training") @Valid TrainingWrite toSave,
        BindingResult result,
        Model model,
        Authentication auth,
        String[] exerciseIds
    ) {
        if (result.hasErrors()) {
            model.addAttribute("action", "create");
            prepExerciseSelect(model, exerciseIds);
            return "training/save";
        }

        service.setExercisesById(toSave, exerciseIds);

        service.create(toSave, UserService.getUserIdByAuth(auth));
        prepExerciseSelect(model);
        model.addAttribute("action", "create");
        model.addAttribute("training", new TrainingWrite());
        model.addAttribute("message", "Utworzono nowy trening!");

        return "training/save";
    }

    @Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
    @GetMapping(
        produces = MediaType.TEXT_HTML_VALUE
    )
    String getAllView(
        Model model,
        Authentication auth,
        @PageableDefault(size = 2) Pageable page
    ) {
        model.addAttribute("trainings", getTrainings(auth, page, model));
        PageSortService.setSortModels(page, model, "id");
        return "training/index";
    }

    @GetMapping(
        value = "/api/users/{userId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    ResponseEntity<List<TrainingRead>> getByUserId(@PathVariable String userId) {
        List<TrainingRead> usersTrainings = service.getByUserId(userId);
        return ResponseEntity.ok(usersTrainings);
    }

    @Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
    @GetMapping(
        value = "/train/{id}",
        produces = MediaType.TEXT_HTML_VALUE
    )
    String train(
        @PathVariable int id,
        Model model,
        Authentication auth,
        @PageableDefault(size = 2) Pageable page
    ) {
        Training found = null;
        try {
            found = service.getById(id, UserService.getUserIdByAuth(auth));
        } catch (IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            model.addAttribute("messType", "danger");
            model.addAttribute("mess", e.getMessage());
            model.addAttribute("trainings", getTrainings(auth, page, model));
            return "training/index";
        }

        if (found.getExercises()
            .isEmpty()) {
            logger.warn("Wystąpił problem: brak ćwiczeń");
            model.addAttribute("messType", "danger");
            model.addAttribute("mess", "Wybierz trening zawierający ćwiczenia!");
            model.addAttribute("trainings", getTrainings(auth, page, model));
            return "training/index";
        }

        model.addAttribute("training", new TrainingRead(found));
        return "training/train";
    }

    @Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
    @GetMapping("/edit/{id}")
    public String editView(
        @PathVariable int id,
        Model model,
        Authentication auth,
        @PageableDefault(size = 2) Pageable page
    ) {
        TrainingRead toEdit = null;
        try {
            toEdit = new TrainingRead(service.getById(id, UserService.getUserIdByAuth(auth)));
        } catch (IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            model.addAttribute("messType", "danger");
            model.addAttribute("mess", "Nie można edytować. " + e.getMessage());
            model.addAttribute("trainings", getTrainings(auth, page, model));
            return "training/index";
        }

        String[] selected = getToEditExerciseIds(toEdit);

        prepExerciseSelect(model, selected);
        model.addAttribute("action", "edit/" + id);
        model.addAttribute("training", toEdit);
        model.addAttribute("id", id);
        return "training/save";
    }


    private static String[] getToEditExerciseIds(TrainingRead toEdit) {
        List<ExerciseTraining> toEditList = toEdit.getExercises();
        String[] selected = new String[toEditList.size()];
        for (int i = 0; i < toEditList.size(); i++) {
            selected[i] = toEditList.get(i)
                .getId() + "";
        }
        return selected;
    }

    @Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
    @PostMapping(
        value = "/edit/{id}",
        params = "!addExercise"
    )
    public String editView(
        @PathVariable int id,
        @ModelAttribute("exercise") @Valid TrainingWrite toEdit,
        BindingResult result,
        Model model,
        Authentication auth,
        @PageableDefault(size = 2) Pageable page,
        String[] exerciseIds
    ) {
        if (result.hasErrors()) {
            model.addAttribute("action", "edit/" + id);
            prepExerciseSelect(model, exerciseIds);
            return "training/save";
        }

        service.setExercisesById(toEdit, exerciseIds);

        try {
            service.edit(toEdit, id, UserService.getUserIdByAuth(auth));
        } catch (IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            model.addAttribute("message", "Wystąpił problem przy edycji. " + e.getMessage());
        }

        model.addAttribute("trainings", getTrainings(auth, page, model));
        model.addAttribute("messType", "success");
        model.addAttribute("mess", "Edycja przeszła pomyślnie.");
        return "training/index";
    }

    @Secured(RoleSchema.ROLE_ADMIN)
    @GetMapping(
        value = "/delete/{id}",
        produces = MediaType.TEXT_HTML_VALUE
    )
    public String deleteView(
        @PathVariable int id,
        Model model,
        Authentication auth,
        @PageableDefault(size = 2) Pageable page
    ) {
        try {
            service.delete(id, UserService.getUserIdByAuth(auth));
        } catch (IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            model.addAttribute("messType", "danger");
            model.addAttribute("mess", "Nie można usunąć. " + e.getMessage());
            return "training/index";
        } finally {
            model.addAttribute("trainings", getTrainings(auth, page, model));
        }
        model.addAttribute("mess", "Pomyślnie usunięto wiersz.");
        model.addAttribute("messType", "success");
        return "training/index";
    }

}

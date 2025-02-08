package springweb.training_manager.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.entities.UserRequest;
import springweb.training_manager.models.view_models.user_request.UserRequestRead;
import springweb.training_manager.models.view_models.user_request.UserRequestWrite;
import springweb.training_manager.models.view_models.validation.ValidationErrors;
import springweb.training_manager.repositories.for_controllers.UserRequestRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserRequestService {
    private final UserRequestRepository repository;

    public Page<UserRequestRead> getAllPaged(
        Pageable page,
        User loggedUser
    ) {
        Function<Pageable, Page<UserRequest>> find = UserService.isAtLeastModerator(loggedUser)
            ? repository::findAll
            : pageable -> repository.findAllByRequesterId(loggedUser.getId(), pageable);

        return PageSortService.getPageBy(
            UserRequest.class,
            page,
            find,
            UserRequestRead::new,
            log
        );
    }

    private UserRequest getById(int requestId) {
        return repository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Request with id " + requestId + " does not exist."));
    }

    public UserRequestRead getByIdRead(int requestId) {
        return new UserRequestRead(getById(requestId));
    }

    public Map<String, List<String>> validateAndPrepare(
        UserRequestWrite data,
        BindingResult result
    ) {
        if (result.hasErrors()) {
            var validation = ValidationErrors.createFrom(result);
            return validation.getErrors();
        }

        return null;
    }

    public void create(
        UserRequestWrite toSave,
        User loggedUser
    ) {
        if (
            repository.existsByTitleAndDescription(
                toSave.getTitle(),
                toSave.getDescription()
            )
        )
            throw new IllegalArgumentException("Request with provided title and description already exists.");

        var entityToSave = new UserRequest(
            toSave.getTitle(),
            toSave.getDescription(),
            loggedUser
        );
        repository.save(entityToSave);
    }

    public void closeRequest(
        int requestId,
        User loggedUser
    ) {
        var requestToClose = getById(requestId);
        if (requestToClose.getUserClosingId() != null)
            throw new IllegalStateException("Request " + requestId + " is already closed.");
        requestToClose.setClosing(loggedUser);
        repository.save(requestToClose);
    }

    public void delete(
        int requestId,
        User loggedUser
    ) {
        var requestToDelete = getById(requestId);
        if (
            !UserService.isPermittedToModifyFor(loggedUser, requestToDelete.getRequester())
        )
            throw new IllegalArgumentException("User with id " + loggedUser.getId() + " is not permitted.");
        if (requestToDelete.getUserClosingId() != null)
            throw new IllegalStateException("User with id " + loggedUser.getId() + " delete closed request.");
        repository.delete(requestToDelete);
    }
}

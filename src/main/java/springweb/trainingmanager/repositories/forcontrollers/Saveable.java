package springweb.trainingmanager.repositories.forcontrollers;

import springweb.trainingmanager.models.entities.TrainingSchedule;

public interface Saveable<E> {
    E save(E entity);
}

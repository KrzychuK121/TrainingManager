package springweb.training_manager.models.viewmodels;

/**
 * This interface should be implemented by all write models
 * that can be converted to entities that they are representing
 * E - entity to return after convert
 */
public interface Castable<E> {
    E toEntity();
}

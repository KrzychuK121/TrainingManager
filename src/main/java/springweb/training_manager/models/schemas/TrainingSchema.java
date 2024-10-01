package springweb.training_manager.models.schemas;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;

@MappedSuperclass
@Getter
public abstract class TrainingSchema implements Identificable<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    @NotBlank(message = "Tytuł treningu jest wymagany")
    @Length(min = 3, max = 100, message = "Tytuł musi mieścić się między 3 a 100 znaków")
    protected String title;
    @NotBlank(message = "Opis nie może być pusty")
    @Length(min = 3, max = 300, message = "Opis musi mieścić się między 3 a 300 znaków")
    protected String description;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Integer defaultId() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingSchema training = (TrainingSchema) o;
        return id == training.id &&
            Objects.equals(title, training.title) &&
            Objects.equals(description, training.description);
    }
}

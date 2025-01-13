package springweb.training_manager.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_request")
public class UserRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    @Length(min = 5, max = 100)
    private String title;
    @NotNull
    private String description;
    @NotNull
    private LocalDateTime requestDate;
    @Column(name = "requester_id")
    @NotNull
    private String requesterId;
    @Column(name = "user_closing_id")
    private String userClosingId;

    @ManyToOne
    @JoinColumn(
        name = "requester_id",
        referencedColumnName = "id",
        insertable = false,
        updatable = false
    )
    private User requester;
    @ManyToOne
    @JoinColumn(
        name = "user_closing_id",
        referencedColumnName = "id",
        insertable = false,
        updatable = false
    )
    private User closing;

    public UserRequest(
        String title,
        String description,
        LocalDateTime requestDate,
        User requester
    ) {
        this.title = title;
        this.description = description;
        this.requestDate = requestDate;
        this.requesterId = requester.getId();
    }

    public UserRequest(
        String title,
        String description,
        User requester
    ) {
        this(
            title,
            description,
            LocalDateTime.now(),
            requester
        );
    }

    public void setRequester(User requester) {
        this.requester = requester;
        this.requesterId = requester.getId();
    }

    public void setClosing(User closing) {
        this.closing = closing;
        this.userClosingId = closing.getId();
    }
}

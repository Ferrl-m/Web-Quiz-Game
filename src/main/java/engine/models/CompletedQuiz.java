package engine.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class CompletedQuiz {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonProperty("id")
    private Integer quizId;
    private LocalDateTime completedAt;

    public CompletedQuiz(User user, Integer quizId) {
        this.user = user;
        this.quizId = quizId;
        this.completedAt = LocalDateTime.now();
    }

    public CompletedQuiz(User user, Integer quizId, LocalDateTime completedAt) {
        this.user = user;
        this.quizId = quizId;
        this.completedAt = completedAt;
    }
}
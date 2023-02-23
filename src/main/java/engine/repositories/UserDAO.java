package engine.repositories;

import engine.models.CompletedQuiz;
import engine.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<User, Integer> {

    @Override
    User save(User user);

    Optional<User> findByUsername(String username);

    @Query("SELECT new engine.models.CompletedQuiz(cq.user, cq.quizId, cq.completedAt) FROM CompletedQuiz cq WHERE cq.user.id = :userId")
    Page<CompletedQuiz> findCompletedQuizzesByUserId(@Param("userId") Integer userId, Pageable pageable);

}

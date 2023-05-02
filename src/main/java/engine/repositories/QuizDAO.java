package engine.repositories;

import engine.models.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizDAO extends JpaRepository<Quiz, Integer> {

    @Override
    Quiz save(Quiz quiz);

    @Override
    Optional<Quiz> findById(Integer id);

    @Override
    List<Quiz> findAll();

    @Override
    void deleteById(Integer integer);

    @Override
    Page<Quiz> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM quizzes ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Quiz> findRandom();

    @Query(value = "SELECT * FROM quizzes WHERE theme = :theme", nativeQuery = true)
    Page<Quiz> findAllByTheme(@Param("theme") String theme, Pageable pageable);

    @Query(value = "SELECT * FROM quizzes WHERE user_id = :userId", nativeQuery = true)
    Page<Quiz> findAllByUser(@Param("userId") Integer userId, Pageable pageable);
}

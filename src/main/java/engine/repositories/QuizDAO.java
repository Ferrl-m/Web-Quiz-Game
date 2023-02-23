package engine.repositories;

import engine.models.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
}

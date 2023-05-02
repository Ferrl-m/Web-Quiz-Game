package engine.services;

import engine.controllers.DRO.QuizCreateDTO;
import engine.enums.Themes;
import engine.models.CompletedQuiz;
import engine.models.Quiz;
import engine.models.User;
import engine.repositories.QuizDAO;
import engine.repositories.UserDAO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
public class QuizService {

    private final ModelMapper modelMapper;
    private final QuizDAO quizDAO;
    private final UserDAO userDAO;

    @Autowired
    public QuizService(
            ModelMapper modelMapper,
            QuizDAO quizDAO,
            UserDAO userDAO)
    {
        this.modelMapper = modelMapper;
        this.quizDAO = quizDAO;
        this.userDAO = userDAO;
    }

    public Quiz getQuiz(Integer id) {
        return quizDAO.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    public Quiz getQuiz() {
        return quizDAO.findRandom().orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    public Quiz addQuiz(QuizCreateDTO quizCreateDTO) {
        Quiz quiz = modelMapper.map(quizCreateDTO, Quiz.class);
        quiz.setTheme(Themes.valueOf(quizCreateDTO.getTheme()));

        User user = getCurrentAuthUser();
        quiz.setUser(user);
        user.setQuizzesCreated(user.getQuizzesCreated() + 1);

        return quizDAO.save(quiz);
    }

    public Page<Quiz> getQuizList(int pageNo) {
        Pageable paging = PageRequest.of(pageNo, 10);

        return quizDAO.findAll(paging);
    }

    public Page<Quiz> getThemeQuizzes(String theme, int pageNo) {
        Pageable paging = PageRequest.of(pageNo, 10);

        return quizDAO.findAllByTheme(theme.toUpperCase(), paging);
    }

    public Page<Quiz> getUserQuizzes(String username, int pageNo) {
        Pageable paging = PageRequest.of(pageNo, 10);
        Integer id = userDAO.findByUsername(username).orElseThrow(() -> new ResponseStatusException(NOT_FOUND)).getId();

        return quizDAO.findAllByUser(id, paging);
    }

    public boolean checkAnswer(Integer id, String answer) {
        String expected = getQuiz(id).getAnswer();
        if (expected.equals(answer)) {
            User user = getCurrentAuthUser();
            user.getCompletedQuizzes().add(new CompletedQuiz(user, id));
            userDAO.save(user);
            return true;
        } else return false;
    }

    public ResponseEntity<Object> deleteQuiz(Integer id) {
        User user = getCurrentAuthUser();
        Quiz quiz = quizDAO.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        if (quiz.getUser().equals(user)) {
            user.getQuizzes().remove(quiz);
            quizDAO.deleteById(id);
            user.getCompletedQuizzes().removeAll(user.getCompletedQuizzes().stream().filter(completedQuiz -> completedQuiz.getQuizId() == id).collect(Collectors.toList()));
            userDAO.save(user);
            return new ResponseEntity<>(NO_CONTENT);
        } else throw new ResponseStatusException(FORBIDDEN);
    }

    private User getCurrentAuthUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userDAO.findByUsername(username).get();
    }

    public Page<CompletedQuiz> getCompletedQuizzes(Integer pageNo) {
        Pageable paging = PageRequest.of(pageNo, 10, Sort.by("completedAt").descending());

        return userDAO.findCompletedQuizzesByUserId(getCurrentAuthUser().getId(), paging);
    }

    public void setRatings(Integer id, Integer rating) {
        User user = getCurrentAuthUser();
        Quiz quiz = getQuiz(id);
        quiz.getRatings().put(user, rating);
        quiz.setRating(getAverageRating(id));

        quizDAO.save(quiz);
    }

    public double getAverageRating(Integer id) {
        double sum = 0.0;
        Map<User, Integer> map = getQuiz(id).getRatings();
        for (float rating : map.values()) {
            sum += rating;
        }

        return sum / map.size();
    }
}

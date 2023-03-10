package engine.services;

import engine.controllers.DRO.QuizCreateDTO;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
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

    public Quiz addQuiz(QuizCreateDTO quizCreateDTO) {
        Quiz quiz = modelMapper.map(quizCreateDTO, Quiz.class);

        quiz.setUser(getCurrentAuthUser());

        return quizDAO.save(quiz);
    }

    public Page<Quiz> getQuizList(Integer pageNo) {
        Pageable paging = PageRequest.of(pageNo, 10);

        return quizDAO.findAll(paging);
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
}

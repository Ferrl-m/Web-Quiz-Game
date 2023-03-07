package engine.controllers;

import engine.controllers.DRO.QuizCreateDTO;
import engine.models.CompletedQuiz;
import engine.models.Quiz;
import engine.services.QuizService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/quizzes/{id}")
    public Quiz quiz(@PathVariable int id) {
        return quizService.getQuiz(id);
    }

    @GetMapping("/quizzes")
    public Page<Quiz> quiz(@RequestParam(defaultValue = "0") Integer page) {
        return quizService.getQuizList(page);
    }

    @PostMapping("/quizzes")
    public Quiz create(@Valid @RequestBody QuizCreateDTO quizCreateDTO) {
        return quizService.addQuiz(quizCreateDTO);
    }

    @PostMapping("/quizzes/{id}/solve")
    public Map<String, Object> answer(@PathVariable int id, @RequestBody(required = false) Map<String, List<Integer>> answer) {
        if (quizService.checkAnswer(id, answer.get("answer"))) {
            return Map.of("success", true, "feedback","Congratulations, you're right!");
        } else return Map.of("success", false, "feedback", "Wrong answer! Please, try again.");
    }

    @DeleteMapping("/quizzes/{id}")
    public ResponseEntity<Object> delete(@PathVariable int id) {
        return quizService.deleteQuiz(id);
    }

    @GetMapping("/quizzes/completed")
    public Page<CompletedQuiz>  getCompletedQuizzes(@RequestParam(defaultValue = "0") Integer page) {
        return quizService.getCompletedQuizzes(page);
    }
}

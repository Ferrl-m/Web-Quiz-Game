package engine.controllers;

import engine.controllers.DRO.QuizCreateDTO;
import engine.controllers.DRO.UserCreateDTO;
import engine.models.CompletedQuiz;
import engine.models.Quiz;
import engine.services.QuizService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
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

    @GetMapping("/quizzes/{id}/solve")
    public ModelAndView solve(@PathVariable int id) {
        ModelAndView modelAndView = new ModelAndView("quiz");
        modelAndView.addObject("quiz", quizService.getQuiz(id));

        return modelAndView;
    }

    @PostMapping("/quizzes/{id}/solve")
    public Map<String, Object> solve(@PathVariable int id, @RequestParam String option) {
        if (quizService.checkAnswer(id, option)) {
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

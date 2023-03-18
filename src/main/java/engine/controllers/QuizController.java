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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public ModelAndView solve(@PathVariable int id, @RequestParam(required = false) String answer, @RequestParam(required = false) boolean isCorrect) {
        ModelAndView modelAndView = new ModelAndView("quiz");
        modelAndView.addObject("quiz", quizService.getQuiz(id));
        modelAndView.addObject("answer", answer);
        modelAndView.addObject("isCorrect", isCorrect);

        return modelAndView;
    }

    @GetMapping("/quizzes/solve")
    public ModelAndView solve(@RequestParam(required = false) String answer, @RequestParam(required = false) boolean isCorrect) {
        ModelAndView modelAndView = new ModelAndView("quiz");
        modelAndView.addObject("quiz", quizService.getQuiz());
        modelAndView.addObject("answer", answer);
        modelAndView.addObject("isCorrect", isCorrect);

        return modelAndView;
    }

    @PostMapping("/quizzes/{id}/solve")
    public ModelAndView solve(@PathVariable int id, @RequestParam String option) {
        Quiz quiz = quizService.getQuiz(id);
        String answer = quiz.getAnswer();
        boolean isCorrect = option.equals(answer);
        ModelAndView modelAndView = new ModelAndView("redirect:/quizzes/" + id + "/solve");

        modelAndView.addObject("isCorrect", isCorrect);
        modelAndView.addObject("answer", option);

        return modelAndView;
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

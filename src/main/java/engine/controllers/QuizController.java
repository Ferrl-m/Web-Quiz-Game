package engine.controllers;

import engine.controllers.DRO.QuizCreateDTO;
import engine.models.Quiz;
import engine.services.QuizService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@RestController
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    // Get quiz by id
    @GetMapping("/quiz/{id}")
    public Quiz quiz(@PathVariable int id) {
        return quizService.getQuiz(id);
    }

    // Get all quizzes
    @GetMapping("/quizzes/{page}")
    public ModelAndView quizzes(@PathVariable int page, Authentication authentication) {
        Page<Quiz> quizPage = quizService.getQuizList(page);
        List<Quiz> quizzes = quizPage.getContent();

        ModelAndView modelAndView = new ModelAndView("quizzes");
        modelAndView.addObject("currentPage", page);
        modelAndView.addObject("totalPages", quizPage.getTotalPages());
        modelAndView.addObject("quizzes", quizzes);
        modelAndView.addObject("role", authentication.getAuthorities().toString());

        return modelAndView;
    }

    // Create quiz
    @GetMapping("/quizzes/create")
    public ModelAndView create () {
        ModelAndView modelAndView = new ModelAndView("createQuiz");
        modelAndView.addObject("quizCreateDTO", new QuizCreateDTO());

        return modelAndView;
    }

    @PostMapping("/quizzes/create")
    public ModelAndView create(@Valid @ModelAttribute("quizCreateDTo") QuizCreateDTO quizCreateDTO) {
        quizCreateDTO.setAnswer(quizCreateDTO.getOptions().get(Integer.parseInt(quizCreateDTO.getAnswer())));
        quizService.addQuiz(quizCreateDTO);

        return new ModelAndView("redirect:/");
    }

    // Solve quiz (by id)
    @GetMapping("/quizzes/{id}/solve")
    public ModelAndView solve(@PathVariable int id, @RequestParam(required = false) String answer, @RequestParam(required = false) boolean isCorrect) {
        ModelAndView modelAndView = new ModelAndView("quiz");
        modelAndView.addObject("quiz", quizService.getQuiz(id));
        modelAndView.addObject("answer", answer);
        modelAndView.addObject("isCorrect", isCorrect);

        return modelAndView;
    }

    // Solve quiz (random)
    @GetMapping("/quizzes/solve")
    public ModelAndView solve(@RequestParam(required = false) String answer, @RequestParam(required = false) boolean isCorrect) {
        ModelAndView modelAndView = new ModelAndView("quiz");
        try {
            modelAndView.addObject("quiz", quizService.getQuiz());
            modelAndView.addObject("answer", answer);
            modelAndView.addObject("isCorrect", isCorrect);
        } catch (ResponseStatusException ex) {
            modelAndView.addObject("error", ex.getReason());
        }

        return modelAndView;
    }

    @PostMapping("/quizzes/{id}/solve")
    public ModelAndView solve(@PathVariable int id, @RequestParam String option) {
        boolean isCorrect = quizService.checkAnswer(id, option);
        ModelAndView modelAndView = new ModelAndView("redirect:/quizzes/" + id + "/solve");

        modelAndView.addObject("isCorrect", isCorrect);
        modelAndView.addObject("answer", option);

        return modelAndView;
    }

    // Delete quiz

    @GetMapping("/quizzes/user/{page}/{username}")
    public ModelAndView userQuizzes(@PathVariable int page, @PathVariable String username) {
        Page<Quiz> quizPage = quizService.getUserQuizzes(username, page);
        List<Quiz> quizzes = quizPage.getContent();

        ModelAndView modelAndView = new ModelAndView("my-quizzes");
        modelAndView.addObject("currentPage", page);
        modelAndView.addObject("username", username);
        modelAndView.addObject("totalPages", quizPage.getTotalPages());
        modelAndView.addObject("quizzes", quizzes);

        return modelAndView;
    }

    @DeleteMapping("/quizzes/{id}/delete")
    public String delete(@PathVariable int id) {
        quizService.deleteQuiz(id);

        return "redirect:/";
    }

    @PostMapping("/quizzes/rate/{id}")
    public void rate(@PathVariable int id, @RequestParam("rating") Integer rating) {
        quizService.setRatings(id, rating);
    }
    // Get quizzes by theme
    @GetMapping("/quizzes/{theme}/{page}")
    public ModelAndView quizzes(@PathVariable String theme, @PathVariable int page) {
        Page<Quiz> quizPage = quizService.getThemeQuizzes(theme, page);
        List<Quiz> quizzes = quizPage.getContent();

        ModelAndView modelAndView = new ModelAndView("theme");
        modelAndView.addObject("currentPage", page);
        modelAndView.addObject("totalPages", quizPage.getTotalPages());
        modelAndView.addObject("theme", theme.toUpperCase().charAt(0) + theme.substring(1));
        modelAndView.addObject("quizzes", quizzes);

        return modelAndView;
    }
}

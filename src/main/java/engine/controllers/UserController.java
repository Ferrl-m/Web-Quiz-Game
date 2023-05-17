package engine.controllers;

import engine.controllers.DRO.UserCreateDTO;
import engine.exceptions.UserException;
import engine.models.User;
import engine.repositories.UserDAO;
import engine.security.UserDetailsServiceImpl;
import engine.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.Registration;
import javax.validation.groups.Default;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
public class UserController {

    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserDAO userDAO;

    public UserController(UserService userService, UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder,
                          UserDAO userDAO) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userDAO = userDAO;
    }

    @GetMapping("/")
    public ModelAndView home() {
        return new ModelAndView("index");
    }

    @GetMapping("/register")
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("userCreateDTO", new UserCreateDTO());

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView register(@Validated({Registration.class, Default.class}) @ModelAttribute("userCreateDTO") UserCreateDTO userCreateDTO, BindingResult result) {
        if (result.hasErrors()) {
            return createModelAndView(new UserCreateDTO(), "Unacceptable username or password", "register");
        }
        User user;
        try {
            user = userService.addUser(userCreateDTO);
        } catch (UserException ex) {
            return createModelAndView(new UserCreateDTO(), ex.getMessage(), "register");
        }
        userDetailsService.setAuthentication(userDetailsService.loadUserByUsername(user.getEmail()));

        return new ModelAndView("redirect:/");
    }

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("login");
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setUsername("username");
        modelAndView.addObject("userCreateDTO", userCreateDTO);
        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView login(@Valid @ModelAttribute("userCreateDTO") UserCreateDTO userCreateDTO, BindingResult result) {
        if (result.hasErrors()) {
            return createModelAndView(new UserCreateDTO(), "Unacceptable username or password", "login");
        }
        if (userDetailsService.login(passwordEncoder, userCreateDTO)) {
            return new ModelAndView("redirect:/");
        } else return createModelAndView(new UserCreateDTO(), "Invalid username or password", "login");
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            return ResponseEntity.status(HttpStatus.OK).body("Logout successful");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Logout failed");
    }

    @GetMapping("/profile/{username}")
    public ModelAndView profile(@PathVariable String username, Authentication authentication) {
        User user = userDAO.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        ModelAndView modelAndView = new ModelAndView("profile");
        modelAndView.addObject("username", user.getUsername());
        modelAndView.addObject("email", user.getEmail());
        modelAndView.addObject("createdAt", user.getCreatedAt());
        modelAndView.addObject("completed", user.getCompletedQuizzes().size());
        modelAndView.addObject("quizzesCreated", user.getQuizzesCreated());
        modelAndView.addObject("role", authentication.getAuthorities().toString());

        return modelAndView;
    }

    @DeleteMapping("/users/delete/{username}")
    public ResponseEntity<String> delete(@PathVariable String username) {
        return userService.deleteUser(username);
    }

    @GetMapping("/users/change-credentials")
    public ModelAndView changeCredentials() {
        return new ModelAndView("credentials");
    }

    @PostMapping("/users/change-credentials")
    public ModelAndView changeCredentials(@RequestParam(name = "newUsername", required = false) String newUsername,
                                    @RequestParam(name = "newEmail", required = false) String newEmail,
                                    @RequestParam(name = "newPassword", required = false) String newPassword,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {

        return userService.update(authentication, newUsername, newEmail, newPassword, redirectAttributes);
    }

    @GetMapping("/users/{page}")
    public ModelAndView users(@PathVariable int page) {
        Page<User> userPage = userService.getUserList(page);
        List<User> users = new ArrayList<>(userPage.getContent());
        users.sort(Comparator.comparing(User::getQuizzesCreated).reversed());

        ModelAndView modelAndView = new ModelAndView("top-users");
        modelAndView.addObject("currentPage", page);

        modelAndView.addObject("totalPages", userPage.getTotalPages());
        modelAndView.addObject("users", users);

        return modelAndView;
    }

    private static ModelAndView createModelAndView(UserCreateDTO userCreateDTO, String errorMessage, String page) {
        ModelAndView modelAndView = new ModelAndView(page);
        modelAndView.addObject("userCreateDTO", userCreateDTO);
        modelAndView.addObject("error", errorMessage);

        return modelAndView;
    }
}

package engine.controllers;

import com.sun.xml.bind.v2.TODO;
import engine.controllers.DRO.UserCreateDTO;
import engine.exceptions.UserException;
import engine.models.User;
import engine.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("userCreateDTO", new UserCreateDTO());

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid @ModelAttribute("userCreateDTO") UserCreateDTO userCreateDTO, BindingResult result) {
        if (result.hasErrors()) {
            return createModelAndView(new UserCreateDTO(), "Unacceptable username or password", "register");
        }

        User user;
        try {
            user = userService.addUser(userCreateDTO);
        } catch (UserException ex) {
            return createModelAndView(new UserCreateDTO(), ex.getMessage(), "register");
        }

        // TODO remove authentication logic from controller
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // set authentication in SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new ModelAndView("index");
    }

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("userCreateDTO", new UserCreateDTO());
        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView login(@Valid @ModelAttribute("userCreateDTO") UserCreateDTO userCreateDTO, BindingResult result) {
        if (result.hasErrors()) {
            return createModelAndView(new UserCreateDTO(), "Unacceptable username or password", "login");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(userCreateDTO.getUsername());
        if (passwordEncoder.matches(userCreateDTO.getPassword(), userDetails.getPassword())) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            // set authentication in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return new ModelAndView("index");
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

    private static ModelAndView createModelAndView(UserCreateDTO userCreateDTO, String errorMessage, String page) {
        ModelAndView modelAndView = new ModelAndView(page);
        modelAndView.addObject("userCreateDTO", userCreateDTO);
        modelAndView.addObject("error", errorMessage);

        return modelAndView;
    }
}

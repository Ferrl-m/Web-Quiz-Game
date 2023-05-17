package engine.services;

import engine.controllers.DRO.UserCreateDTO;
import engine.exceptions.UserException;
import engine.models.Quiz;
import engine.models.User;
import engine.repositories.UserDAO;
import engine.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
public class UserService {

    private final UserDAO userDAO;
    private final PasswordEncoder encoder;
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public UserService(UserDAO userDAO, PasswordEncoder encoder, UserDetailsServiceImpl userDetailsService) {
        this.userDAO = userDAO;
        this.encoder = encoder;
        this.userDetailsService = userDetailsService;
    }

    public User addUser(UserCreateDTO userCreateDTO) throws UserException {
        if (userDAO.findByUsername(userCreateDTO.getUsername()).isPresent()) {
            throw new UserException("User with this username already exists");
        }
        if (userDAO.findByEmail(userCreateDTO.getEmail()).isPresent()) {
            throw new UserException("User with this email already exists");
        }
        User user = new User();
        user.setUsername(userCreateDTO.getUsername());
        user.setEmail(userCreateDTO.getEmail());
        user.setPassword(encoder.encode(userCreateDTO.getPassword()));
        user.setCreatedAt(LocalDate.now());
        user.setRole("USER");

        return userDAO.save(user);
    }

    public ModelAndView update(Authentication authentication, String newUsername, String newEmail, String newPassword, RedirectAttributes redirectAttributes) {
        String currentUsername = authentication.getName();
        User currentUser = userDAO.findByUsername(currentUsername).get();

        if (newUsername != null && !newUsername.isEmpty() && !newUsername.equals(currentUsername)) {
            if (userDAO.findByUsername(newUsername).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Username already exists.");
                return new ModelAndView("redirect:/users/change-credentials");
            }
            if (newUsername.length() < 5) {
                redirectAttributes.addFlashAttribute("error", "Username must be longer than 5 symbols.");
                return new ModelAndView("redirect:/users/change-credentials");
            }
            currentUser.setUsername(newUsername);
        }

        if (newEmail != null && !newEmail.isEmpty() && !newEmail.equals(currentUser.getEmail())) {
            if (userDAO.findByEmail(newEmail).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Email already exists.");
                return new ModelAndView("redirect:/users/change-credentials");
            }
            if (!newEmail.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                redirectAttributes.addFlashAttribute("error", "Email is not valid.");
                return new ModelAndView("redirect:/users/change-credentials");
            }
            currentUser.setEmail(newEmail);
        }

        if (newPassword != null && !newPassword.isEmpty()) {
            if (newPassword.length() < 5) {
                redirectAttributes.addFlashAttribute("error", "Password must be longer than 5 symbols.");
                return new ModelAndView("redirect:/users/change-credentials");
            }
            currentUser.setPassword(encoder.encode(newPassword));
        }

        userDAO.save(currentUser);
        userDetailsService.setAuthentication(userDetailsService.loadUserByUsername(currentUser.getEmail()));
        redirectAttributes.addFlashAttribute("success", "Credentials updated successfully.");

        return new ModelAndView("redirect:/");
    }

    public Page<User> getUserList(int pageNo) {
        Pageable paging = PageRequest.of(pageNo, 10);

        return userDAO.findAll(paging);
    }

    public ResponseEntity<String> deleteUser(String username) {
        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        if (username.equals(current.getName()) || current.getAuthorities().toString().equals("[ADMIN]")) {
            userDAO.delete(userDAO.findByUsername(username).get());
        } else throw new ResponseStatusException(FORBIDDEN);

        return ResponseEntity.noContent().build();
    }
}

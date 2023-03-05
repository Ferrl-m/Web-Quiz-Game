package engine.services;

import engine.controllers.DRO.UserCreateDTO;
import engine.models.User;
import engine.repositories.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserDAO userDAO;
    private final PasswordEncoder encoder;


    @Autowired
    public UserService(UserDAO userDAO, PasswordEncoder encoder) {
        this.userDAO = userDAO;
        this.encoder = encoder;
    }

    public User addUser(UserCreateDTO userCreateDTO) {
        if (userDAO.findByUsername(userCreateDTO.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setUsername(userCreateDTO.getUsername());
        user.setPassword(encoder.encode(userCreateDTO.getPassword()));
        user.setRole("USER");

        return userDAO.save(user);
    }
}

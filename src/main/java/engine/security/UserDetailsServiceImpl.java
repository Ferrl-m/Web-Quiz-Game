package engine.security;

import engine.controllers.DRO.UserCreateDTO;
import engine.models.User;
import engine.repositories.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDAO userDAO;

    @Autowired
    public UserDetailsServiceImpl (UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new UserDetailsImpl(user);
    }

    public void setAuthentication(UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // set authentication in SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public boolean login (PasswordEncoder passwordEncoder, UserCreateDTO userCreateDTO) {
        UserDetails userDetails = loadUserByUsername(userCreateDTO.getEmail());
        if (passwordEncoder.matches(userCreateDTO.getPassword(), userDetails.getPassword())) {
            setAuthentication(userDetails);

            return true;
        } else return false;
    }
}

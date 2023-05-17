package engine.security;

import engine.models.User;
import engine.repositories.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@EnableWebSecurity
@Configuration
public class WebSecurityConfigurerImpl extends WebSecurityConfigurerAdapter {

    UserDetailsService userDetailsService;
    UserDAO userDAO;

    @Autowired
    public WebSecurityConfigurerImpl(UserDetailsService userDetailsService, UserDAO userDAO) {
        this.userDetailsService = userDetailsService;
        this.userDAO = userDAO;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(getEncoder());

        String adminCredentialsFilePath = "src/main/resources/admin-credentials.txt";
        List<String> adminCredentials = Files.readAllLines(Paths.get(adminCredentialsFilePath));

        if (adminCredentials.size() % 3 == 0) {
            for (int i = 0; i < adminCredentials.size(); i += 3) {
                String adminEmail = adminCredentials.get(i);
                String adminUsername = adminCredentials.get(i + 1);
                String adminPassword = adminCredentials.get(i + 2);

                if (userDAO.findByEmail(adminEmail).isPresent()) {
                    continue;
                }

                PasswordEncoder passwordEncoder = getEncoder();
                String encodedPassword = passwordEncoder.encode(adminPassword);

                User adminUser = new User();
                adminUser.setEmail(adminEmail);
                adminUser.setUsername(adminUsername);
                adminUser.setPassword(encodedPassword);
                adminUser.setCreatedAt(LocalDate.now());
                adminUser.setRole("ADMIN");

                userDAO.save(adminUser);
            }
        } else {
            throw new RuntimeException("Invalid admin credentials file format");
        }
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.logout()
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/")
                .and()
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers("/register", "/login", "/actuator", "/css/**", "/favicon.ico", "/").permitAll()
                .anyRequest().authenticated();
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}

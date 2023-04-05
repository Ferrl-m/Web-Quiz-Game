package engine.controllers.DRO;

import lombok.Data;

import javax.servlet.Registration;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserCreateDTO {

    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    @NotBlank (groups = Registration.class)
    private String email;
    @NotBlank(groups = Registration.class)
    private String username;
    @Size(min = 5)
    @NotBlank
    private String password;
}

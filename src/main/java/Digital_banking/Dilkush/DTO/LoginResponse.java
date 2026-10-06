package Digital_banking.Dilkush.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String name;
    private String email;
    private String role;
}
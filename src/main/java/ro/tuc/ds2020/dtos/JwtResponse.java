package ro.tuc.ds2020.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UUID id;
    private String password;
    private String email;
    private String role;

    public JwtResponse(String accessToken,UUID id,String password,   String email, String role) {
        this.token = accessToken;
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }

}
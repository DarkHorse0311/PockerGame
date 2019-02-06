package be.kdg.userservice.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenDto {
    private String access_token;
    private int expires_in;
}

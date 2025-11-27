package ma.smartshop.smartshop.dto.client;

import lombok.Data;

@Data
public class ClientUserCreateRequestDto {

    private String username;
    private String password;
}
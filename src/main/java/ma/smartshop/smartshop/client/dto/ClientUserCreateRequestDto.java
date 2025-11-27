package ma.smartshop.smartshop.client.dto;

import lombok.Data;

@Data
public class ClientUserCreateRequestDto {

    private String username;
    private String password;
}
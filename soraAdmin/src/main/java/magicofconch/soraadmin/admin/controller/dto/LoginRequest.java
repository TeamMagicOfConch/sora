package magicofconch.soraadmin.admin.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequest {
    private String adminName;
    private String password;
}

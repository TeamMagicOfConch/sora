package magicofconch.sora.security.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthRes {
	private String accessToken;
	private String refreshToken;
	private String username;
}

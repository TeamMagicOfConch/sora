package magicofconch.sora.security.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenDto {
	private String accessToken;
	private String refreshToken;
}

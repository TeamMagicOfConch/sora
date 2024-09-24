package magicofconch.sora.user.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRes {
	private String accessToken;
	private String refreshToken;
}

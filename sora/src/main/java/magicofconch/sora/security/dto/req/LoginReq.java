package magicofconch.sora.security.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginReq {
	@Schema(description = "로그인을 위한 OS ID", example = "XXXX-XXXX-XXXX")
	private String osId;
}

package magicofconch.sora.security.dto.req;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterReq {
	private String osId;
	private String osType;
	private String username;
	private int initialReviewCount;
}

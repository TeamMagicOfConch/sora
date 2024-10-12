package magicofconch.sora.security.dto.req;

import lombok.Data;
import lombok.NoArgsConstructor;
import magicofconch.sora.user.enums.OsType;

@Data
@NoArgsConstructor
public class RegisterReq {
	private String osId;
	private String osType;
	private String username;
	private int initialReviewCount;
}

package magicofconch.sora.user.dto.req;

import lombok.Data;
import lombok.NoArgsConstructor;
import magicofconch.sora.user.enums.OsType;

@Data
@NoArgsConstructor
public class RegisterReq {
	private String identifier;
	private String osType;
}

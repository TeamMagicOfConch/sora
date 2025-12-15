package magicofconch.sora.user.api.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import magicOfConch.enums.OsType;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FcmRegisterReq {

	private String token;
	private OsType osType;
	private String osId;
}

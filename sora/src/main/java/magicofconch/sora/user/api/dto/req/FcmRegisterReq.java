package magicofconch.sora.user.api.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import magicOfConch.enums.OsType;

@Schema(description = "FCM 토큰 등록 요청")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FcmRegisterReq {

	@Schema(
		description = "Firebase Cloud Messaging 토큰",
		example = "dGhpcyBpcyBhIGZha2UgZmNtIHRva2VuLi4u"
	)
	private String token;

	@Schema(
		description = "운영체제 타입",
		example = "IOS",
		allowableValues = {"IOS", "ANDROID"}
	)
	private OsType osType;

	@Schema(
		description = "기기 고유 식별자 (OS별 고유 ID)",
		example = "A1B2C3D4-E5F6-G7H8-I9J0-K1L2M3N4O5P6"
	)
	private String osId;
}

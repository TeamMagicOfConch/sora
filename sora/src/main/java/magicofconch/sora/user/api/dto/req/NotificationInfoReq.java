package magicofconch.sora.user.api.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "알림 설정 업데이트 요청")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationInfoReq {

	@Schema(
		description = "Streak 알림 수신 동의 여부",
		example = "true"
	)
	private Boolean streak;

}

package magicofconch.sora.user.api.dto.req;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StreakReq {

	@NotBlank
	private String reviewTime;

	@NotNull
	private LocalDateTime reviewAt;

	@NotBlank
	private String writeLocation;

	@NotBlank
	private String aspiration;
}

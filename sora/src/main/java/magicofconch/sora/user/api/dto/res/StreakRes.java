package magicofconch.sora.user.api.dto.res;

import java.time.LocalTime;

public record StreakRes(String reviewTime,
						LocalTime reviewAt,
						String writeLocation,
						String aspiration) {
}

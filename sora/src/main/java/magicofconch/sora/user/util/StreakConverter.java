package magicofconch.sora.user.util;

import magicOfConch.user.StreakInfo;
import magicofconch.sora.user.api.dto.req.StreakReq;
import magicofconch.sora.user.api.dto.res.StreakRes;

public class StreakConverter {

	public static void overwrite(StreakInfo target, StreakReq src) {
		target.setReviewAt(src.getReviewAt());
		target.setReviewTime(src.getReviewTime());
		target.setWriteLocation(src.getWriteLocation());
		target.setAspiration(src.getAspiration());
	}

	public static StreakInfo from(StreakReq src) {
		return StreakInfo.builder()
			.aspiration(src.getAspiration())
			.reviewTime(src.getReviewTime())
			.writeLocation(src.getWriteLocation())
			.reviewAt(src.getReviewAt())
			.build();
	}

	public static StreakRes streakResFrom(StreakInfo src) {
		return new StreakRes(src.getReviewTime(), src.getReviewAt(), src.getWriteLocation(), src.getAspiration());

	}
}

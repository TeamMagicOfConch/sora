package magicofconch.sora.review.fixture;

import java.time.LocalDate;
import java.time.LocalTime;

import magicOfConch.user.StreakInfo;

public class StreakInfoFixture {

	public static StreakInfo create() {
		return StreakInfo.builder()
			.reviewAt(LocalTime.of(21, 0))
			.writeLocation("집")
			.aspiration("성장하는 개발자")
			.lastWriteDate(LocalDate.now().minusDays(1))
			.reviewStreakCount(5)
			.isReceiveNotification(true)
			.build();
	}

	public static StreakInfo createWithLastWriteDate(LocalDate lastWriteDate) {
		return StreakInfo.builder()
			.reviewAt(LocalTime.of(21, 0))
			.writeLocation("집")
			.aspiration("성장하는 개발자")
			.lastWriteDate(lastWriteDate)
			.reviewStreakCount(1)
			.isReceiveNotification(true)
			.build();
	}

	public static StreakInfo createWithStreakCount(LocalDate lastWriteDate, int streakCount) {
		return StreakInfo.builder()
			.reviewAt(LocalTime.of(21, 0))
			.writeLocation("집")
			.aspiration("성장하는 개발자")
			.lastWriteDate(lastWriteDate)
			.reviewStreakCount(streakCount)
			.isReceiveNotification(true)
			.build();
	}

	public static StreakInfo createEmpty() {
		return StreakInfo.builder()
			.reviewAt(LocalTime.of(21, 0))
			.writeLocation("집")
			.aspiration("성장하는 개발자")
			.lastWriteDate(null)
			.reviewStreakCount(0)
			.isReceiveNotification(false)
			.build();
	}
}

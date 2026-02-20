package magicofconch.sora.review.fixture;

import magicOfConch.enums.UserRole;
import magicOfConch.user.OsAuthInfo;
import magicOfConch.user.StreakInfo;
import magicOfConch.user.UserInfo;

public class UserInfoFixture {

	public static UserInfo create() {
		StreakInfo streakInfo = StreakInfoFixture.create();
		return UserInfo.builder()
			.uuid("test-uuid-1234")
			.socialId("test-social-id")
			.username("테스트유저")
			.role(UserRole.ROLE_USER)
			.initialReviewCount(0)
			.streakInfo(streakInfo)
			.build();
	}

	public static UserInfo createWithStreakInfo(StreakInfo streakInfo) {
		return UserInfo.builder()
			.uuid("test-uuid-1234")
			.socialId("test-social-id")
			.username("테스트유저")
			.role(UserRole.ROLE_USER)
			.initialReviewCount(0)
			.streakInfo(streakInfo)
			.build();
	}
}

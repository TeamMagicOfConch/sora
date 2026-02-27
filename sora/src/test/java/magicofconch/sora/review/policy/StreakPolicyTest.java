package magicofconch.sora.review.policy;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import magicOfConch.user.StreakInfo;
import magicofconch.sora.review.fixture.StreakInfoFixture;

@DisplayName("Streak 정책 테스트")
class StreakPolicyTest {

	@Nested
	@DisplayName("연속 작성 확인 로직")
	class UpdateStreakTest {

		@Test
		@DisplayName("어제 작성했다면 연속 카운트가 1 증가한다")
		void whenLastWriteDateIsYesterday_thenStreakCountIncreases() {
			// given
			LocalDate yesterday = LocalDate.now().minusDays(1);
			StreakInfo streakInfo = StreakInfoFixture.createWithStreakCount(yesterday, 5);

			// when
			updateStreak(streakInfo);

			// then
			assertThat(streakInfo.getReviewStreakCount()).isEqualTo(6);
			assertThat(streakInfo.getLastWriteDate()).isEqualTo(LocalDate.now());
		}

		@Test
		@DisplayName("연속이 끊긴 경우 카운트가 1로 초기화된다")
		void whenStreakBroken_thenCountResetsToOne() {
			// given
			LocalDate threeDaysAgo = LocalDate.now().minusDays(3);
			StreakInfo streakInfo = StreakInfoFixture.createWithStreakCount(threeDaysAgo, 10);

			// when
			updateStreak(streakInfo);

			// then
			assertThat(streakInfo.getReviewStreakCount()).isEqualTo(1);
			assertThat(streakInfo.getLastWriteDate()).isEqualTo(LocalDate.now());
		}

		@Test
		@DisplayName("처음 작성하는 경우(lastWriteDate가 null) 카운트가 1로 설정된다")
		void whenFirstTimeWriting_thenStreakCountIsOne() {
			// given
			StreakInfo streakInfo = StreakInfoFixture.createEmpty();

			// when
			updateStreak(streakInfo);

			// then
			assertThat(streakInfo.getReviewStreakCount()).isEqualTo(1);
			assertThat(streakInfo.getLastWriteDate()).isEqualTo(LocalDate.now());
		}

		// 실제 ReviewService의 로직을 추출하여 테스트
		private void updateStreak(StreakInfo streakInfo) {
			LocalDate today = LocalDate.now();
			LocalDate yesterday = today.minusDays(1);

			if (streakInfo.getLastWriteDate() != null && streakInfo.getLastWriteDate().isEqual(yesterday)) {
				streakInfo.setLastWriteDate(today);
				streakInfo.addStreakCount();
			} else {
				streakInfo.setLastWriteDate(today);
				streakInfo.clearStreakCount();
			}
		}
	}

	@Nested
	@DisplayName("StreakInfo 메서드 테스트")
	class StreakInfoMethodTest {

		@Test
		@DisplayName("clearStreakCount는 카운트를 1로 설정한다")
		void clearStreakCount_setsCountToOne() {
			// given
			StreakInfo streakInfo = StreakInfoFixture.createWithStreakCount(LocalDate.now(), 10);

			// when
			streakInfo.clearStreakCount();

			// then
			assertThat(streakInfo.getReviewStreakCount()).isEqualTo(1);
		}

		@Test
		@DisplayName("addStreakCount는 카운트를 1 증가시킨다")
		void addStreakCount_incrementsByOne() {
			// given
			StreakInfo streakInfo = StreakInfoFixture.createWithStreakCount(LocalDate.now(), 5);

			// when
			streakInfo.addStreakCount();

			// then
			assertThat(streakInfo.getReviewStreakCount()).isEqualTo(6);
		}
	}
}

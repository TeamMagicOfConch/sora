package magicofconch.sora.review.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import magicOfConch.enums.FeedbackType;
import magicOfConch.user.StreakInfo;
import magicOfConch.user.UserInfo;
import magicofconch.sora.review.api.dto.req.SaveReq;
import magicofconch.sora.review.fixture.StreakInfoFixture;
import magicofconch.sora.review.fixture.UserInfoFixture;
import magicofconch.sora.review.repository.ReviewRepository;
import magicofconch.sora.util.Encryption.EncryptionUtil;
import magicofconch.sora.util.SecurityUtil;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService 테스트")
class ReviewServiceTest {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private SecurityUtil securityUtil;

	@Mock
	private EncryptionUtil encryptionUtil;

	@InjectMocks
	private ReviewService reviewService;

	@Nested
	@DisplayName("일기 저장 시 Streak 업데이트")
	class SaveReviewStreakTest {

		@Test
		@DisplayName("어제 작성한 경우 연속 카운트가 증가한다")
		void whenWrittenYesterday_thenStreakIncreases() throws Exception {
			// given
			LocalDate yesterday = LocalDate.now().minusDays(1);
			StreakInfo streakInfo = StreakInfoFixture.createWithStreakCount(yesterday, 5);
			UserInfo userInfo = UserInfoFixture.createWithStreakInfo(streakInfo);

			SaveReq saveReq = new SaveReq(
				"오늘의 일기",
				FeedbackType.THINKING,
				LocalDate.now(),
				"피드백"
			);

			given(securityUtil.getCurrentUsersEntity()).willReturn(userInfo);
			given(encryptionUtil.encrypt(anyString())).willReturn("encrypted");

			// when
			reviewService.saveReview(saveReq);

			// then
			assertThat(streakInfo.getReviewStreakCount()).isEqualTo(6);
			assertThat(streakInfo.getLastWriteDate()).isEqualTo(LocalDate.now());
			verify(reviewRepository, times(1)).save(any());
		}

		@Test
		@DisplayName("연속이 끊긴 경우 카운트가 1로 초기화된다")
		void whenStreakBroken_thenCountResetsToOne() throws Exception {
			// given
			LocalDate threeDaysAgo = LocalDate.now().minusDays(3);
			StreakInfo streakInfo = StreakInfoFixture.createWithStreakCount(threeDaysAgo, 10);
			UserInfo userInfo = UserInfoFixture.createWithStreakInfo(streakInfo);

			SaveReq saveReq = new SaveReq(
				"오늘의 일기",
				FeedbackType.THINKING,
				LocalDate.now(),
				"피드백"
			);

			given(securityUtil.getCurrentUsersEntity()).willReturn(userInfo);
			given(encryptionUtil.encrypt(anyString())).willReturn("encrypted");

			// when
			reviewService.saveReview(saveReq);

			// then
			assertThat(streakInfo.getReviewStreakCount()).isEqualTo(1);
			assertThat(streakInfo.getLastWriteDate()).isEqualTo(LocalDate.now());
			verify(reviewRepository, times(1)).save(any());
		}

		@Test
		@DisplayName("처음 작성하는 경우 카운트가 1이 된다")
		void whenFirstWrite_thenCountIsOne() throws Exception {
			// given
			StreakInfo streakInfo = StreakInfoFixture.createEmpty();
			UserInfo userInfo = UserInfoFixture.createWithStreakInfo(streakInfo);

			SaveReq saveReq = new SaveReq(
				"첫 일기",
				FeedbackType.FEELING,
				LocalDate.now(),
				"첫 피드백"
			);

			given(securityUtil.getCurrentUsersEntity()).willReturn(userInfo);
			given(encryptionUtil.encrypt(anyString())).willReturn("encrypted");

			// when
			reviewService.saveReview(saveReq);

			// then
			assertThat(streakInfo.getReviewStreakCount()).isEqualTo(1);
			assertThat(streakInfo.getLastWriteDate()).isEqualTo(LocalDate.now());
			verify(reviewRepository, times(1)).save(any());
		}
	}
}

package magicofconch.soraadmin.streak.batch.processorTest;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import magicOfConch.enums.UserRole;
import magicOfConch.user.OsAuthInfo;
import magicOfConch.user.StreakInfo;
import magicOfConch.user.UserInfo;
import magicofconch.soraadmin.batch.streak.dto.FcmRequest;
import magicofconch.soraadmin.batch.streak.dto.UserStreakItem;
import magicofconch.soraadmin.batch.streak.processor.StreakProcessor;
import magicofconch.soraadmin.util.FcmUtil;

@ExtendWith(MockitoExtension.class)
class StreakProcessorTest {

	private final LocalDateTime alertTime = LocalDateTime.of(2024, 3, 10, 14, 30);
	@Mock
	private FcmUtil fcmUtil;
	@InjectMocks
	private StreakProcessor processor;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(processor, "alertTime", alertTime);
	}

	private OsAuthInfo osAuthInfo() {
		OsAuthInfo osAuthInfo = OsAuthInfo.builder()
			.osId("test-os-id")
			.osType("IOS")
			.build();
		osAuthInfo.updateToken("test-fcm-token");
		return osAuthInfo;
	}

	private StreakInfo streakInfo(LocalTime reviewAt, Boolean receiveNotification, int streakCount) {
		return StreakInfo.builder()
			.reviewAt(reviewAt)
			.isReceiveNotification(receiveNotification)
			.aspiration("개발자")
			.writeLocation("카페")
			.reviewStreakCount(streakCount)
			.build();
	}

	private UserInfo userInfo(StreakInfo streakInfo) {
		return UserInfo.builder()
			.uuid("test-uuid")
			.socialId("social-id")
			.osAuthInfo(osAuthInfo())
			.role(UserRole.ROLE_USER)
			.username("테스터")
			.initialReviewCount(0)
			.streakInfo(streakInfo)
			.build();
	}

	// ── null 반환 케이스 ────────────────────────────────────────────────

	@Test
	void streakInfo가_null이면_null을_반환한다() throws Exception {
		UserInfo userInfo = userInfo(streakInfo(LocalTime.of(14, 30), true, 1));
		userInfo.updateStreakInfo(null);
		UserStreakItem item = new UserStreakItem(userInfo, null, 0);

		assertThat(processor.process(item)).isNull();
	}

	@Test
	void 수신동의_안한_유저는_null을_반환한다() throws Exception {
		UserInfo userInfo = userInfo(streakInfo(LocalTime.of(14, 30), false, 1));
		UserStreakItem item = new UserStreakItem(userInfo, null, 0);

		assertThat(processor.process(item)).isNull();
	}

	@Test
	void 알람시간이_다른_유저는_null을_반환한다() throws Exception {
		UserInfo userInfo = userInfo(streakInfo(LocalTime.of(10, 0), true, 1));
		UserStreakItem item = new UserStreakItem(userInfo, null, 0);

		assertThat(processor.process(item)).isNull();
	}

	@Test
	void OsAuthInfo가_없으면_null을_반환한다() throws Exception {
		UserInfo userInfo = userInfo(streakInfo(LocalTime.of(14, 30), true, 1));
		ReflectionTestUtils.setField(userInfo, "osAuthInfo", new ArrayList<>());
		UserStreakItem item = new UserStreakItem(userInfo, null, 0);

		assertThat(processor.process(item)).isNull();
	}

	// ── 메시지 분기 케이스 ──────────────────────────────────────────────

	@Test
	void 회고를_한번도_안쓴_유저는_첫_회고_유도_메시지를_반환한다() throws Exception {
		UserInfo userInfo = userInfo(streakInfo(LocalTime.of(14, 30), true, 0));
		UserStreakItem item = new UserStreakItem(userInfo, null, 0);

		FcmRequest result = processor.process(item);

		assertThat(result).isNotNull();
		assertThat(result.body()).isEqualTo("오늘부터 회고 습관을 만들어볼까요?");
	}

	@Test
	void 회고는_있지만_스트릭이_0이면_첫_회고_날짜_메시지를_반환한다() throws Exception {
		UserInfo userInfo = userInfo(streakInfo(LocalTime.of(14, 30), true, 0));
		LocalDate firstReviewDate = LocalDate.of(2024, 3, 5);
		UserStreakItem item = new UserStreakItem(userInfo, firstReviewDate, 3);

		FcmRequest result = processor.process(item);

		assertThat(result).isNotNull();
		assertThat(result.body()).isEqualTo("3월 5일 첫 회고를 작성했어요. 오늘도 한 줄 적어볼까요?");
	}

	@Test
	void 스트릭이_1에서_3이면_열망_메시지를_반환한다() throws Exception {
		UserInfo userInfo = userInfo(streakInfo(LocalTime.of(14, 30), true, 2));
		UserStreakItem item = new UserStreakItem(userInfo, LocalDate.of(2024, 3, 1), 5);

		FcmRequest result = processor.process(item);

		assertThat(result).isNotNull();
		assertThat(result.body()).isEqualTo("이제 곧 회고 시간이에요. 개발자에서 카페가 되어볼까요?");
	}

	@Test
	void 스트릭이_4이상이면_연속일수_메시지를_반환한다() throws Exception {
		UserInfo userInfo = userInfo(streakInfo(LocalTime.of(14, 30), true, 5));
		UserStreakItem item = new UserStreakItem(userInfo, LocalDate.of(2024, 3, 1), 10);

		FcmRequest result = processor.process(item);

		assertThat(result).isNotNull();
		assertThat(result.body()).isEqualTo("오늘 한문장 쓰면 연속 5일차, 개발자에 한걸음 더 가까워져요");
	}
}

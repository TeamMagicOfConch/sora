package magicofconch.soraadmin.batch.streak.processor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicOfConch.enums.OsType;
import magicOfConch.user.StreakInfo;
import magicOfConch.user.UserInfo;
import magicOfConch.dto.FcmRequest;
import magicOfConch.dto.UserStreakItem;
import magicofconch.soraadmin.util.FcmUtil;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class StreakProcessor implements ItemProcessor<UserStreakItem, FcmRequest> {

	private final FcmUtil fcmUtil;
	private final String zeroReview = "오늘부터 회고 습관을 만들어볼까요?";
	private final String streakZeroReviewNonZero = "%d월 %d일 첫 회고를 작성했어요. 오늘도 한 줄 적어볼까요?";
	private final String streakOneToThree = "이제 곧 회고 시간이에요. %s에서 %s가 되어볼까요?";
	private final String streakMoreThree = "오늘 한문장 쓰면 연속 %d일차, %s에 한걸음 더 가까워져요";
	@Value("#{jobExecutionContext['alertTime']}")
	private LocalDateTime alertTime;

	@Override
	public FcmRequest process(UserStreakItem item) throws Exception {
		UserInfo userInfo = item.userInfo();
		LocalDate firstReviewDate = item.firstReviewDate();
		long reviewCount = item.reviewCount();

		// log.info("Processing user info {}", userInfo.getId());

		StreakInfo streakInfo = userInfo.getStreakInfo();
		// 아직 StreakInfo가 없는 기존 버전 user 대비
		if (streakInfo == null)
			return null;

		// 수신 동의 안한 유저 제외
		if (!streakInfo.getIsReceiveNotification())
			return null;

		int hour = streakInfo.getReviewAt().getHour();
		int minute = streakInfo.getReviewAt().getMinute();

		/**
		 * 현재 시간에 알람 설정한 유저만 필터
		 * Todo: DB 조건부 조회로 변경
		 */
		if (hour != alertTime.getHour() || minute != alertTime.getMinute()) {
			return null;
		}

		// log.info("user reviewAt = hh:mm = {}:{} || current={}:{}", hour, minute, alertTime.getHour(),
		// 	alertTime.getMinute());

		if (userInfo.getOsAuthInfo().isEmpty()) {
			return null;
		}

		long count = reviewCount;

		String aspiration = streakInfo.getAspiration();
		String writeLocation = streakInfo.getWriteLocation();
		Integer streakCount = streakInfo.getReviewStreakCount();

		String body;

		if (count <= 0) {   // 회고를 1번도 안쓴 경우
			body = zeroReview;
		} else {            // 회고를 1번이상 쓴 경우
			if (userInfo.getStreakInfo().getReviewStreakCount() == 0) {
				body = String.format(streakZeroReviewNonZero, firstReviewDate.getMonthValue(),
					firstReviewDate.getDayOfMonth());
			} else if (streakInfo.getReviewStreakCount() <= 3) {
				body = String.format(streakOneToThree, aspiration, writeLocation);
			} else {
				body = String.format(streakMoreThree, streakCount, aspiration);
			}
		}

		// Todo: 현재는 유저당 1개의 OS_Auth 정보 존재, 추후 OAuth2 통합시 User의 모든 OsID 순회 처리 필요
		FcmRequest fcmRequest = new FcmRequest(
			OsType.findOsType(userInfo.getOsAuthInfo().get(0).getOsType()),
			"회고쓸 시간이에요!",
			body,
			userInfo.getOsAuthInfo().get(0).getFcmToken()
		);

		return fcmRequest;
	}

}

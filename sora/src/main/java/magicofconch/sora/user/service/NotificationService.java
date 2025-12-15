package magicofconch.sora.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import magicOfConch.user.UserInfo;
import magicofconch.sora.user.api.dto.req.NotificationInfoReq;
import magicofconch.sora.util.SecurityUtil;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private SecurityUtil securityUtil;

	/**
	 * notification 상태 관리 메서드
	 * @param req
	 */
	@Transactional
	public void updateNotification(NotificationInfoReq req) {

		UserInfo userInfo = securityUtil.getCurrentUsersEntity();

		if (req.getStreak()) {
			userInfo.getStreakInfo().receiveNotification();
		}
	}

}

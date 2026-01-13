package magicofconch.sora.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicOfConch.user.OsAuthInfo;
import magicOfConch.user.UserInfo;
import magicofconch.sora.user.api.dto.req.FcmRegisterReq;
import magicofconch.sora.user.api.dto.req.NotificationInfoReq;
import magicofconch.sora.user.repository.OsAuthInfoRepository;
import magicofconch.sora.util.ResponseCode;
import magicofconch.sora.util.SecurityUtil;
import magicofconch.sora.util.exception.BusinessException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

	private final OsAuthInfoRepository osAuthInfoRepository;
	private final SecurityUtil securityUtil;

	/**
	 * notification fcm 토큰 추가
	 * @param req : 토큰 등록 요청 정보
	 */
	@Transactional
	public void registerFcm(FcmRegisterReq req) {

		UserInfo userInfo = securityUtil.getCurrentUsersEntity();

		OsAuthInfo osAuthInfo = osAuthInfoRepository.findByUserIdAndOsId(userInfo.getId(), req.getOsId())
			.orElseThrow(() -> new BusinessException(ResponseCode.OS_ID_NOT_FOUND));

		osAuthInfo.updateToken(req.getToken());
		osAuthInfoRepository.save(osAuthInfo);
	}

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

package magicofconch.sora.security.service;

import java.util.UUID;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import magicOfConch.enums.UserRole;
import magicOfConch.user.OsAuthInfo;
import magicOfConch.user.StreakInfo;
import magicOfConch.user.UserInfo;
import magicofconch.sora.security.dto.req.LoginReq;
import magicofconch.sora.security.dto.req.RegisterReq;
import magicofconch.sora.security.dto.res.AuthRes;
import magicofconch.sora.security.dto.res.TokenDto;
import magicofconch.sora.security.jwt.JwtUtil;
import magicofconch.sora.security.os_id.OsIdAuthenticationToken;
import magicofconch.sora.user.repository.OsAuthInfoRepository;
import magicofconch.sora.user.repository.UserInfoRepository;
import magicofconch.sora.util.ResponseCode;
import magicofconch.sora.util.SecurityUtil;
import magicofconch.sora.util.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final UserInfoRepository userInfoRepository;
	private final OsAuthInfoRepository osAuthInfoRepository;
	private final JwtUtil jwtUtil;
	private final SecurityUtil securityUtil;

	@Transactional
	public AuthRes registerUser(RegisterReq registerReq) {

		if (osAuthInfoRepository.existsByOsId(registerReq.getOsId())) {
			throw new BusinessException(ResponseCode.USER_ALREADY_REGISTERED);
		}

		OsAuthInfo osAuthInfo = OsAuthInfo.builder()
			.osId(registerReq.getOsId())
			.osType(registerReq.getOsType())
			.build();

		UserInfo userInfo = UserInfo.builder()
			.osAuthInfo(osAuthInfo)
			.uuid(UUID.randomUUID().toString())
			.username(registerReq.getUsername())
			.initialReviewCount(registerReq.getInitialReviewCount())
			.role(UserRole.ROLE_SEMI_USER)
			.build();

		String accessToken = jwtUtil.generateAccessToken(userInfo.getUuid(), UserRole.ROLE_SEMI_USER,
			userInfo.getUsername());
		String refreshToken = jwtUtil.generateRefreshToken(userInfo.getUuid(), UserRole.ROLE_SEMI_USER,
			userInfo.getUsername());

		// UserInfo userInfo = UserInfo.builder()
		// 	.osAuthInfo(osAuthInfo)
		// 	.uuid(UUID.randomUUID().toString())
		// 	.username(registerReq.getUsername())
		// 	.initialReviewCount(registerReq.getInitialReviewCount())
		// 	.role(UserRole.ROLE_USER)
		// 	.build();
		//
		// String accessToken = jwtUtil.generateAccessToken(userInfo.getUuid(), UserRole.ROLE_USER,
		// 	userInfo.getUsername());
		// String refreshToken = jwtUtil.generateRefreshToken(userInfo.getUuid(), UserRole.ROLE_USER,
		// 	userInfo.getUsername());

		userInfo.updateRefreshToken(refreshToken);

		osAuthInfoRepository.save(osAuthInfo);
		userInfoRepository.save(userInfo);

		AuthRes res = new AuthRes(accessToken, refreshToken, userInfo.getUsername());
		return res;
	}

	/**
	 * 사용자 login 서비스 로직
	 * @param req
	 * @return
	 */
	@Transactional
	public AuthRes login(LoginReq req) {

		OsIdAuthenticationToken authenticationToken = new OsIdAuthenticationToken(req.getOsId());

		OsIdAuthenticationToken osIdAuthenticationToken = (OsIdAuthenticationToken)authenticationManagerBuilder.getObject()
			.authenticate(authenticationToken);

		String uuid = osIdAuthenticationToken.getUserDetails().getUuid();

		UserInfo user = userInfoRepository.findUserInfoByUuid(uuid)
			.orElseThrow(() -> new BusinessException(ResponseCode.LOGIN_FAIL));

		UserRole role = user.getRole();
		if (validOnboardingInfo(user.getStreakInfo())) {
			role = UserRole.ROLE_USER;
		} else {
			role = UserRole.ROLE_SEMI_USER;
		}

		TokenDto tokenDto = jwtUtil.generateTokenDtoWithRole(osIdAuthenticationToken, role);

		user.updateRefreshToken(tokenDto.getRefreshToken());

		return new AuthRes(tokenDto.getAccessToken(), tokenDto.getRefreshToken(),
			osIdAuthenticationToken.getUserDetails().getUsername());

	}

	@Transactional
	public void delete() {
		UserInfo user = securityUtil.getCurrentUsersEntity();
		userInfoRepository.delete(user);
	}

	/**
	 * 온보딩 정보를 전부 가지고 있는지 확인하는 메서드
	 * @param streakInfo
	 * @return
	 */
	private Boolean validOnboardingInfo(StreakInfo streakInfo) {

		if (streakInfo == null || streakInfo.getAspiration() == null || streakInfo.getReviewAt() == null
			|| streakInfo.getWriteLocation() == null) {
			return false;
		}

		return true;
	}
}

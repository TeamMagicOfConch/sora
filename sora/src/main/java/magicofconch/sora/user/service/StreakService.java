package magicofconch.sora.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import magicOfConch.enums.UserRole;
import magicOfConch.user.StreakInfo;
import magicOfConch.user.UserInfo;
import magicofconch.sora.security.dto.res.TokenDto;
import magicofconch.sora.security.jwt.JwtUtil;
import magicofconch.sora.user.api.dto.req.StreakReq;
import magicofconch.sora.user.repository.UserInfoRepository;
import magicofconch.sora.user.util.StreakConverter;
import magicofconch.sora.util.ResponseCode;
import magicofconch.sora.util.SecurityUtil;
import magicofconch.sora.util.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class StreakService {

	private final SecurityUtil securityUtil;
	private final JwtUtil jwtUtil;
	private final UserInfoRepository userInfoRepository;

	/**
	 * semi user가 streak을 등록하는 기능
	 * @param req
	 * @return
	 */
	@Transactional
	public TokenDto upsert(StreakReq req) {
		UserInfo user = securityUtil.getCurrentUsersEntity();

		if (user.getStreakInfo() == null) {
			StreakInfo created = StreakConverter.from(req);
			user.updateStreakInfo(created);
		} else {
			StreakConverter.overwrite(user.getStreakInfo(), req);
		}

		user.updateRole(UserRole.ROLE_SEMI_USER);
		userInfoRepository.save(user).getStreakInfo();

		TokenDto tokenDto = jwtUtil.generateTokenDtoWithRole(user.getUuid(), user.getUsername(), user.getRole());

		return tokenDto;
	}

	@Transactional
	public StreakInfo updateAll(StreakReq req) {
		UserInfo user = securityUtil.getCurrentUsersEntity();

		if (user.getStreakInfo() == null) {
			throw new BusinessException(ResponseCode.STREAK_NOT_REGISTERED);
		}
		StreakConverter.overwrite(user.getStreakInfo(), req);
		return userInfoRepository.save(user).getStreakInfo();
	}
}

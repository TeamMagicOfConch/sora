package magicofconch.sora.security.service;

import java.util.UUID;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import magicofconch.sora.security.dto.req.LoginReq;
import magicofconch.sora.security.dto.res.TokenDto;
import magicofconch.sora.security.jwt.JwtUtil;
import magicofconch.sora.security.os_id.OsIdAuthenticationToken;
import magicofconch.sora.security.dto.req.RegisterReq;
import magicofconch.sora.security.dto.res.AuthRes;
import magicofconch.sora.user.entity.OsAuthInfo;
import magicofconch.sora.user.entity.UserInfo;
import magicofconch.sora.user.enums.UserRole;
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

	public AuthRes registerUser(RegisterReq registerReq){

		if(osAuthInfoRepository.existsByOsId(registerReq.getOsId())){
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
			.role(UserRole.ROLE_USER.getRoleName())
			.build();

		osAuthInfoRepository.save(osAuthInfo);
		userInfoRepository.save(userInfo);

		String accessToken = jwtUtil.generateAccessToken(userInfo.getUuid(), UserRole.ROLE_USER.getRoleName());
		String refreshToken = jwtUtil.generateRefreshToken(userInfo.getUuid(), UserRole.ROLE_USER.getRoleName());

		AuthRes res = new AuthRes(accessToken, refreshToken, userInfo.getUsername());
		return res;
	}


	public AuthRes login(LoginReq req){

		OsIdAuthenticationToken authenticationToken = new OsIdAuthenticationToken(req.getOsId());

		OsIdAuthenticationToken osIdAuthenticationToken = (OsIdAuthenticationToken) authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		TokenDto tokenDto =jwtUtil.generateTokenDto(osIdAuthenticationToken);

		return new AuthRes(tokenDto.getAccessToken(), tokenDto.getRefreshToken(), osIdAuthenticationToken.getUserDetails().getUsername());

	}

	@Transactional
	public void delete(){
		UserInfo user = securityUtil.getCurrentUsersEntity();
		userInfoRepository.delete(user);
	}
}

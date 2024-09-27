package magicofconch.sora.security.service;

import java.util.UUID;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import magicofconch.sora.security.dto.req.LoginReq;
import magicofconch.sora.security.dto.res.TokenDto;
import magicofconch.sora.security.jwt.JwtUtil;
import magicofconch.sora.security.os_id.OsIdAuthenticationToken;
import magicofconch.sora.security.dto.req.RegisterReq;
import magicofconch.sora.security.dto.res.RegisterRes;
import magicofconch.sora.user.entity.OsAuthInfo;
import magicofconch.sora.user.entity.UserInfo;
import magicofconch.sora.user.enums.UserRole;
import magicofconch.sora.user.repository.OsAuthInfoRepository;
import magicofconch.sora.user.repository.UserInfoRepository;
import magicofconch.sora.util.ResponseCode;
import magicofconch.sora.util.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final UserInfoRepository userInfoRepository;
	private final OsAuthInfoRepository osAuthInfoRepository;
	private final JwtUtil jwtUtil;

	public RegisterRes registerUser(RegisterReq registerReq){

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
			.role(UserRole.ROLE_USER.getRoleName())
			.build();

		osAuthInfoRepository.save(osAuthInfo);
		userInfoRepository.save(userInfo);

		String accessToken = jwtUtil.generateAccessToken(userInfo.getUuid(), UserRole.ROLE_USER.getRoleName());
		String refreshToken = jwtUtil.generateRefreshToken(userInfo.getUuid(), UserRole.ROLE_USER.getRoleName());

		RegisterRes res = new RegisterRes(accessToken, refreshToken);
		return res;
	}


	public TokenDto login(LoginReq req){

		OsIdAuthenticationToken authenticationToken = new OsIdAuthenticationToken(req.getOsId());

		OsIdAuthenticationToken osIdAuthenticationToken = (OsIdAuthenticationToken) authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		return jwtUtil.generateTokenDto(osIdAuthenticationToken);

	}
}

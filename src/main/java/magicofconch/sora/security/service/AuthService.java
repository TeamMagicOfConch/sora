package magicofconch.sora.security.service;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import magicofconch.sora.security.dto.req.LoginReq;
import magicofconch.sora.security.dto.res.TokenDto;
import magicofconch.sora.security.jwt.JwtUtil;
import magicofconch.sora.security.os_id.OsIdAuthenticationToken;
import magicofconch.sora.user.enums.UserRole;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final JwtUtil jwtUtil;


	public TokenDto login(LoginReq req){

		OsIdAuthenticationToken authenticationToken = new OsIdAuthenticationToken(req.getOsId());

		OsIdAuthenticationToken osIdAuthenticationToken = (OsIdAuthenticationToken) authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		return jwtUtil.generateTokenDto(osIdAuthenticationToken);

	}
}

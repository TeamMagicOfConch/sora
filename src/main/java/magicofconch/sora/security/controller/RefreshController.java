package magicofconch.sora.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import magicofconch.sora.security.dto.res.TokenDto;
import magicofconch.sora.security.jwt.RefreshTokenService;
import magicofconch.sora.util.Response;

@RestController
@RequiredArgsConstructor
public class RefreshController {
	private final RefreshTokenService refreshTokenService;

	@PostMapping("/user/reissue")
	public Response<TokenDto> reissue(HttpServletRequest httpServletRequest){
		TokenDto tokenDto = refreshTokenService.reissue(httpServletRequest);

		return Response.ok(tokenDto);
	}

}

package magicofconch.sora.security.controller;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import magicofconch.sora.security.dto.req.LoginReq;
import magicofconch.sora.security.dto.res.TokenDto;
import magicofconch.sora.security.jwt.JwtUtil;
import magicofconch.sora.security.service.AuthService;
import magicofconch.sora.util.Response;

@RestController
@RequiredArgsConstructor
public class SecurityController {

	private final AuthService authService;

	@PostMapping("/login")
	public Response<TokenDto>  login(@RequestBody LoginReq req){
		TokenDto tokenDto = authService.login(req);

		return Response.ok(tokenDto);
	}

	@GetMapping("/isthiswork")
	public Response okok (){
		return Response.ok();
	}
}

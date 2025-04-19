package magicofconch.sora.security.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import magicofconch.sora.security.dto.req.LoginReq;
import magicofconch.sora.security.dto.res.TokenDto;
import magicofconch.sora.security.jwt.RefreshTokenService;
import magicofconch.sora.security.service.AuthService;
import magicofconch.sora.security.dto.req.RegisterReq;
import magicofconch.sora.security.dto.res.AuthRes;
import magicofconch.sora.util.Response;

@RestController
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final RefreshTokenService refreshTokenService;

	@PostMapping("/user/register")
	public Response<AuthRes> registerUser(@RequestBody RegisterReq registerReq){
		AuthRes res = authService.registerUser(registerReq);
		return Response.ok(res);
	}

	@PostMapping("/user/login")
	public Response<AuthRes>  login(@RequestBody LoginReq req){
		AuthRes auth = authService.login(req);
		return Response.ok(auth);
	}

	@GetMapping("/user/reissue")
	public Response<TokenDto> reissue(@RequestHeader(value = "Refresh-Token") String refreshToken){
		TokenDto tokenDto = refreshTokenService.reissue(refreshToken);

		return Response.ok(tokenDto);
	}

	@DeleteMapping("/auth/user/delete")
	public Response<Void> delete(){
		authService.delete();
		return Response.ok();
	}

	@GetMapping("/auth/user/isthiswork")
	public Response okok (){
		return Response.ok();
	}
}

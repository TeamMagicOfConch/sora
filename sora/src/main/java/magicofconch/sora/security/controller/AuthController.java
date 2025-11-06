package magicofconch.sora.security.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import magicofconch.sora.security.dto.req.LoginReq;
import magicofconch.sora.security.dto.req.RegisterReq;
import magicofconch.sora.security.dto.res.AuthRes;
import magicofconch.sora.security.dto.res.TokenDto;
import magicofconch.sora.security.jwt.RefreshTokenService;
import magicofconch.sora.security.service.AuthService;
import magicofconch.sora.util.Response;

@RestController
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final RefreshTokenService refreshTokenService;

	@PostMapping("/user/register")
	public Response<AuthRes> registerUser(@RequestBody RegisterReq registerReq) {
		AuthRes res = authService.registerUser(registerReq);
		return Response.ok(res);
	}

	@PostMapping("/user/login")
	@Operation(summary = "유저 로그인", description = "os_id를 통해 로그인을 합니다. 성공시 AT/RT를 반환합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그인 성공"),
		@ApiResponse(responseCode = "404", description = "로그인 실패 실패(os_id에 대한 유저 미확인)")
	})
	public Response<AuthRes> login(@RequestBody @Parameter(description = "로그인 os_id") LoginReq req) {
		AuthRes auth = authService.login(req);
		return Response.ok(auth);
	}

	@GetMapping("/user/reissue")
	public Response<TokenDto> reissue(@RequestHeader(value = "Refresh-Token") String refreshToken) {
		TokenDto tokenDto = refreshTokenService.reissue(refreshToken);

		return Response.ok(tokenDto);
	}

	@DeleteMapping("/auth/user/delete")
	public Response<Void> delete() {
		authService.delete();
		return Response.ok();
	}

	@GetMapping("/auth/user/isthiswork")
	public Response okok() {
		return Response.ok();
	}
}

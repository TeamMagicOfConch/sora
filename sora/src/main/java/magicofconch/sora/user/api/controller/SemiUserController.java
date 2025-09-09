package magicofconch.sora.user.api.controller;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.security.dto.res.TokenDto;
import magicofconch.sora.user.api.dto.req.StreakReq;
import magicofconch.sora.user.service.StreakService;
import magicofconch.sora.util.Response;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/semi/user")
public class SemiUserController {

	private final StreakService streakService;

	@PutMapping("/streak")
	@Operation(summary = "semi-user streak 등록", description = "streak이 등록되지 않은 semi-user의 streak 등록 기능")
	public Response<TokenDto> registerStreak(@Valid @RequestBody StreakReq streakReq) {
		TokenDto response = streakService.upsert(streakReq);

		return Response.ok(response);
	}

}

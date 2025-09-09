package magicofconch.sora.user.api.controller;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import magicofconch.sora.security.dto.res.TokenDto;
import magicofconch.sora.user.api.dto.req.StreakReq;
import magicofconch.sora.user.service.StreakService;
import magicofconch.sora.util.Response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/semi/user")
public class SemiUserController {

	private final StreakService streakService;

	@PutMapping("/streak")
	public Response<?> registerStreak(@Valid @RequestBody StreakReq streakReq) {
		TokenDto response = streakService.upsert(streakReq);

		return Response.ok(response);
	}

}

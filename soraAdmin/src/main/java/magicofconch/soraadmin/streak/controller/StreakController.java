package magicofconch.soraadmin.streak.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import magicofconch.soraadmin.streak.service.StreakService;

@RestController
@RequiredArgsConstructor
public class StreakController {

	private final StreakService streakService;

	@GetMapping
	public void testStreak(@RequestParam String title, String body, String token) throws IOException {
		streakService.sendNotification(title, body, token);
	}

}

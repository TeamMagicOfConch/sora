package magicofconch.sorawebflux.review.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class FcmSimulatorController {

	@PostMapping("/test/fcm/send")
	public Mono<String> simulateFcmSend(@RequestBody String body) {
		return Mono.just("{\"status\":\"ok\"}");
	}
}

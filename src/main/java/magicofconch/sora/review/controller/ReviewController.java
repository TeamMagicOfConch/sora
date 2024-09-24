	package magicofconch.sora.review.controller;

	import java.time.Duration;

	import org.springframework.http.MediaType;
	import org.springframework.web.bind.annotation.PostMapping;
	import org.springframework.web.bind.annotation.RequestBody;
	import org.springframework.web.bind.annotation.RestController;

	import lombok.RequiredArgsConstructor;
	import magicofconch.sora.review.dto.req.SoraReviewReq;
	import magicofconch.sora.review.service.ReviewService;
	import reactor.core.publisher.Flux;

	@RestController
	@RequiredArgsConstructor
	public class ReviewController {

		private final ReviewService reviewService;

		@PostMapping(value = "/test/api/request/review", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
		public Flux<String> requestSora(@RequestBody SoraReviewReq req) {
			Flux<String> feedback = reviewService.requestSora(req);

			return feedback;
		}
	}

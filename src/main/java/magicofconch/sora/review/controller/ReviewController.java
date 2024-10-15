	package magicofconch.sora.review.controller;

	import java.time.Duration;
	import java.util.concurrent.Executors;

	import org.springframework.http.MediaType;
	import org.springframework.web.bind.annotation.GetMapping;
	import org.springframework.web.bind.annotation.PostMapping;
	import org.springframework.web.bind.annotation.RequestBody;
	import org.springframework.web.bind.annotation.RequestParam;
	import org.springframework.web.bind.annotation.RestController;
	import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

	import jakarta.servlet.http.HttpServletResponse;
	import lombok.RequiredArgsConstructor;
	import lombok.extern.slf4j.Slf4j;
	import magicofconch.sora.review.dto.req.SubmitReq;
	import magicofconch.sora.review.dto.res.ReviewRes;
	import magicofconch.sora.review.service.ReviewService;
	import magicofconch.sora.util.Response;
	import reactor.core.publisher.Flux;

	@Slf4j
	@RestController
	@RequiredArgsConstructor
	public class ReviewController {

		private final ReviewService reviewService;

		@GetMapping("/auth/user/api/review/inquiry/month")
		public Response inquiryReview(@RequestParam int year, int month){
			log.info("year = {} , month = {}", year, month);
			return Response.ok(reviewService.inquiryMonthly(year, month));
		}

		@PostMapping(value = "/auth/user/api/review/submit", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
		public SseEmitter submitReview(@RequestBody SubmitReq req, HttpServletResponse response) {

			response.setHeader("X-Accel-Buffering", "no");
			response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
			SseEmitter emitter = new SseEmitter();
			String feedback = reviewService.requestSora(req);

			Executors.newSingleThreadExecutor().submit(() -> {
				try {
					int seq = 0;
					for (char part : feedback.toCharArray()) {
						log.info("[request each char] part: {}", part);
						ReviewRes res = ReviewRes.builder()
							.value(String.valueOf(part))
							.seq(seq)
							.build();

						emitter.send(res);
						seq++;
					}

					emitter.complete();
				} catch (Exception e) {
					log.warn("emitter error");
					emitter.completeWithError(e);
				}
			});

			return emitter;
		}

		@PostMapping(value = "/test/api/request/review", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
		public SseEmitter requestSoraTest(@RequestBody SubmitReq req, HttpServletResponse response) {
			response.setHeader("X-Accel-Buffering", "no");
			response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
			SseEmitter emitter = new SseEmitter();
			String feedback = reviewService.requestSoraTest(req); // 피드백 문자열 가져오기

			Executors.newSingleThreadExecutor().submit(() -> {
				try {
					log.info("[request sora] feedback: {}", feedback);

					int seq = 0;
					// 한 글자씩 보내기 위한 루프
					for (char part : feedback.toCharArray()) {
						// 공백, 개행 포함하여 한 글자씩 처리
						log.info("[request each char] part: {}", part);
						ReviewRes res = ReviewRes.builder()
							.value(String.valueOf(part)) // 한 글자씩 전송
							.seq(seq)
							.build();

						emitter.send(res); // 글자 전송
						seq++;
					}

					emitter.complete(); // 전송 완료
				} catch (Exception e) {
					log.error("SSE 스트리밍 중 오류 발생: {}", e.getMessage());
					emitter.completeWithError(e); // 오류 발생 시 처리
				}
			});

			return emitter; // 클라이언트에 emitter 반환
		}



		@PostMapping(value = "/test/api/request/review/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
		public Flux<String> requestSoraTest2(@RequestBody SubmitReq req, HttpServletResponse response) {
			// Nginx에서 SSE 응답에 대해 버퍼링을 비활성화하기 위한 헤더 추가
			response.setHeader("X-Accel-Buffering", "no");
			response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);

			// ReviewService로부터 피드백 문자열을 받아와서 처리
			String feedback = reviewService.requestSoraTest(req); // 단순 문자열 반환
			log.info("[request sora] feedback: {}", feedback);

			// 공백 기준으로 문자열을 나누고 500ms 간격으로 클라이언트에 스트리밍
			return Flux.fromArray(feedback.split("\\s+"))  // 공백을 기준으로 나눔
				.delayElements(Duration.ofMillis(500));  // 500ms 지연
		}
	}

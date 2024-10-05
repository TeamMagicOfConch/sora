	package magicofconch.sora.review.controller;

	import java.io.IOException;
	import java.io.PrintWriter;
	import java.time.Duration;

	import org.springframework.boot.web.servlet.server.Encoding;
	import org.springframework.http.MediaType;
	import org.springframework.web.bind.annotation.PostMapping;
	import org.springframework.web.bind.annotation.RequestBody;
	import org.springframework.web.bind.annotation.RestController;
	import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

	import jakarta.servlet.http.HttpServletResponse;
	import lombok.RequiredArgsConstructor;
	import lombok.extern.slf4j.Slf4j;
	import magicofconch.sora.review.dto.req.SoraReviewReq;
	import magicofconch.sora.review.service.ReviewService;
	import reactor.core.Disposable;
	import reactor.core.publisher.Flux;

	@Slf4j
	@RestController
	@RequiredArgsConstructor
	public class ReviewController {

		private final ReviewService reviewService;

		@PostMapping(value = "/test/api/request/review", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
		public SseEmitter requestSoraTest(@RequestBody SoraReviewReq req, HttpServletResponse response) {
			// SseEmitter 생성 (타임아웃 설정 가능, 예: 10초)
			SseEmitter sseEmitter = new SseEmitter(10_000L);

			try {
				// Nginx에서 SSE 응답에 대해 버퍼링을 비활성화하기 위한 헤더 추가
				response.setHeader("X-Accel-Buffering", "no");
				response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);

				// ReviewService로부터 feedback 문자열 받기
				String feedback = reviewService.requestSora(req);

				// 공백을 기준으로 feedback 문자열을 단어 단위로 나누기
				String[] words = feedback.split("\\s+");

				// 각 단어를 클라이언트로 전송
				for (String word : words) {
					sseEmitter.send(SseEmitter.event().data(word));  // SSE를 통해 단어 전송
					response.flushBuffer();  // 전송 즉시 데이터가 클라이언트로 전달되도록 버퍼를 플러시

					log.info("[request sora] 전송된 단어: {}", word);

					Thread.sleep(500);  // 500ms 대기 후 다음 데이터 전송
				}

				// 전송이 완료된 후 완료 신호 보내기
				sseEmitter.complete();

			} catch (Exception e) {
				// 예외 발생 시 오류 처리
				sseEmitter.completeWithError(e);
			}

			return sseEmitter;  // SseEmitter 반환
		}


		@PostMapping(value = "/test/api/request/review2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
		public Flux<String> requestSoraTest(@RequestBody SoraReviewReq req) {
			String feedback = reviewService.requestSora(req); // 단순 문자열 반환

			return Flux.fromArray(feedback.split("\\s+")) // 공백을 기준으로 나눔
				.delayElements(Duration.ofMillis(500));// Optional: 스트리밍 효과를 테스트하기 위한 지연
		}

		/*
		@PostMapping(value = "/auth/user/api/review/today", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
		public Flux<String> requestSora(@RequestBody SoraReviewReq req) {
			String feedback = reviewService.requestSoraTest(req); // 단순 문자열 반환

			return Flux.fromArray(feedback.split("\\s+")) // 공백을 기준으로 나눔
				.delayElements(Duration.ofMillis(100)); // Optional: 스트리밍 효과를 테스트하기 위한 지연
		}

		@PostMapping(value = "/auth/user/api/review/test", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
		public Flux<String> requestSoraTestAuth(@RequestBody SoraReviewReq req) {
			String feedback = reviewService.requestSoraTest(req); // 단순 문자열 반환

			return Flux.fromArray(feedback.split("\\s+")) // 공백을 기준으로 나눔
				.delayElements(Duration.ofMillis(100)); // Optional: 스트리밍 효과를 테스트하기 위한 지연
		}*/
	}

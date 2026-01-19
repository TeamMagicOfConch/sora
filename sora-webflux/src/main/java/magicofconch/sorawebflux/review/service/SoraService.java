package magicofconch.sorawebflux.review.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sorawebflux.enums.FeedbackType;
import magicofconch.sorawebflux.review.dto.ReviewRes;
import magicofconch.sorawebflux.review.dto.SaveReq;
import magicofconch.sorawebflux.review.dto.SubmitReq;
import magicofconch.sorawebflux.security.JwtUtil;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class SoraService {

	private static final String DIFY_API_URL = "https://api.dify.ai/v1/chat-messages";
	private final OpenAiChatModel chatClient;
	private final WebClient webClient;
	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper;
	@Value("${prompt.sora-type.T}")
	private String promptAsT;
	@Value("${prompt.sora-type.F}")
	private String promptAsF;
	@Value("${sora.srv.url}")
	private String appServer;
	@Value("${dify.api-key.T}")
	private String difyApiKeyT;
	@Value("${dify.api-key.F}")
	private String difyApiKeyF;

	/**
	 * open-ai 스트리밍 메서드
	 *
	 * @param req
	 * @return
	 */
	public Flux<ReviewRes> streamReview(SubmitReq req, String token) {
		if (token == null || !jwtUtil.validateToken(token) || jwtUtil.isExpired(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
		}

		String username = jwtUtil.getUsername(token);
		String prompt = (req.getType() == FeedbackType.FEELING ? promptAsF : promptAsT)
			.replace("{name}", username) + "\n" + req.getBody();

		AtomicReference<StringBuilder> bufferRef = new AtomicReference<>(new StringBuilder());

		return chatClient.stream(prompt)
			.doOnNext(text -> bufferRef.get().append(text))
			.index()
			.map(tuple -> ReviewRes.builder()
				.seq(tuple.getT1().intValue())
				.value(tuple.getT2())
				.build())
			.doOnComplete(() -> {
				String feedback = bufferRef.get().toString();
				sendToServer(req, feedback, token);
			});
	}

	/**
	 * Dify 스트리밍 메서드
	 *
	 * @param req
	 * @param token
	 * @return
	 */
	public Flux<ReviewRes> streamReviewWithDify(SubmitReq req, String token) {
		if (token == null || !jwtUtil.validateToken(token) || jwtUtil.isExpired(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
		}

		String username = jwtUtil.getUsername(token);
		String apiKey = (req.getType() == FeedbackType.FEELING ? difyApiKeyF : difyApiKeyT);

		// Dify API 요청 바디 구성
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("inputs", Map.of("name", username));
		requestBody.put("query", req.getBody());
		requestBody.put("response_mode", "streaming");
		requestBody.put("conversation_id", "");
		requestBody.put("user", username);
		requestBody.put("auto_save", false);
		requestBody.put("disable_history", true);

		AtomicReference<StringBuilder> bufferRef = new AtomicReference<>(new StringBuilder());
		AtomicReference<Integer> seqRef = new AtomicReference<>(0);

		return webClient.post()
			.uri(DIFY_API_URL)
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(requestBody)
			.retrieve()
			.bodyToFlux(String.class)
			.doOnNext(line -> log.debug("Dify raw response: {}", line))
			.flatMap(data -> {
				try {
					// SSE 형식이면 "data: " 제거
					String jsonData = data.startsWith("data: ") ? data.substring(6).trim() : data.trim();

					if (jsonData.isEmpty() || jsonData.equals("[DONE]")) {
						return Flux.empty();
					}

					JsonNode jsonNode = objectMapper.readTree(jsonData);
					String event = jsonNode.path("event").asText();

					if ("message".equals(event)) {
						String answer = jsonNode.path("answer").asText("");
						if (!answer.isEmpty()) {
							bufferRef.get().append(answer);
							return Flux.just(ReviewRes.builder()
								.seq(seqRef.getAndUpdate(i -> i + 1))
								.value(answer)
								.build());
						}
					} else if ("message_end".equals(event)) {
						log.info("Dify streaming completed");
					}
					return Flux.empty();
				} catch (Exception e) {
					log.error("Error parsing Dify response: {}", data, e);
					return Flux.empty();
				}
			})
			.doOnComplete(() -> {
				String feedback = bufferRef.get().toString();
				log.info("Dify complete feedback: {}", feedback);
				sendToServer(req, feedback, token);
			})
			.doOnError(error -> {
				log.error("Dify streaming error", error);
			});
	}

	/**
	 * 서버에 리뷰 저장
	 *
	 * @param req
	 * @param feedback
	 * @param token
	 */
	private void sendToServer(SubmitReq req, String feedback, String token) {

		log.info(token);

		SaveReq saveReq = new SaveReq(req.getBody(), req.getType(), req.getReviewDate(), feedback);

		webClient.post()
			.uri(appServer)
			.contentType(MediaType.APPLICATION_JSON)
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.bodyValue(saveReq)
			.retrieve()
			.bodyToMono(Void.class)
			.subscribe();
	}

}
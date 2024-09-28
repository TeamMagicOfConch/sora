package magicofconch.sora.review.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.review.dto.req.SoraReviewReq;
import magicofconch.sora.review.enums.FeedbackType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
	private final OpenAiChatModel openAiChatModel;

	@Value("${prompt.sora-type.T}")
	private String promptAsT;

	@Value("${prompt.sora-type.F}")
	private String promptAsF;

	/**
	 * todo : Review Entity 생성 후 저장
	 * @param req : 소라 회고 리뷰 요청
	 * @return : 소라 응답
	 */
	public Flux<String> requestSora(SoraReviewReq req){
		Message userMessage = new UserMessage(req.getBody());
		SystemPromptTemplate systemPromptTemplate;
		FeedbackType requestType = req.getType();

		if(requestType.equals(FeedbackType.FEELING)){
			systemPromptTemplate = new SystemPromptTemplate(promptAsF);
		}
		else{
			systemPromptTemplate = new SystemPromptTemplate(promptAsT);
		}

		// note : 추후 유저 이름 반영을 위해
		//Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", "test_name"));

		Message systemMessage = systemPromptTemplate.createMessage();

		AtomicReference<StringBuilder> fullResponse = new AtomicReference<>(new StringBuilder());

		return openAiChatModel.stream(userMessage, systemMessage)
			.doOnNext(response -> {
				fullResponse.get().append(response); // 전체 응답 누적
			})
			.doOnComplete(() -> {
				// 스트림 완료 시 누적된 응답을 데이터베이스에 저장
				saveToDatabase(fullResponse.get().toString()).subscribe();
			});

	}

	private Mono<Void> saveToDatabase(String fullResponse) {
		// DB 저장 로직
		log.info("ReviewService savetoDatabse = {}", fullResponse);
		return Mono.empty();
	}
}

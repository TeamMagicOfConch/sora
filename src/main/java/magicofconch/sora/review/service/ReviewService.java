package magicofconch.sora.review.service;

import java.util.Map;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import magicofconch.sora.review.dto.req.SoraReviewReq;
import magicofconch.sora.review.enums.ReviewType;

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
	public String requestSora(SoraReviewReq req){
		Message userMessage = new UserMessage(req.getMyWrite());
		SystemPromptTemplate systemPromptTemplate;
		ReviewType requestType = req.getType();

		if(requestType.equals(ReviewType.F)){
			systemPromptTemplate = new SystemPromptTemplate(promptAsF);
		}
		else{
			systemPromptTemplate = new SystemPromptTemplate(promptAsT);
		}

		//todo : context에서 user이름으로 조회 추가하기
		Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", "test_name"));
		String response = openAiChatModel.call(userMessage, systemMessage);

		return response;
	}
}

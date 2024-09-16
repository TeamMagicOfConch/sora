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
import magicofconch.sora.review.enums.FeedbackType;

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
		String response = openAiChatModel.call(userMessage, systemMessage);

		return response;
	}
}

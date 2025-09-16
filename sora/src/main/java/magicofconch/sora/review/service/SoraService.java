package magicofconch.sora.review.service;

import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import magicOfConch.enums.FeedbackType;
import magicOfConch.user.UserInfo;
import magicofconch.sora.review.api.dto.req.SubmitReq;
import magicofconch.sora.review.repository.ReviewRepository;
import magicofconch.sora.util.ResponseCode;
import magicofconch.sora.util.SecurityUtil;
import magicofconch.sora.util.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class SoraService {

	private final OpenAiChatClient chatClient;  // 변경된 부분
	private final ReviewRepository reviewRepository;
	private final SecurityUtil securityUtil;
	private final ReviewService reviewService;

	@Value("${prompt.sora-type.T}")
	private String promptAsT;

	@Value("${prompt.sora-type.F}")
	private String promptAsF;

	/**
	 * review 작성 메서드
	 *
	 * @param req
	 * @return
	 */
	public String requestSora(SubmitReq req) {
		FeedbackType requestType = req.getType();
		UserInfo userInfo = securityUtil.getCurrentUsersEntity();

		// 중복 리뷰 검사
		if (reviewRepository.existsByReviewDateAndUserInfo(req.getReviewDate(), userInfo)) {
			throw new BusinessException(ResponseCode.REVIEW_ALREADY_EXIST);
		}

		// 프롬프트 선택
		String promptTemplate = requestType.equals(FeedbackType.FEELING) ? promptAsF : promptAsT;
		String systemPrompt = promptTemplate.replace("{name}", userInfo.getUsername());

		// 사용자 메시지와 프롬프트 합치기
		String fullPrompt = systemPrompt + "\n" + req.getBody();

		// ChatClient 호출
		String feedback = chatClient.call(fullPrompt);

		reviewService.saveReview(req, feedback, userInfo);

		return feedback;
	}
}

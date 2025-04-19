package magicofconch.sora.review.service;

import lombok.RequiredArgsConstructor;
import magicofconch.sora.review.dto.req.SubmitReq;
import magicOfConch.enums.FeedbackType;
import magicofconch.sora.review.repository.ReviewRepository;
import magicOfConch.user.UserInfo;
import magicofconch.sora.util.ResponseCode;
import magicofconch.sora.util.SecurityUtil;
import magicofconch.sora.util.exception.BusinessException;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SoraService {

    private final OpenAiChatModel openAiChatModel;
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
        Message userMessage = new UserMessage(req.getBody());
        SystemPromptTemplate systemPromptTemplate;
        FeedbackType requestType = req.getType();
        UserInfo userInfo = securityUtil.getCurrentUsersEntity();

        if (reviewRepository.existsByReviewDateAndUserInfo(req.getReviewDate(), userInfo)) {
            throw new BusinessException(ResponseCode.REVIEW_ALREADY_EXIST);
        }

        if (requestType.equals(FeedbackType.FEELING)) {
            systemPromptTemplate = new SystemPromptTemplate(promptAsF);
        } else {
            systemPromptTemplate = new SystemPromptTemplate(promptAsT);
        }

        Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", userInfo.getUsername()));

        reviewService.printDbConnectionStatus("==== [Inside requestSora] DB Connection Status ===="); // 현재 DB 커넥션 개수 출력

        String feedback = openAiChatModel.call(userMessage, systemMessage);
        reviewService.saveReview(req, feedback, userInfo);

        reviewService.printDbConnectionStatus("==== [After requestSora] DB Connection Status ===="); // 현재 DB 커넥션 개수 출력

        return feedback;
    }
}

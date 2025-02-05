package magicofconch.sora.review.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.review.dto.req.SubmitReq;
import magicofconch.sora.review.dto.res.InquiryDayRes;
import magicofconch.sora.review.dto.res.InquiryMonthRes;
import magicofconch.sora.review.entity.Review;
import magicofconch.sora.review.enums.FeedbackType;
import magicofconch.sora.review.repository.ReviewRepository;
import magicofconch.sora.user.entity.UserInfo;
import magicofconch.sora.util.Encryption.EncryptionUtil;
import magicofconch.sora.util.ResponseCode;
import magicofconch.sora.util.SecurityUtil;
import magicofconch.sora.util.exception.BusinessException;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final OpenAiChatModel openAiChatModel;
    private final ReviewRepository reviewRepository;
    private final SecurityUtil securityUtil;
    private final EncryptionUtil encryptionUtil;

    @Value("${prompt.sora-type.T}")
    private String promptAsT;

    @Value("${prompt.sora-type.F}")
    private String promptAsF;

    /**
     * 해당 Month 날짜별
     *
     * @param year
     * @param month
     * @return
     */
    @Transactional
    public List<InquiryMonthRes> inquiryMonthly(int year, int month) {
        List<InquiryMonthRes> inquiryReviewResList = new ArrayList<>();
        UserInfo userInfo = securityUtil.getCurrentUsersEntity();
        List<Review> reviews = reviewRepository.findByUserInfoIdAndUpdatedAtBetween(userInfo.getId(), month, year);

        for (Review review : reviews) {
            InquiryMonthRes res = new InquiryMonthRes(review);
            inquiryReviewResList.add(res);
        }

        return inquiryReviewResList;
    }

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

        String feedback = openAiChatModel.call(userMessage, systemMessage);
        String encryptFeedback;
        String encryptBody;
        try {
            encryptBody = encryptionUtil.encrypt(req.getBody());
            encryptFeedback = encryptionUtil.encrypt(feedback);
        } catch (Exception e) {
            throw new BusinessException(ResponseCode.REVIEW_GENERAL_FAIL);
        }

        Review review = Review.builder()
                .userInfo(userInfo)
                .body(encryptBody)
                .feedbackType(req.getType())
                .feedback(encryptFeedback)
                .reviewDate(req.getReviewDate())
                .build();

        saveToDatabase(review);
        return feedback;
    }

    @Transactional
    public InquiryDayRes inquiryDay(LocalDate date) {
        UserInfo user = securityUtil.getCurrentUsersEntity();

        Review review = reviewRepository.findReviewByReviewDateAndUserInfo(date, user)
                .orElseThrow(() -> new BusinessException(ResponseCode.REVIEW_NOT_EXIST));

        String feedbackPlain;
        String bodyPlain;
        try {
            bodyPlain = encryptionUtil.decrypt(review.getBody());
            feedbackPlain = encryptionUtil.decrypt(review.getFeedback());

        } catch (Exception e) {
            throw new BusinessException(ResponseCode.REVIEW_GENERAL_FAIL);
        }

        return InquiryDayRes.builder()
                .feedback(feedbackPlain)
                .body(bodyPlain)
                .date(review.getReviewDate())
                .build();
    }

    @Transactional
    public void saveToDatabase(Review review) {
        reviewRepository.save(review);
    }

}

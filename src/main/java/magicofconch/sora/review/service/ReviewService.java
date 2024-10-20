package magicofconch.sora.review.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.review.dto.req.SubmitReq;
import magicofconch.sora.review.dto.res.InquiryDayRes;
import magicofconch.sora.review.dto.res.InquiryMonthRes;
import magicofconch.sora.review.entity.Review;
import magicofconch.sora.review.enums.FeedbackType;
import magicofconch.sora.review.repository.ReviewRepository;
import magicofconch.sora.user.entity.UserInfo;
import magicofconch.sora.util.ResponseCode;
import magicofconch.sora.util.SecurityUtil;
import magicofconch.sora.util.exception.BusinessException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
	private final OpenAiChatModel openAiChatModel;
	private final ReviewRepository reviewRepository;
	private final SecurityUtil securityUtil;

	@Value("${prompt.sora-type.T}")
	private String promptAsT;

	@Value("${prompt.sora-type.F}")
	private String promptAsF;

	/**
	 * 해당 Month 날짜별
	 * @param year
	 * @param month
	 * @return
	 */
	public List<InquiryMonthRes> inquiryMonthly(int year, int month){
		List<InquiryMonthRes> inquiryReviewResList = new ArrayList<>();
		UserInfo userInfo = securityUtil.getCurrentUsersEntity();
		List<Review> reviews = reviewRepository.findByUserInfoIdAndUpdatedAtBetween(userInfo.getId(), month, year);

		for(Review review : reviews){
			InquiryMonthRes res = new InquiryMonthRes(review);
			inquiryReviewResList.add(res);
		}

		return inquiryReviewResList;
	}

	/**
	 * review 작성 메서드
	 * @param req
	 * @return
	 */
	public String requestSora(SubmitReq req){
		Message userMessage = new UserMessage(req.getBody());
		SystemPromptTemplate systemPromptTemplate;
		FeedbackType requestType = req.getType();
		UserInfo userInfo = securityUtil.getCurrentUsersEntity();

		if(reviewRepository.existsByReviewDateAndUserInfo(req.getReviewDate(), userInfo)){
			throw new BusinessException(ResponseCode.REVIEW_ALREADY_EXIST);
		}

		if(requestType.equals(FeedbackType.FEELING)){
			systemPromptTemplate = new SystemPromptTemplate(promptAsF);
		}
		else{
			systemPromptTemplate = new SystemPromptTemplate(promptAsT);
		}

		Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", userInfo.getUsername()));

		String feedback = openAiChatModel.call(userMessage, systemMessage);

		Review review = Review.builder()
			.userInfo(userInfo)
			.body(req.getBody())
			.feedbackType(req.getType())
			.feedback(feedback)
			.reviewDate(req.getReviewDate())
			.build();

		saveToDatabase(review);
		return feedback;
	}

	public InquiryDayRes inquiryDay(LocalDate date){
		UserInfo user = securityUtil.getCurrentUsersEntity();

		Review review = reviewRepository.findReviewByReviewDateAndUserInfo(date, user)
			.orElseThrow(() -> new BusinessException(ResponseCode.REVIEW_NOT_EXIST));

		return InquiryDayRes.builder()
			.feedback(review.getFeedback())
			.body(review.getBody())
			.date(review.getReviewDate())
			.build();
	}

	/**
	 * todo : Review Entity 생성 후 저장
	 * @param req : 소라 회고 리뷰 요청
	 * @return : 소라 응답
	 */
	public String requestSoraTest(SubmitReq req){
		Message userMessage = new UserMessage(req.getBody());
		SystemPromptTemplate systemPromptTemplate;
		FeedbackType requestType = req.getType();

		if(requestType.equals(FeedbackType.FEELING)){
			systemPromptTemplate = new SystemPromptTemplate(promptAsF);
		}
		else{
			systemPromptTemplate = new SystemPromptTemplate(promptAsT);
		}

		Message systemMessage = systemPromptTemplate.createMessage();

		String feedback = openAiChatModel.call(userMessage, systemMessage);

		return feedback;
	}

	@Transactional
	public void saveToDatabase(Review review) {
		reviewRepository.save(review);
	}

}

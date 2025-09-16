package magicofconch.sora.review.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicOfConch.review.Review;
import magicOfConch.user.UserInfo;
import magicofconch.sora.review.api.dto.req.SaveReq;
import magicofconch.sora.review.api.dto.req.SubmitReq;
import magicofconch.sora.review.api.dto.res.CursorBaseReviewRes;
import magicofconch.sora.review.api.dto.res.InquiryDayRes;
import magicofconch.sora.review.api.dto.res.InquiryMonthRes;
import magicofconch.sora.review.domain.dto.ReviewItemDto;
import magicofconch.sora.review.repository.ReviewRepository;
import magicofconch.sora.util.Encryption.EncryptionUtil;
import magicofconch.sora.util.ResponseCode;
import magicofconch.sora.util.SecurityUtil;
import magicofconch.sora.util.exception.BusinessException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final SecurityUtil securityUtil;
	private final EncryptionUtil encryptionUtil;
	private final DataSource dataSource;
	private final Integer REVIEW_LIST_SIZE = 10;
	@Value("${prompt.sora-type.T}")
	private String promptAsT;
	@Value("${prompt.sora-type.F}")
	private String promptAsF;

	/**
	 * 해당 Month에 속한 모든 유저별 review 조회
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
	 * review 저장 메서드
	 * @param req
	 */
	@Transactional
	public void saveReview(SaveReq req) {
		String encryptFeedback;
		String encryptBody;
		try {
			encryptBody = encryptionUtil.encrypt(req.getBody());
			encryptFeedback = encryptionUtil.encrypt(req.getFeedback());
		} catch (Exception e) {
			throw new BusinessException(ResponseCode.REVIEW_GENERAL_FAIL);
		}

		UserInfo userInfo = securityUtil.getCurrentUsersEntity();
		Review review = Review.builder()
			.userInfo(userInfo)
			.body(encryptBody)
			.feedbackType(req.getType())
			.feedback(encryptFeedback)
			.reviewDate(req.getReviewDate())
			.build();

		reviewRepository.save(review);
	}

	/**
	 * 날짜별 유저의 review 조회
	 * @param date
	 * @return
	 */
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
	public void saveReview(SubmitReq req, String feedback, UserInfo userInfo) {
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

		reviewRepository.save(review);
	}

	@Transactional
	public CursorBaseReviewRes listByDateDesc(String after) {
		UserInfo user = securityUtil.getCurrentUsersEntity();

		Pageable pageable = PageRequest.of(0, REVIEW_LIST_SIZE + 1);

		LocalDate cursorDate = null;
		if (after != null && !after.isBlank()) {
			try {
				cursorDate = LocalDate.parse(after); // ← 파싱한 값을 대입
			} catch (Exception e) {
				throw new BusinessException(ResponseCode.REVIEW_INQUIRY_PARAM_WRONG);
			}
		} else {
			// after 미지정 시 "오늘 포함"으로 보려면 오늘을 커서로 사용
			cursorDate = LocalDate.now();
		}

		// 포함 조회(<=)
		List<Review> rows =
			reviewRepository.findAllByUserInfo_IdAndReviewDateLessThanEqualOrderByReviewDateDesc(
				user.getId(), cursorDate, pageable);

		boolean hasNext = rows.size() > REVIEW_LIST_SIZE;
		if (hasNext)
			rows = rows.subList(0, REVIEW_LIST_SIZE);

		// nextCursor는 마지막(가장 과거) 날짜 - 1일  → 다음 호출도 <= 규칙 유지, 중복 없음
		String nextCursor = (hasNext && !rows.isEmpty())
			? rows.get(rows.size() - 1).getReviewDate().minusDays(1).toString()
			: null;

		List<ReviewItemDto> items = new ArrayList<>(rows.size());
		for (Review review : rows) {
			String body;
			try {
				body = encryptionUtil.decrypt(review.getBody());
			} catch (Exception e) {
				throw new BusinessException(ResponseCode.REVIEW_GENERAL_FAIL);
			}
			items.add(new ReviewItemDto(review.getFeedbackType(), body, review.getReviewDate()));
		}

		return new CursorBaseReviewRes(items, hasNext, nextCursor);
	}

}

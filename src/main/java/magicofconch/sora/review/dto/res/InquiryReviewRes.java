package magicofconch.sora.review.dto.res;

import lombok.Getter;
import magicofconch.sora.review.entity.Review;
import magicofconch.sora.review.enums.FeedbackType;

@Getter
public class InquiryReviewRes {

	private int day;
	private FeedbackType feedbackType;


	public InquiryReviewRes(Review review){
		this.day = review.getUpdatedAt().getDayOfMonth();
		this.feedbackType = review.getFeedbackType();
	}
}

package magicofconch.sora.review.api.dto.res;

import lombok.Getter;
import magicOfConch.enums.FeedbackType;
import magicOfConch.review.Review;

@Getter
public class InquiryMonthRes {

	private int day;
	private FeedbackType feedbackType;

	public InquiryMonthRes(Review review) {
		this.day = review.getReviewDate().getDayOfMonth();
		this.feedbackType = review.getFeedbackType();
	}
}

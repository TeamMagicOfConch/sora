package magicofconch.sora.review.dto.res;

import lombok.Getter;
import magicofconch.sora.review.entity.Review;
import magicofconch.sora.review.enums.FeedbackType;

@Getter
public class InquiryMonthRes {

	private int day;
	private FeedbackType feedbackType;


	public InquiryMonthRes(Review review){
		this.day = review.getReviewDate().getDayOfMonth();
		this.feedbackType = review.getFeedbackType();
	}
}

package magicofconch.sora.review.dto.req;

import lombok.Getter;
import magicofconch.sora.review.enums.FeedbackType;

@Getter
public class SoraReviewReq {
	private String body;
	private FeedbackType type;
}

package magicofconch.sora.review.dto.req;

import lombok.Getter;
import magicofconch.sora.review.enums.ReviewType;

@Getter
public class SoraReviewReq {
	private String myWrite;
	private ReviewType type;
}
